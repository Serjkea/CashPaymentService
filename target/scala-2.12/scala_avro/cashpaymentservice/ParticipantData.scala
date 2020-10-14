/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package cashpaymentservice

import scala.annotation.switch

case class ParticipantData(var id: Int, var balance: Int) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(0, 0)
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        id
      }.asInstanceOf[AnyRef]
      case 1 => {
        balance
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.id = {
        value
      }.asInstanceOf[Int]
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
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ParticipantData\",\"namespace\":\"cashpaymentservice\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"balance\",\"type\":\"int\"}]}")
}