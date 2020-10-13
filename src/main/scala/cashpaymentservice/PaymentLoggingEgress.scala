package cashpaymentservice

import akka.stream.scaladsl.RunnableGraph
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet

class PaymentLoggingEgress extends AkkaStreamlet{

  val checkIn = AvroInlet[PaymentStatus]("checkIn")
  val statusIn = AvroInlet[PaymentStatus]("statusIn")

  override def shape(): StreamletShape = StreamletShape.withInlets(checkIn,statusIn)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {
    override def runnableGraph(): RunnableGraph[_] = ???
  }
  }

}
