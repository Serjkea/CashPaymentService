package cashpaymentservice

object PaymentLoggingMessage {

  case class WARN(message: String)
  case class INFO(message: String)

}
