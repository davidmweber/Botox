package za.co.monadic.botox

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex


/**
 *
 */

trait RegexInterp {
  // creates us a regex string interpolation.
  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.tail.map( _ => "x") : _*)
  }
}

trait AtMessage


case class Ring() extends AtMessage

case class AtOk() extends AtMessage // OK message from the AT command set

case class RSSI(strength: Int) extends AtMessage {
  // Convert the signal strength to dB
  def toDb = if (strength == 99) None else Some(strength * 2 - 113)
}

case class Mode(sys: Int, subSys: Int) extends AtMessage {
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
case class CallerId(number: String) extends AtMessage

object DecodeMessage extends RegexInterp {

  def apply(msg: String) : Option[AtMessage] = msg match {
    case r"\^RSSI:(\d{1,2})${strength}" => Some(RSSI(strength.toInt))
    case "OK" => Some(AtOk())
    case r"\+CLIP: .\+(\d{11})${number}.*" => Some(CallerId(number)) // How do you quote a quote?
    case _ => println(s"unknown AT message received: $msg")
      None
  }
}