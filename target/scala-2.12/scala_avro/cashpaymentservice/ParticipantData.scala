/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package cashpaymentservice

import scala.annotation.switch

case class ParticipantData(var nameId: String, var balance: Int) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this("", 0)
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        nameId
      }.asInstanceOf[AnyRef]
      case 1 => {
        balance
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.nameId = {
        value.toString
      }.asInstanceOf[String]
      case 1 => this.balance = {
        value
      }.asInstanceOf[Int]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = ParticipantData.SCHEMA$
}

object ParticipantData {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ParticipantData\",\"namespace\":\"cashpaymentservice\",\"fields\":[{\"name\":\"nameId\",\"type\":\"string\"},{\"name\":\"balance\",\"type\":\"int\"}]}")
}