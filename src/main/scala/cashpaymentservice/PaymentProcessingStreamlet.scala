package cashpaymentservice

import cloudflow.flink._
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.RichCoMapFunction
import org.apache.flink.streaming.api.scala.DataStream

class PaymentProcessingStreamlet extends FlinkStreamlet{

  val participantIn: AvroInlet[ParticipantData] = AvroInlet[ParticipantData]("participantIn")
  val validPaymentIn: AvroInlet[ValidPayment] = AvroInlet[ValidPayment]("validPaymentIn")

  val paymentStatusOut: AvroOutlet[PaymentStatus] = AvroOutlet[PaymentStatus]("paymentStatusOut")

  override def shape(): StreamletShape = StreamletShape(paymentStatusOut).withInlets(participantIn,validPaymentIn)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val inputParticipant: DataStream[ParticipantData] = readStream(participantIn)
      val inputValidPayment: DataStream[ValidPayment] = readStream(validPaymentIn)

      val outputPaymentStatus: DataStream[PaymentStatus] = inputParticipant.connect(inputValidPayment).map(new MakingPayment).

      writeStream(outputPaymentStatus,paymentStatusOut)
    }
  }

}

class MakingPayment extends RichCoMapFunction[ParticipantData,ValidPayment,PaymentStatus] {

  @transient var mapState: MapState[Int,Int] = _

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    mapState = getRuntimeContext.getMapState(new MapStateDescriptor[Int,Int]("participant", classOf[Int],classOf[Int]))
  }

  override def map1(participant: ParticipantData): PaymentStatus = {
    mapState.put(participant.id,participant.balance)
    PaymentStatus("INFO", s"For participant with ${participant.id} balance updated")
  }

  override def map2(payment: ValidPayment): PaymentStatus = {
    val payer = payment.from.toInt //TODO!!!
    val amount = payment.value
    if (mapState.contains(payer)) {
      val balance: Int = mapState.get(payer)
      if (balance >= amount) {
        mapState.put(payer,balance-amount)
        PaymentStatus("INFO", s"Payment $payment was made!")
      } else {
        PaymentStatus("WARN", s"There is not enough money on the payer's $payer balance!")
      }
    }
    PaymentStatus("WARN", s"The payer $payer was not found!")
  }}
