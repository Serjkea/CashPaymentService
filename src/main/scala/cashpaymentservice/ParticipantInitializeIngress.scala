package cashpaymentservice

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._

import cloudflow.streamlets._
import cloudflow.streamlets.avro._

import JsonParticipant._

class ParticipantInitializeIngress extends AkkaServerStreamlet{

  val participantsOut: AvroOutlet[ParticipantData] = AvroOutlet[ParticipantData]("participantsOut").withPartitioner(RoundRobinPartitioner)

  final override def shape(): StreamletShape = StreamletShape.withOutlets(participantsOut)

  final override protected def createLogic(): AkkaStreamletLogic = HttpServerLogic.default(this,participantsOut)

}
