package be.info.unamur

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.Patterns
import akka.actor._
import be.info.unamur.actors.SliderActor
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
  implicit val timeout = Timeout(3.second)
  implicit val ec = system.dispatcher
  var sv : Int = 0


  get("/") {

    val f: Future[Any] = sliderActor ? "GET_VALUE"
    f.onSuccess {
      case s:Int => {
        sv = s
        println("sliderActor responded " + s)
      }
    }
    ssp("/WEB-INF/templates/views/index.ssp", "sensor_value" -> sv)
  }
}