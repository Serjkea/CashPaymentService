package cashpaymentservice

import akka.event.LoggingAdapter
import akka.stream.scaladsl.{RunnableGraph, Sink}
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.util.scaladsl.Merger
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet

class PaymentLoggingEgress extends AkkaStreamlet{

  val checkIn: AvroInlet[PaymentStatus] = AvroInlet[PaymentStatus]("checkIn")
  val statusIn: AvroInlet[PaymentStatus] = AvroInlet[PaymentStatus]("statusIn")

  override def shape(): StreamletShape = StreamletShape.withInlets(checkIn,statusIn)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {

    val log: LoggingAdapter = system.log

    override def runnableGraph(): RunnableGraph[_] = {
      Merger.source(checkIn,statusIn).map(s => s.infoType match {
        case "WARN" => log.warning(s.message)
        case "INFO" => log.info(s.message)
      }).to(Sink.ignore)
    }
  }


}
