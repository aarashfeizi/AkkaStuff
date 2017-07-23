package com.github.pvoznenko.newPackage

/**
  * Created by feyzi on 7/18/17.
  */


package com.github.pvoznenko

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer

case object OneSlash

case object Ping

case object Crash



object HttpRequest_2 {


  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end
  implicit val executionContext = system.dispatcher


  def main(args: Array[String]): Unit = {

  }
}