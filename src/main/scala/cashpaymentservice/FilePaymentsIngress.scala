package cashpaymentservice

import java.nio.file.Path

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Framing, RunnableGraph, Source}
import akka.util.ByteString
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.avro.AvroOutlet
import cloudflow.streamlets.{RoundRobinPartitioner, StreamletShape, StringConfigParameter}

import scala.concurrent.Future

class FilePaymentsIngress extends AkkaStreamlet {

  val paymentsOut: AvroOutlet[PaymentData] =
    AvroOutlet("payments-out").withPartitioner(RoundRobinPartitioner)

  override def shape(): StreamletShape = StreamletShape.withOutlets(paymentsOut)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {

    override def runnableGraph(): RunnableGraph[_] = {

      val fileName =
        StringConfigParameter("payments.filename", "payments filename", Some("payments.dat")).getValue(context)
      val fileDirectory =
        StringConfigParameter("payments.directory", "payments directory", Some("./test-data/")).getValue(context)

      Source
        .single(NotUsed)
        .flatMapConcat(_ => FileIO.fromPath(Path.of(s"$fileDirectory$fileName")))
        .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))
        .map(bs => PaymentData(bs.utf8String))
        .to(plainSink(paymentsOut))
    }

  }

}
