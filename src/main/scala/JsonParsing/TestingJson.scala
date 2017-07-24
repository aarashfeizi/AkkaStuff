package JsonParsing

import akka.http.scaladsl.model.HttpEntity
import play.api.libs.json.Json

/**
  * Created by feyzi on 7/23/17.
  */
//class TestingJson (str : String) {
object TestingJson extends App {
  //  val jsonValue = Json.parse("""{"id": 1}""")

  val jsonValue = Json.parse(
    """
  {
    "name" : "Watership Down",
    "location" : {
      "lat" : 51.235685,
      "long" : -1.309197
    },
    "residents" : [ {
      "name" : "Fiver",
      "age" : 4,
      "role" : null
    }, {
      "name" : "Bigwig",
      "age" : 6,
      "role" : "Owsla"
    } ]
  }
  """)

  val id = (jsonValue \ "location" \ "lat").as[Double] + 2

  val typee = (jsonValue \ "name").get
  println(id)
  println(typee)

}
