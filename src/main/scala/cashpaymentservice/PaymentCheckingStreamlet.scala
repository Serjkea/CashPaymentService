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

      val outputTag = new OutputTag[PaymentStatus]("warning-branch")

      val inputPayment: DataStream[PaymentData] = readStream(paymentsIn)
      val outputValidPayment = inputPayment.process((paymentData: PaymentData, ctx: ProcessFunction[PaymentData, ValidPayment]#Context, out: Collector[ValidPayment]) => {
        val payment = buildValidPayment(paymentData)
        if (isValidPayment(payment))
          out.collect(payment)
        else
          ctx.output(outputTag, PaymentStatus("WARN", s"Payment ${paymentData.payment} is not valid!"))
      })

      val outputPaymentStatus = outputValidPayment.getSideOutput(outputTag)

      writeStream(validPaymentsOut,outputValidPayment)
      writeStream(checkStatusOut,outputPaymentStatus)

    }
  }

  def buildValidPayment(inputPayment: PaymentData): ValidPayment = {
    val wordPattern = "\\w+".r
    val namePattern = "[a-zA-Z]+".r
    val numberPattern = "[0-9]+".r
    val fields = (wordPattern findAllIn inputPayment.payment).toSeq
    if ((fields.size == 3) && fields(0).matches(namePattern.regex) && fields(1).matches(namePattern.regex) && fields(2).matches(numberPattern.regex))
      ValidPayment(fields(0),fields(1),fields(2).toInt)
    else
      ValidPayment("","",-1)
  }

  def isValidPayment(payment: ValidPayment): Boolean = payment.value != -1

}
