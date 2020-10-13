package cashpaymentservice

import cloudflow.akkastream.util.scaladsl.HttpServerLogic
import cloudflow.akkastream.{AkkaServerStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroOutlet
import org.apache.kafka.clients.producer.RoundRobinPartitioner

class ParticipantInitializeIngress extends AkkaServerStreamlet{

  val out = AvroOutlet[ParticipantData]("out").withPartitioner(RoundRobinPartitioner)

  override def shape(): StreamletShape = StreamletShape.withOutlets(out)

  override protected def createLogic(): AkkaStreamletLogic = HttpServerLogic.default(this,out)

}
