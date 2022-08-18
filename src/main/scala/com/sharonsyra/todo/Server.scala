package com.sharonsyra.todo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import scala.concurrent.{ExecutionContext, Future}

class Server(router: TodoRouter, host: String, port: Int)(implicit ex: ExecutionContext, actorSystem: ActorSystem) {

  def bind(): Future[ServerBinding] = Http().newServerAt(interface = host, port = port).bind(router.route)

}
