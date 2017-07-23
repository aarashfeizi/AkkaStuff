package com.github.pvoznenko.newPackage


/**
  * Created by feyzi on 7/23/17.
  */

  import akka.actor.{Actor, ActorSystem}
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.marshalling.Marshal
  import akka.http.scaladsl.model.HttpMethods._
  import akka.http.scaladsl.model._
  import akka.http.scaladsl.unmarshalling.Unmarshal
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl.{Flow, Sink, Source}

  import scala.concurrent.duration._
  import scala.concurrent.{Await, Future}
  import scala.io.StdIn
  import scala.util.{Failure, Success}




object Pintapin {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end
  implicit val executionContext = system.dispatcher

  def changeString(oldString: String) = {
    val newString = oldString.replace("pintapin", "@#%$!$^")
    Future(newString)
  }

  def main(args: Array[String]): Unit = {
    /* implicit val system = ActorSystem()
     implicit val materializer = ActorMaterializer()
     // needed for the future map/flatmap in the end
     implicit val executionContext = system.dispatcher

     // construct a simple GET request to `homeUri`
     val homeUri = Uri("/abc")
     HttpRequest(GET, uri = homeUri)



     val bindingFuture = Http().bindAndHandleSync(requestHandler, "localhost", 8080)*/

    /**
      * init
      */


    /**
      * done
      */


    val requestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) => {


        val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
          Http().outgoingConnection("beta.ptp")

        def dispatchRequest(request: HttpRequest): Future[HttpResponse] =
          Source.single(request)
            .via(connectionFlow)
            .runWith(Sink.head)

        val aggFut /*: IndexedSeq[String] */ = for {
          p <- dispatchRequest(HttpRequest(uri = "/searchdest?search_text=Tehran"))
//          p <- dispatchRequest(HttpRequest(uri = "/about"))
          oldString <- Unmarshal(p).to[String];
          newString <- changeString(oldString)
        //          finalp <- parseResponse(p.entity)
        } yield (newString)

        aggFut map { p =>
          HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`text/html`), p))
          //          HttpResponse(entity = p)

        }

      }
      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
        Future {
          HttpResponse(entity = "PONG!")
        }
      case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
        sys.error("BOOM!")

    }


    val bindingFuture = Http().bindAndHandleAsync(requestHandler, "localhost", 9000)
    println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.shutdown()) // and shutdown when done

  }

}