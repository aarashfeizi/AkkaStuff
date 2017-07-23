package com.github.pvoznenko.Binding

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

/**
  * Created by feyzi on 7/18/17.
  */
object Binding {
  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http().bind(interface = "localhost", port = 9090)



    val bindingFuture: Future[Http.ServerBinding] =
      serverSource.to(Sink.foreach { connection => // foreach materializes the source
        println("Accepted new connection from " + connection.remoteAddress)


        // ... and then actually handle the connection
      }).run()
  }
}
