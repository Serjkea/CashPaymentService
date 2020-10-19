package cashpaymentservice

import java.nio.file
import java.nio.file.Path

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl.{FileIO, Framing, RunnableGraph, Source}
import akka.util.ByteString
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.avro.AvroOutlet
import cloudflow.streamlets.{RoundRobinPartitioner, StreamletShape, StringConfigParameter}

import scala.concurrent.Future

class FilePaymentsIngress extends AkkaStreamlet {

  val paymentsOut: AvroOutlet[PaymentData] = AvroOutlet[PaymentData]("payments-out").withPartitioner(RoundRobinPartitioner)

  override def shape(): StreamletShape = StreamletShape.withOutlets(paymentsOut)

  val fileName: StringConfigParameter = StringConfigParameter("payments.filename","payments filename",Some("payments.dat"))
  val fileDirectory: StringConfigParameter = StringConfigParameter("payments.directory","payments directory",Some("/test-data"))

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {

    val listFiles: NotUsed ⇒ Source[file.Path, NotUsed] = { _ ⇒
      Directory.ls(Path.of(fileDirectory.key))
    }
    val readFile: Path ⇒ Source[ByteString, Future[IOResult]] = { path: Path ⇒
      FileIO.fromPath(path)
    }

    override def runnableGraph(): RunnableGraph[_] = {
      Source.single(NotUsed)
        .flatMapConcat(listFiles)
        .flatMapConcat(readFile)
        .via(Framing.delimiter(ByteString("\n"), 1024))
        .map(s => PaymentData(s.utf8String))
        .to(plainSink(paymentsOut))
    }

  }

}
