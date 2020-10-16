package cashpaymentservice

import spray.json._

case object JsonParticipant extends DefaultJsonProtocol {
  implicit val participantFormat: RootJsonFormat[ParticipantData] = jsonFormat(ParticipantData.apply, "nameId", "balance")
}