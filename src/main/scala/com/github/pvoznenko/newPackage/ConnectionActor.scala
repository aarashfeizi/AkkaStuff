package com.github.pvoznenko.newPackage

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}

import scala.concurrent.Future
import scala.io.StdIn

/**
  * Created by feyzi on 7/23/17.
  */
class ConnectionActor extends Actor {

  case object StartConnection

  case class ConnectWebsite(s: String)

  override def receive = {
    case StartConnection => {
      val requestHandler: HttpRequest => Future[HttpResponse] = {
        case HttpRequest(GET, Uri.Path("/pintpin"), _, _, _) => {

          val c = context.system.actorOf(Props[RequestHandler], "child1")

          val a = c ? OneSlash
          a map { p =>
            p
          }
        }
        case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
          Future {
            HttpResponse(entity = "PONG!")
          }
        case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
          sys.error("BOOM!")

      }


      val bindingFuture = Http().bindAndHandleAsync(requestHandler, "localhost", 8080)
      println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
      StdIn.readLine() // let it run until user presses return
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.shutdown()) // and shutdown when done

    }
  }
}