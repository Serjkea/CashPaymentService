package cashpaymentservice

import java.nio.file.Path

import akka.stream.scaladsl.{FileIO, Framing, RunnableGraph, Sink}
import akka.util.ByteString
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.{RoundRobinPartitioner, StreamletShape}
import cloudflow.streamlets.avro.AvroOutlet


class FilePaymentsIngress extends AkkaStreamlet {

  val out = AvroOutlet[PaymentData]("out").withPartitioner(RoundRobinPartitioner)

  override def shape(): StreamletShape = StreamletShape.withOutlets(out)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {
    override def runnableGraph(): RunnableGraph[_] =
      FileIO.fromPath(Path.of("/test-data/payments.dat"))
        .via(Framing.delimiter(ByteString("\n"), 1024))
        .map(_.utf8String)
        .to(plainSink(out))
  }

}
