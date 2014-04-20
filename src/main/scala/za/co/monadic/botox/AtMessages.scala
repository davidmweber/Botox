package za.co.monadic.botox

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex


trait AtResponse

case class Unknown(msg: String) extends AtResponse

case class Ring() extends AtResponse

case class AtOk() extends AtResponse // OK message from the AT command set

object AtOk {
  def unapply(msg: String) = {
   if (msg == "OK") Some(AtOk) else None
  }
}

case class RSSI(strength: Int) extends AtResponse {
  // Convert the signal strength to dB
  def toDb = if (strength == 99) None else Some(strength * 2 - 113)
}

object RSSI {
  val reg = """\^RSSI:(\d{1,2})""".r
  def unapply(msg: String): Option[Int] = reg findFirstIn msg match {
    case Some(reg(strength)) => Some(strength.toInt)
    case _ =>  None
  }
}

case class Mode(sys: Int, subSys: Int) extends AtResponse {
  def systemMode = sys match {
    case 0 => "No service"
    case 1 => "AMPS"
    case 2 => "CDMA"
    case 3 => "GSM/GPRS"
    case 4 => "HDR"
    case 5 => "WCDMA"
    case 6 => "GPS"
  }

  def subSystemMode = subSys match {
    case 0 => "No service"
    case 1 => "GSM"
    case 2 => "GPRS"
    case 3 => "EDGE"
    case 4 => "WCDMA"
    case 5 => "HSDPA"
    case 6 => "HSUPA"
    case 7 => "HSDPA and HSUPA"
    case 8 => "TD-SCDMA"
    case 9 => "HSPA+"
  }
}

//+CLIP: "+27827718256",145,,,,0
case class CallerId(number: String) extends AtResponse

object CallerId {
  val reg = """\+CLIP: "\+(\d{11})".*""".r
  def unapply(msg: String) =  reg.findFirstIn(msg) match {
    case Some(reg(num)) => Some(num)
    case _ => None
  }
}

object DecodeMessage {

  def apply(msg: String) : AtResponse = msg match {
    case RSSI(r) => RSSI(r)
    case AtOk() => AtOk()
    case CallerId(n) => CallerId(n)
    case m => Unknown(m)
  }
}