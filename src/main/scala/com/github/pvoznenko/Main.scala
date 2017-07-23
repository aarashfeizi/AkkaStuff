
package com.github.pvoznenko

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import scala.io.StdIn

object WebServer {

  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future map/flatmap in the end
    implicit val executionContext = system.dispatcher

    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
        HttpResponse(entity = HttpEntity(
          ContentType(MediaTypes.`text/html`, HttpCharsets.`UTF-8`),
          "<html><body>Hello world!</body></html>"))


      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
        HttpResponse(entity = "PONG!")

      case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
        sys.error("BOOM!")

//      case r: HttpRequest =>
//        r.discardEntityBytes() // important to drain incoming HTTP Entity stream
//        HttpResponse(404, entity = "Unknown resource!")
    }

    val bindingFuture = Http().bindAndHandleSync(requestHandler, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done

  }
}


/*import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, ContentTypes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.util.Random
import scala.io.StdIn

object WebServer {

  def main(args: Array[String]) {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    // streams are re-usable so we can define it here
    // and use it for every request
    val numbers = Source.fromIterator(() => Iterator.continually(Random.nextInt()))

    val route =
      path("random") {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/plain(UTF-8)`,
              // transform each number to a chunk of bytes
              numbers.map(n => ByteString(s"$n\n"))
            )
          )
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}*/
/*

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.github.pvoznenko.utils.Config
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._

object Main extends App with Config with Routes {
  private implicit val system = ActorSystem("mySystem")
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()


  val route = {
    pathSingleSlash {
      get {
        complete {
          "Hello world!"
        }
      }
    }
    path("hello") {
      get {
        complete {
          "Say hello!!"
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("ENTER!!")




//  Http().bindAndHandle(handler = logRequestResult("log")(routes), interface = httpInterface, port = httpPort)
}*/
