package be.info.unamur

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.Patterns
import akka.actor._
import akka.actor.Actor
import be.info.unamur.actors.SliderActor
import be.info.unamur.actors.TraficInterpreterActor
import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport
import com.phidgets.InterfaceKitPhidget
import com.phidgets.event.{SensorChangeEvent, SensorChangeListener}
import scala.concurrent.{Await, Future}

class MainServlet extends ScalatraServlet with ScalateSupport {
  before() {
    contentType = "text/html"
  }

  val system = ActorSystem("ActorSystem")
  val sliderActor = system.actorOf(Props[SliderActor], name = "sliderActor")
  implicit val timeout = Timeout(4.second)
  implicit val ec = system.dispatcher
  var sv : Int = 0
  var c0 : String = ""
  var c1 : String = ""
  var c2 : String = ""
  var fkinitialised : Boolean = false
  var rfidinitialised : Boolean = false
  var touchsensorstate : Boolean = false 
  var lastIRstate : Int = -1
  var newIRstate : Int = -2
  var disconnectedG : Boolean = false
  var disconnectedD : Boolean = false



  get("/") {


    if (fkinitialised ==  false) {
      println("Main Asking for INIT") 
      val f: Future[Any] = sliderActor ? "INIT"
      f.onSuccess {
        case s:String => {
          println("s")
          fkinitialised = true
          rfidinitialised = true
          println("fk and rfid has been initialised")
        }
      }
    }

    if (fkinitialised){
    val g: Future[Any] = sliderActor ? "TOUCH_SENSOR_STATE"
    g.onSuccess {
        case s:Boolean => {
          println("TOUCH_SENSOR_STATE is " + s)
          touchsensorstate = s
        }
    }

    val h: Future[Any] = sliderActor ? "TRAFIC_INFO"
    h.onSuccess {
      case s:String => {
        c1 = s
        println("C1 color : " + s)
      }
    }

    val i: Future[Any] = sliderActor ? "IR_STATE" //Gestion du cas de bouchons
    i.onSuccess {
      case s:Int => {
        lastIRstate = newIRstate
        newIRstate = s
        if ( (lastIRstate == newIRstate) && lastIRstate > 10 && lastIRstate < 15) c1 = "red"
      }
    }

    val j: Future[Any] = sliderActor ? "CLEAN_HISTO"
    j.onSuccess {
      case s:String => {
        println(s)
      }
    } 
        
    val k: Future[Any] = sliderActor ? "IFK_ATTACHED"
    k.onSuccess {
      case s:Boolean => {
        println("fkinitialised is set to " + s)
        fkinitialised = s
      }
    } 



    val l: Future[Any] = sliderActor ? "RFID_ATTACHED"
    l.onSuccess {
      case s:Boolean => {
        println("rfidinitialised is set to " + s)
        rfidinitialised = s
      }
    } 

  }



    ssp("/WEB-INF/templates/views/index.ssp", "touchsensorstate" -> touchsensorstate, "c0" -> c0, "c1"-> c1, "c2" -> c2, "disconnectedD" -> !rfidinitialised, "disconnectedG" -> !fkinitialised)
  }
}