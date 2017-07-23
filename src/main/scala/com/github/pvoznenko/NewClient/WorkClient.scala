package com.github.pvoznenko.NewClient
import java.util.concurrent.Future

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.Uri.Path
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future


/**
  * Created by feyzi on 7/19/17.
object WorkClient {

  // constructor boilerplate elided

  // Type alias for readability's sake
  type TradingApiResult[T] = Either[ApiError, T]

  sealed trait ApiError
  case class NotFound(error: String) extends ApiError
  case class Unauthorized(error: String) extends ApiError
  case class UnexpectedStatusCode(status: StatusCode) extends ApiError


  import akka.http.scaladsl.unmarshalling.{ Unmarshal, Unmarshaller }
  import akka.http.scaladsl.model.StatusCodes

  // Constructor/etc elided

  // `um` is provided by the previously mentioned `SprayJsonSupport`
  // This is a prevalent theme in akka-related code: IMPLICITS, IMPLICITS EVERYWHERE.  Fun fact: this also requires
  // an implicit ActorSystem and ActorMaterializer floating around!
  private def deserialize[T](r: HttpResponse)(implicit um: Unmarshaller[ResponseEntity, T]): Future[TradingApiResult[T]] = {
    r.status match {
      case StatusCodes.OK => Unmarshal(r.entity).to[T] map Right.apply
      case StatusCodes.Unauthorized => Future(Left(Unauthorized(r.entity.toString)))
      case StatusCodes.NotFound => Future(Left(NotFound(r.entity.toString)))
      case _ => Future(Left(UnexpectedStatusCode(r.status)))
    }
  }

  // Use it in the API call!
  def apiIsUp: Future[TradingApiResult[ApiStatus]] = {
    val source = Source.single(HttpRequest(uri = Uri(path = Path("/ob/api/heartbeat"))))
    val flow = Http().outgoingConnectionHttps("api.stockfighter.io").mapAsync(1) { r =>
      deserialize[ApiStatus](r)
    }

    source.via(flow).runWith(Sink.head)
  }
}
*/
