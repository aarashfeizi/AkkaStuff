package com.github.pvoznenko.newPackag

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.github.pvoznenko.newPackage.com.github.pvoznenko._

import scala.concurrent.Future

/**
  * Created by feyzi on 7/23/17.
  */
class RequestHandlerActor extends Actor {

  override def receive: Receive = {
    case OneSlash => {
      val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
        Http().outgoingConnection("beta.pintapin.com")

      def dispatchRequest(request: HttpRequest): Future[HttpResponse] =
        Source.single(request)
          .via(connectionFlow)
          .runWith(Sink.head)

      val aggFut /*: IndexedSeq[String] */ = for {
        p <- dispatchRequest(HttpRequest(uri = "/"))
        oldString <- Unmarshal(p).to[String];
        newString <- changeString(oldString)

      } yield (newString)

      aggFut map { p =>
        sender ! HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`text/html`), p))
        //          HttpResponse(entity = p)
      }


    }
    case Ping =>
    case Crash =>
  }

  def changeString(oldString: String) = {
    val newString = oldString.replace("pintapin", "@#%$!$^")
    Future(newString)
  }

  def parseResponse(p: ResponseEntity): Future[ResponseEntity] = {

    val oldString = Unmarshal(p).to[String]

    val newString = oldString.map(p => p.replace("pintapin", "@@@"))

    val newP = Marshal(newString).to[ResponseEntity]

    newP
  }
}
