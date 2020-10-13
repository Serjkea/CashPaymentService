package cashpaymentservice

import cloudflow.flink._
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}

class PaymentCheckingStreamlet extends FlinkStreamlet {

  val paymentIn = AvroInlet[PaymentData]("paymentIn")

  val paymentStatusOut = AvroOutlet[PaymentStatus]("paymentStatusOut")
  val validPaymentOut = AvroOutlet[ValidPayment]("validPaymentOut")

  override def shape(): StreamletShape = StreamletShape(paymentIn).withOutlets(paymentStatusOut,validPaymentOut)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {

    }
  }

}
