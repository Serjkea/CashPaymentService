package cashpaymentservice

import java.io.{File, FileNotFoundException}
import java.nio.file.Path

import akka.stream.scaladsl.{FileIO, Framing, RunnableGraph}
import akka.util.ByteString
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.avro.AvroOutlet
import cloudflow.streamlets.{RoundRobinPartitioner, StreamletShape}

class FilePaymentsIngress extends AkkaStreamlet {

  val paymentsOut: AvroOutlet[PaymentData] = AvroOutlet[PaymentData]("paymentsOut").withPartitioner(RoundRobinPartitioner)

  override def shape(): StreamletShape = StreamletShape.withOutlets(paymentsOut)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {

    val fileDirectory: String = system.settings.config.getString("payments.directory")
    val fileName:String = system.settings.config.getString("payments.filename")

    def paymentsPath(fileDirectory: String, fileName: String): Path = {
      val paymentsPath = fileDirectory + File.pathSeparator + fileName
      val paymentsFile = new File(paymentsPath)
      if (paymentsFile.exists)
        paymentsFile.toPath
      else
        throw new FileNotFoundException(s"File with directory $paymentsPath not found")
    }

    override def runnableGraph(): RunnableGraph[_] =
      FileIO.fromPath(paymentsPath(fileDirectory,fileName))
        .via(Framing.delimiter(ByteString("\n"), 1024))
        .map(s => PaymentData(s.utf8String))
        .to(plainSink(paymentsOut))
  }

}
