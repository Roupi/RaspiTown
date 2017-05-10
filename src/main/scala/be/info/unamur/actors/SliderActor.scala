package be.info.unamur.actors

import akka.actor.Actor
import akka.actor.ActorRef
import com.phidgets.Phidget
import com.phidgets.InterfaceKitPhidget
import com.phidgets.RFIDPhidget
import com.phidgets.event._ 
import java.time.LocalTime
import scala.collection.mutable.Queue
import com.phidgets.PhidgetException

class SliderActor extends Actor{

var ifk = new InterfaceKitPhidget()
var rfid = new RFIDPhidget()
var ifkinitialised : Boolean = false
var rfidinitialised : Boolean = true
var touchsensorstate : Boolean = false;
var histo = Queue[LocalTime]()

  override def receive: Receive = {
    case "INIT" => {
      println("INIT message recieved")
      println("IFK initialisation..")

        ifk addSensorChangeListener new SensorChangeListener {

           override def sensorChanged(sensorEvent : SensorChangeEvent) {
                if ( (sensorEvent.getIndex() == 4) && (sensorEvent.getValue() == 0 && ifkinitialised) ){ // If value of touchsensor changed (and that is not the first change due to initialization)
                  println("Value of touchSensor changed: " + sensorEvent.getValue());
                  touchsensorstate = true
                }
            }
        }


       ifk addSensorChangeListener new SensorChangeListener {

           override def sensorChanged(sensorEvent : SensorChangeEvent) {
                if ( (sensorEvent.getIndex() == 6) && // If change event came from input 6
                      (4800/(sensorEvent.getValue()-20) > 10) && (4800/(sensorEvent.getValue()-20) < 15) // If object passd between 10 and 15 cm
                      && (histo.isEmpty || LocalTime.now.minusNanos(500000000).isAfter(histo.last)) ){ //If last changed value wasn't in the same 0.5 seconds (counter commute of the sensor)
                  println("Value of IR sensor changed: " + 4800/(sensorEvent.getValue()-20) );
                  histo += LocalTime.now
                }
            }
        }

        ifk addDetachListener new DetachListener {
          override def detached (detachEvent : DetachEvent){
            
          this.synchronized{
            if (ifkinitialised){
              println("IFK has been detached")
              ifk.close
              ifk = new InterfaceKitPhidget()
              ifkinitialised = false
            }
          }

          }
        }


      this.synchronized{
        ifk openAny()
        println("IFK OpenAny : OK")
        println("IFK Waiting for attachement..")
        ifk waitForAttachment()
        println("IFK WaitForAttachement : OK")
        ifkinitialised = true
        println("IFK initialised")
      }

      println("RFID initialisation..")

      rfid addTagGainListener new TagGainListener {
          override def tagGained(tagGainEvent: TagGainEvent): Unit = {
            println("RFID tag gained")
            touchsensorstate = false
          }
      }



        rfid addDetachListener new DetachListener {
          override def detached (detachEvent : DetachEvent){
            
          this.synchronized{
            if (rfidinitialised){
              println("RFID has been detached")
              rfid.close
              rfid = new RFIDPhidget()
              rfidinitialised = false
            }
          }

          }
        }

      this.synchronized{
        rfid openAny()
        rfid waitForAttachment()
        rfid setAntennaOn true
        rfidinitialised = true
        println("RFID initialised") 
      }

      sender ! "IFK and RFID initialised"

    }

    case "TOUCH_SENSOR_STATE" => {

        println("TOUCH_SENSOR_STATE requested")
        sender ! touchsensorstate

    }

    case "TRAFIC_INFO" => {
      System.out.println("TRAFIC INFO Requested")
        val t_ref = LocalTime.now.minusSeconds(10)
        var nb_cars : Int = 0
        for(t <- histo) {
          if (t.isAfter(t_ref)) nb_cars = nb_cars + 1
        }
        
        System.out.println(nb_cars + " cars passed in the last 10 secondes")
        if (nb_cars > 5) {
          sender ! "red"
        } else if (nb_cars > 3) {
          sender ! "orange"
        } else if (nb_cars > 0){
          sender ! "green"
          } else sender ! "grey"
    }

    case "IR_STATE" => {
      println("IR_STATE requested => " + 4800/(ifk.getSensorValue(6)-20))
      sender ! 4800/(ifk.getSensorValue(6)-20)
    }

    case "CLEAN_HISTO" => {
      if (histo.length > 10) histo.dequeue 
      sender ! "histo cleaned"
    }

    case "IFK_ATTACHED" => {
      sender ! ifkinitialised
    }
    case "RFID_ATTACHED" => {
      sender ! rfidinitialised
    }

    case _ => System.out.println("Unexpected msg received in SliderActor")
  }

}
