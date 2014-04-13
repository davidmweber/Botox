package za.co.monadic.botox

import akka.actor._

object AtModem extends App {

  implicit val system = ActorSystem("ModemThingy")
  val modem = system.actorOf(SerialIO("/dev/ttyUSB0"), "USB-3G")

  val r = RSSI("^RSSI:11")
  println(r.get.toDb)
  Thread.sleep(1000)
  modem ! "ate\r"
  modem ! "at^curc=1\r"
  modem ! "at^portsel=1\r"
  modem ! "at+clip=1\r"
  modem ! "AT^CGMM?\r"
}
