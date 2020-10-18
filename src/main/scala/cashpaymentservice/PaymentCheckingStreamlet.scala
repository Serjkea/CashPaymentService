package cashpaymentservice

import cloudflow.flink._
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag}
import org.apache.flink.util.Collector

class PaymentCheckingStreamlet extends FlinkStreamlet {

  @transient val paymentsIn: AvroInlet[PaymentData] = AvroInlet[PaymentData]("payments-in")

  @transient val checkStatusOut: AvroOutlet[PaymentStatus] = AvroOutlet[PaymentStatus]("check-status-out")
  @transient val validPaymentsOut: AvroOutlet[ValidPayment] = AvroOutlet[ValidPayment]("valid-payments-out")

  override def shape(): StreamletShape = StreamletShape(paymentsIn).withOutlets(checkStatusOut,validPaymentsOut)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {

      val inputPayment: DataStream[PaymentData] = readStream(paymentsIn)

      val outputTag = new OutputTag[PaymentStatus]("warning-branch")

      val outputValidPayment = inputPayment.process(new PaymentValidationProcess(outputTag))

      val outputPaymentStatus = outputValidPayment.getSideOutput(outputTag)

      writeStream(validPaymentsOut,outputValidPayment)
      writeStream(checkStatusOut,outputPaymentStatus)

    }
  }

  class PaymentValidationProcess(outputTag: OutputTag[PaymentStatus]) extends ProcessFunction[PaymentData,ValidPayment] {
    override def processElement(paymentData: PaymentData, ctx: ProcessFunction[PaymentData, ValidPayment]#Context, out: Collector[ValidPayment]): Unit = {
      if (isValid(paymentData)) {
        out.collect(buildValidPayment(paymentData))
      } else
        ctx.output(outputTag, PaymentStatus("WARN", s"Payment ${paymentData.payment} is not valid!"))
    }
  }

  def buildValidPayment(inputPayment: PaymentData): ValidPayment = {
    val pattern = "\\w+".r
    val fields = pattern.findAllIn(inputPayment.payment).toSeq
    ValidPayment(fields(0),fields(1),fields(2).toInt)
  }

  def isValid(inputPayment: PaymentData): Boolean = {
    val mask = "<\\w+> -> <\\w+>: <\\d+>".r
    mask.findAllMatchIn(inputPayment.payment).nonEmpty
  }

}
