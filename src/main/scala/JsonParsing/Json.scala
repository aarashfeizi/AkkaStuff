package JsonParsing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.concurrent.duration
import scala.util.{Failure, Success}


/**
  * Created by feyzi on 7/23/17.
  */
object Json {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {

    val requestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) => {


        val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
          Http().outgoingConnection("beta.ptp")

        def dispatchRequest(request: HttpRequest): Future[HttpResponse] =
          Source.single(request)
            .via(connectionFlow)
            .runWith(Sink.head)

//        val request = dispatchRequest(HttpRequest(uri = "/about"))
        val request = dispatchRequest(HttpRequest(uri = "/searchdest?search_text=Tehran"))

//        HttpResponse(entity = Await.result(request, 2 seconds))
        request map { r =>
          HttpResponse(entity = r.entity)
        }

      }
    }
    val bindingFuture = Http().bindAndHandleAsync(requestHandler, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.shutdown()) // and shutdown when done
  }
}
