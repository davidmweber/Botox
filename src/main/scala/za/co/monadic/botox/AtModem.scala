package za.co.monadic.botox

import akka.actor._

object AtModem extends App {

  implicit val system = ActorSystem("ModemThingy")
  val modem = system.actorOf(SerialIO("/dev/ttyUSB0"), "USB-3G")

  val r = DecodeMessage("^RSSI:12")
  println(r)
  val n = DecodeMessage("+CLIP: \"+22222222223\",145,,,,0")
  println(n)
  println(DecodeMessage("OK"))
  Thread.sleep(1000)
//  modem ! "ate\r"
  modem ! "at^curc=1\r"
  modem ! "at^portsel=1\r"
  modem ! "at+clip=1\r"
}
