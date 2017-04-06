package be.info.unamur

import javax.servlet.ServletConfig

import akka.actor.{ActorRef, ActorSystem}
import com.phidgets.RFIDPhidget
import com.phidgets.InterfaceKitPhidget
import com.phidgets.event.{TagGainEvent, TagGainListener}
import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport


class ActorsServlet(system: ActorSystem, servoMotorActor: ActorRef) extends ScalatraServlet with
  ScalateSupport {

  val rfid = new RFIDPhidget()
  rfid addTagGainListener new TagGainListener {
    override def tagGained(tagGainEvent: TagGainEvent): Unit = {
      servoMotorActor ! "OPEN_THE_BARRIER"
    }
  }

  get("/init") {
    rfid openAny()
    rfid waitForAttachment()
    rfid setAntennaOn true
  }
}
