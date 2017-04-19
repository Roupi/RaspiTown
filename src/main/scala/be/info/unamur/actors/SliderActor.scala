package be.info.unamur.actors

import akka.actor.Actor
import com.phidgets.InterfaceKitPhidget

 class SliderActor extends Actor{



  override def receive: Receive = {
    case "GET_VALUE" => {
      System.out.println("GET_VALUE msg received")
      val ifk = new InterfaceKitPhidget()
      ifk openAny()
      System.out.println("OpenAny : OK")
      System.out.println("Waiting for attachement..")
      ifk waitForAttachment()
      System.out.println("WaitForAttachement : OK")
      sender ! ifk.getSensorValue(6)
      ifk.close()
    }
    case _ => System.out.println("Unexpected msg received in SliderActor")
  }


}
