package cashpaymentservice

import akka.event.LoggingAdapter
import akka.stream.scaladsl.{RunnableGraph, Sink}
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.util.scaladsl.Merger
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet

class PaymentLoggingEgress extends AkkaStreamlet{

  val checkStatusIn: AvroInlet[PaymentStatus] = AvroInlet[PaymentStatus]("check-status-in")
  val processStatusIn: AvroInlet[PaymentStatus] = AvroInlet[PaymentStatus]("process-status-in")

  override def shape(): StreamletShape = StreamletShape.withInlets(checkStatusIn,processStatusIn)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {

    val log: LoggingAdapter = system.log

    override def runnableGraph(): RunnableGraph[_] = {
      Merger.source(checkStatusIn,processStatusIn).map(s => s.infoType match {
        case "WARN" => log.warning(s.message)
        case "INFO" => log.info(s.message)
      }).to(Sink.ignore)
    }
  }


}
