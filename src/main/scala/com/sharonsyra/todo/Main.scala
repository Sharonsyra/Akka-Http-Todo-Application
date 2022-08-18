package com.sharonsyra.todo

import akka.actor.ActorSystem

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main extends App {
  val host: String = "0.0.0.0"
  val port: Int = 8080

  implicit val system: ActorSystem = ActorSystem("todoapi")

  val todoRepository = new InMemoryTodoRepository(
    Seq(
      Todo(id = UUID.randomUUID().toString, title = "pack", description = "pack up", done = true),
      Todo(id = UUID.randomUUID().toString, title = "cook", description = "cook dinner", done = true),
      Todo(id = UUID.randomUUID().toString, title = "travel", description = "travel", done = true),
      Todo(id = UUID.randomUUID().toString, title = "docs", description = "travel docs", done = false),
      Todo(id = UUID.randomUUID().toString, title = "meal prep", description = "prep", done = false)
    )
  )
  val router = new TodoRouter(todoRepository)
  val server = new Server(router = router, host = host, port = port)

  val binding = server.bind()

  binding.onComplete {
    case Success(value) => println(s"value is $value")
    case Failure(exception) => println(s"exception is $exception")
  }
}
