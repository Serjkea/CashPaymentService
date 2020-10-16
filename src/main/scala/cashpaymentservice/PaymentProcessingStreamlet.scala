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

  @transient val participantsIn: AvroInlet[ParticipantData] = AvroInlet[ParticipantData]("participants-in")
  @transient val validPaymentsIn: AvroInlet[ValidPayment] = AvroInlet[ValidPayment]("valid-payments-in")

  @transient val processStatusOut: AvroOutlet[PaymentStatus] = AvroOutlet[PaymentStatus]("process-status-out")

  override def shape(): StreamletShape = StreamletShape(processStatusOut).withInlets(participantsIn,validPaymentsIn)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val inputParticipant: DataStream[ParticipantData] = readStream(participantsIn)
      val inputValidPayment: DataStream[ValidPayment] = readStream(validPaymentsIn)

      val outputPaymentStatus: DataStream[PaymentStatus] = inputParticipant.connect(inputValidPayment).map(new MakingPayment)
      writeStream(processStatusOut,outputPaymentStatus)
    }
  }

}

class MakingPayment extends RichCoMapFunction[ParticipantData,ValidPayment,PaymentStatus] {

  @transient var mapState: MapState[String, Int] = _

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    mapState = getRuntimeContext.getMapState(new MapStateDescriptor[String, Int]("participant", classOf[String], classOf[Int]))
  }

  override def map1(participant: ParticipantData): PaymentStatus = {
    mapState.put(participant.nameId, participant.balance)
    PaymentStatus("INFO", s"For participant ${participant.nameId} balance updated")
  }

  override def map2(payment: ValidPayment): PaymentStatus = makePayment(payment)

  def makePayment(payment: ValidPayment): PaymentStatus = {
    if (hasParticipants(payment.from, payment.recipient)) {
      if (isEnoughBalance(payment.from, payment.value))
        updateBalance(payment.from, payment.recipient,payment.value)
      else
        PaymentStatus("WARN", s"There is not enough money on the payer's ${payment.from} balance!")
    }
    PaymentStatus("WARN", s"Transfer is not possible! Check the participants")
  }

  def hasParticipants(payerId : String, recipientId: String): Boolean = mapState.contains(payerId) && mapState.contains(recipientId)

  def isEnoughBalance(payerId: String, amount:Int): Boolean = mapState.get(payerId) >= amount

  def updateBalance(payerId: String, recipientId: String, amount: Int): PaymentStatus = {
    mapState.put(payerId, mapState.get(payerId) - amount)
    mapState.put(recipientId, mapState.get(recipientId) + amount)
    PaymentStatus("INFO", s"Payment from $payerId to $recipientId completed successfully!")
  }

}
