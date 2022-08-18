package com.sharonsyra.todo

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class Todo(id: String, title: String, description: String, done: Boolean)
final case class CreateTodo(title: String, description: String)
final case class UpdateTodo(title: Option[String], description: Option[String], done: Option[Boolean])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  // formats for unmarshalling and marshalling
  implicit val todoFormat: RootJsonFormat[Todo] = jsonFormat4(Todo)
  implicit val createTodoFormat: RootJsonFormat[CreateTodo] = jsonFormat2(CreateTodo)
  implicit val updateTodoFormat: RootJsonFormat[UpdateTodo] = jsonFormat3(UpdateTodo)
}