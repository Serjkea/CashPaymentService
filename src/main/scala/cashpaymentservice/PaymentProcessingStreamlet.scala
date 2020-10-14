package cashpaymentservice

import cloudflow.flink._
import cloudflow.streamlets.StreamletShape

class PaymentProcessingStreamlet extends FlinkStreamlet{

  val participantIn = ??? //AvroInlet[ParticipantData]("particioantIn")
  val validPaymentIn = ??? //AvroInlet[ValidPayment]("validPaymentIn")

  val paymentStatusOut = ??? //AvroOutlet[PaymentStatus]("paymentStatusOut")

  override def shape(): StreamletShape = ??? //StreamletShape(paymentStatusOut).withInlets(participantIn,validPaymentIn)

  override protected def createLogic(): FlinkStreamletLogic = ??? // new FlinkStreamletLogic() {
//    override def buildExecutionGraph(): Unit = {
//
//    }
//  }

}
