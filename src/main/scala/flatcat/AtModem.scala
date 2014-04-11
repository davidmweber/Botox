package flatcat

import gnu.io.{SerialPortEvent, SerialPortEventListener, SerialPort, CommPortIdentifier}
import java.io.{OutputStream, InputStream}


class SerialIO(portName :String) {

  val portIdentifier: CommPortIdentifier = CommPortIdentifier.getPortIdentifier(portName)
  if (portIdentifier.isCurrentlyOwned) throw new RuntimeException("Error: Port is currently in use")
  val port = portIdentifier.open(this.getClass.getName, 2000)

  sys.addShutdownHook { // Does not get called when you run from the IDE
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

  private def toByteArray(s: String): Array[Byte] = {
    val b = new Array[Byte](s.length)
    for (i <- 0 until s.length ) {
      b(i) = s(i).toByte
    }
    b
  }

  def write(message: String): Unit = {
    out.write(toByteArray(message))
  }

  class EventListener(in: InputStream ) extends  SerialPortEventListener {
    override def serialEvent(event: SerialPortEvent): Unit = {
       while(in.available() > 0) {
         print(in.read().toChar)
       }
    }
  }
}

object SerialIO {
  def apply(portName :String) = new SerialIO(portName)
}

object AtModem extends App {
  val sio = SerialIO("/dev/ttyUSB0")
  Thread.sleep(1000)
  sio.write("AT\r")
  Thread.sleep(1000)
  sio.write("AT\r")
  Thread.sleep(1000)
  sio.write("AT\r")
}
