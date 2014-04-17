package za.co.monadic.botox

import gnu.io.{SerialPortEvent, SerialPortEventListener, SerialPort, CommPortIdentifier}
import java.io.{OutputStream, InputStream}
import akka.actor._
import akka.util.ByteString

class SerialIO(portName: String) extends Actor with ActorLogging {

  val portIdentifier: CommPortIdentifier = CommPortIdentifier.getPortIdentifier(portName)
  if (portIdentifier.isCurrentlyOwned) throw new RuntimeException("Error: Port is currently in use")
  val port = portIdentifier.open(this.getClass.getName, 2000)

  sys.addShutdownHook {
    // Does not get called when you run from the IDE
    println("Closing down serial port")
    port.removeEventListener()
    port.close()
  }

  port match {
    case serialPort: SerialPort =>
      serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
      val in: InputStream = serialPort.getInputStream
      serialPort.addEventListener(new EventListener(in))
      serialPort.notifyOnDataAvailable(true)
    case unknown =>
      throw new RuntimeException(s"Error: Only serial ports are handled here: We got ${unknown.getClass.getCanonicalName}")
  }

  var out: OutputStream = port.getOutputStream

  private def write(message: String): Unit = {
    out.write(message.getBytes("UTF-8"))
  }

  class EventListener(in: InputStream) extends SerialPortEventListener {
    var ret = ByteString.newBuilder

    override def serialEvent(event: SerialPortEvent): Unit = {
      this.synchronized { // Better safe than sorry
        while (in.available() > 0) {
          val t: Byte = in.read().toByte
          t match {
            case 13 => // CR
              if (ret.length > 0) {
                self ! ret.result()
                ret.clear()
              }
            case 10 => // lF
            case _ => ret = ret += t
          }
        }
      }
    }
  }

  def receive = {
    case s: String => write(s)
    case b: ByteString =>
      val message = b.decodeString("UTF-8")
      DecodeMessage(message) match {
        case Some(m:AtMessage) => println( s"$message => $m}")
        case None => s"Unrecognised message: $message"
      }

  }
}

object SerialIO {
  def apply(portName: String): Props = Props(classOf[SerialIO], portName)
}
