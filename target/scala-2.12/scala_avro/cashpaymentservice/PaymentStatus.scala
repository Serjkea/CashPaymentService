/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package cashpaymentservice

import scala.annotation.switch

case class PaymentStatus(var infoType: String, var message: String) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this("", "")
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        infoType
      }.asInstanceOf[AnyRef]
      case 1 => {
        message
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.infoType = {
        value.toString
      }.asInstanceOf[String]
      case 1 => this.message = {
        value.toString
      }.asInstanceOf[String]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = PaymentStatus.SCHEMA$
}

object PaymentStatus {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PaymentStatus\",\"namespace\":\"cashpaymentservice\",\"fields\":[{\"name\":\"infoType\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"}]}")
}