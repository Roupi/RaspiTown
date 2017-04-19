package be.info.unamur.actors

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor._
import scala.concurrent.{Await, Future} 
import akka.pattern.Patterns
import com.phidgets.InterfaceKitPhidget
import java.time.LocalTime
import scala.collection.mutable.Queue
import scala.util.Random

 class TraficInterpreterActor(ActorName: String) extends Actor{

    val rand = Random

    var histo = Queue[LocalTime](
      LocalTime.parse("10:15:30"),
      LocalTime.parse("10:14:20"),
      LocalTime.parse("10:13:30"),
      LocalTime.parse("10:13:20"),
      LocalTime.parse("10:13:10"),
      LocalTime.parse("10:13:05"),
      LocalTime.parse("10:12:50"),
      LocalTime.parse("10:12:45"))


  override def receive: Receive = {

    case "TRAFIC_INFO" => {
      System.out.println("TRAFIC INFO Requested")
        histo.dequeue
        histo += LocalTime.parse("10:50:30").minusMinutes(rand.nextInt(50))
        val t_ref = LocalTime.parse("10:23:30").minusMinutes(10)
        var nb_cars : Int = 0
        for(t <- histo) {
          System.out.println(ActorName + " " + t)
          if (t.isAfter(t_ref)) nb_cars = nb_cars + 1
        }
        
        System.out.println(nb_cars + " cars passed in the last 10 minutes in front of " + ActorName)
        if (nb_cars > 7) {
          sender ! "red"
        } else if (nb_cars > 4) {
          sender ! "orange"
        } else sender ! "green"
    }

    case _ => System.out.println("Unexpected msg received in TraficInterpreterActor")
  }


}