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
import play.api.libs.json
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._


/**
  * Created by feyzi on 7/23/17.
  */
object JsonClass {


  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end
  implicit val executionContext = system.dispatcher

  object Keyword {
    implicit val keywordFormater = Json.format[Keyword]
  }

  object KeywordResult {
    implicit val nikonResultFormater = Json.format[KeywordResult]
  }

  object Title {
    implicit val titleFormater = Json.format[Title]
  }
  object UrlCode{
    implicit val urlCodeFormater = Json.format[UrlCode]
  }
  object Result {
    implicit val resultFormater = Json.format[Result]
  }
  object CategoryResult {
    implicit val categoryResultFormater = Json.format[CategoryResult]
  }


  object DigiJsonObj {
    implicit val digiJsonObjFormater = Json.format[DigiJsonObj]
  }

  case class Keyword(keyword: String) {}

  case class KeywordResult(val KeywordResult: List[Keyword]) {}

  case class CategoryResult(val categoryResult: List[Result]) {}

  case class Result(keyword: Keyword, title: Title, urlCode: UrlCode)

  case class Title(title : String)

  case class UrlCode(urlCode: String)

  case class DigiJsonObj(categoryResult: CategoryResult, val keywordResult: KeywordResult)


  //  val db = Database.forConfig()

  def printstuff(string: String): Unit = {
    val json = Json.parse(string)

    val something = json.validate[DigiJsonObj].asOpt match {
      case Some(a) => {
        a match {
          case DigiJsonObj(b, c) =>
            println("YESSSS***************************************************")
//              for (i <- 1 to c.) {
//                println(i + " blah blah blah" + b(1).keyword)
//              }
        }
      }
      case None => println("ERROR**************************")
    }

    val key = "Keyword"
    val par = "CategoryResult"

    //    println(something)
    //    Json.getj
    //    val arr = (json \ par \ 1 \ key)

    //    println(arr)
    //    for {
    //      i <- 0
    //      to arr
    //    }
    val a = (json \ par \ 1 \ key).as[String]
    println(a)
  }

  def main(args: Array[String]): Unit = {

    val requestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) => {


        val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
          Http().outgoingConnection("www.digikala.com")

        def dispatchRequest(request: HttpRequest): Future[HttpResponse] =
          Source.single(request)
            .via(connectionFlow)
            .runWith(Sink.head)

        //        val request = dispatchRequest(HttpRequest(uri = "/about"))
        val res = for {
        //          response <- dispatchRequest(HttpRequest(uri = "/searchdest?search_text=Tehran"))
          response <- dispatchRequest(HttpRequest(uri = "/api/SearchApi/?term=nikon"))
          entityStr <- Unmarshal(response.entity).to[String]
        //          println(entityStr)
        } yield (response, entityStr)

        res map { r =>

          println(r._2)
          printstuff(r._2)
          HttpResponse(entity = r._2)
        }

        //        res onComplete {
        //          case _:
        //          val obj = new TestingJson(r._2)
        //        }

        //        HttpResponse(entity = Await.result(request, 2 seconds))
        //        request map { r =>
        //          HttpResponse(entity = r.entity)
        //        }

      }
    }

    //    jsObj : JsValue.validate[NikonResult].getOpt
    val bindingFuture = Http().bindAndHandleAsync(requestHandler, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.shutdown()) // and shutdown when done
  }
}
