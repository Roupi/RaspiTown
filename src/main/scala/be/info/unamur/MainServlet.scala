package be.info.unamur

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.Patterns
import akka.actor._
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
  val rocco = system.actorOf(Props(new TraficInterpreterActor("rocco")), name = "rocco")
  val freddi = system.actorOf(Props(new TraficInterpreterActor("freddi")), name = "freddi")
  val michel = system.actorOf(Props(new TraficInterpreterActor("michel")), name = "michel")
  implicit val timeout = Timeout(4.second)
  implicit val ec = system.dispatcher
  var sv : Int = 0
  var c0 : String = ""
  var c1 : String = ""
  var c2 : String = ""


  get("/") {

/*    val f: Future[Any] = sliderActor ? "GET_VALUE"
    f.onSuccess {
      case s:Int => {
        sv = s
        println("sliderActor responded " + s)
      }
    }*/

        val g: Future[Any] = rocco ? "TRAFIC_INFO"
    g.onSuccess {
      case s:String => {
        c0 = s
        println("rocco responded " + s + " for 0")
      }
    }

        val h: Future[Any] = freddi ? "TRAFIC_INFO"
    h.onSuccess {
      case s:String => {
        c1 = s
        println("freddi responded " + s + " for 1")
      }
    }

        val i: Future[Any] = michel ? "TRAFIC_INFO"
    i.onSuccess {
      case s:String => {
        c2 = s
        println("michel responded " + s + " for 2")
      }
    }

    ssp("/WEB-INF/templates/views/index.ssp", "sensor_value" -> sv, "c0" -> c0, "c1"-> c1, "c2" -> c2)
  }
}