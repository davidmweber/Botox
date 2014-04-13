package za.co.monadic.botox

import scala.util.{Failure, Success, Try}

/**
 *
 */
trait AtMessage

//+CLIP: "+27827718256",145,,,,0
case class Ring(clid: String) extends AtMessage

case class RSSI(strength: Int) extends AtMessage  {
  // Convert the signal strength to dB
  def toDb = if (strength == 99) None else Some(strength * 2 - 113)
}

object RSSI {
  val re = """\^RSSI:(\d{1,2})""".r
  // Decodes messages of the form ^RSSI:11
  def apply(message: String): Option[RSSI] = Try(message) match {
    case Success(re(str)) => Some(new RSSI(str.toInt))
    case Failure(e) => None
  }
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
