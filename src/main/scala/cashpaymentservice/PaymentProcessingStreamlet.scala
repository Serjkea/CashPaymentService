package cashpaymentservice

import cloudflow.flink._
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor, ValueState}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.DataStream

class PaymentProcessingStreamlet extends FlinkStreamlet{

  val participantIn = AvroInlet[ParticipantData]("particioantIn")
  val validPaymentIn = AvroInlet[ValidPayment]("validPaymentIn")

  val paymentStatusOut = AvroOutlet[PaymentStatus]("paymentStatusOut")

  override def shape(): StreamletShape = StreamletShape(paymentStatusOut).withInlets(participantIn,validPaymentIn)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val inputParticipant: DataStream[ParticipantData] = readStream(participantIn)
      val inputValidPayment: DataStream[ValidPayment] = readStream(validPaymentIn)

      inputParticipant.keyBy(_.id).map(new UploadParticipantData)

      val outputPaymentStatus: DataStream[PaymentStatus] = inputValidPayment.map(new RichMapFunction[ValidPayment] {

        override def map(value: ValidPayment): PaymentStatus = {

        }
      })

      writeStream(outputPaymentStatus,paymentStatusOut)
    }
  }

}

class UploadParticipantData extends RichMapFunction[ParticipantData] {

  @transient var mapState: MapState[Int, Int] = _

  override def open(params: Configuration): Unit = {
    super.open(params)
    mapState = getRuntimeContext.getMapState(new MapStateDescriptor[Int, Int]("participant", classOf[Int], classOf[Int]))
  }

  override def map(value: ParticipantData): Unit = {
    if (!mapState.contains(value.id))
      mapState.put(value.id, value.balance)
    else if (mapState.get(value.id) != value.balance)
      mapState.put(value.id, value.balance)
  }
}
