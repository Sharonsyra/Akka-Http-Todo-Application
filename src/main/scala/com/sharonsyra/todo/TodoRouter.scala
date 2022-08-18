package com.sharonsyra.todo

import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, get, onComplete, path, pathEndOrSingleSlash, pathPrefix, post, put}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route

import scala.util.{Failure, Success}

class TodoRouter(todoRepository: TodoRepository) extends JsonSupport {

  def route: Route =
    pathPrefix("todos") {
      concat(
        pathEndOrSingleSlash {
          concat(
            get {
              onComplete(todoRepository.allTodos()) {
                case Failure(exception) =>
                  val apiError = ApiError.generic(exception)
                  complete(apiError.statusCode, apiError.message)
                case Success(todos) => complete(todos)
              }
            },
            post {
              entity(as[CreateTodo]) { createTodo =>
                CreateTodoValidator.validate(createTodo) match {
                  case Some(apiError) => complete(apiError.statusCode, apiError.message)
                  case None =>
                    onComplete(todoRepository.createTodo(createTodo)) {
                      case Failure(exception) =>
                        val apiError = ApiError.generic(exception)
                        complete(apiError.statusCode, apiError.message)
                      case Success(todo) => complete(todo)
                    }
                }
              }
            }
          )
        },
        path(Segment) { todoId: String =>
          concat(
            put {
              entity(as[UpdateTodo]) { updateTodo =>
                UpdateTodoValidator.validate(updateTodo) match {
                  case Some(apiError) =>  complete(apiError.statusCode, apiError.message)
                  case None =>
                    onComplete(todoRepository.updateTodo(todoId, updateTodo)) {
                      case Failure(exception) =>
                        exception match {
                          case TodoRepository.TodoNotFound(todoId) =>
                            val todoNotFoundError = ApiError.todoNotFound(todoId)
                            complete(todoNotFoundError.statusCode, todoNotFoundError.message)
                          case _ =>
                            val genericError = ApiError.generic(exception)
                            complete(genericError.statusCode, genericError.message)
                        }
                      case Success(todo) => complete(todo)
                    }
                }
              }
            },
            delete(
              onComplete(todoRepository.deleteTodo(todoId)) {
                case Failure(exception) =>
                  exception match {
                    case TodoRepository.TodoNotFound(todoId) =>
                      val todoNotFoundError = ApiError.todoNotFound(todoId)
                      complete(todoNotFoundError.statusCode, todoNotFoundError.message)
                    case _ =>
                      val genericError = ApiError.generic(exception)
                      complete(genericError.statusCode, genericError.message)
                  }
                case Success(todo) => complete(todo)
              }
            )
          )
        },
        (get & path("done")) {
          onComplete(todoRepository.doneTodos()) {
            case Failure(exception) =>
              val apiError = ApiError.generic(exception)
              complete(apiError.statusCode, apiError.message)
            case Success(todos) => complete(todos)
          }
        },
        (get & path("pending")) {
          onComplete(todoRepository.pendingTodos()) {
            case Failure(exception) =>
              val apiError = ApiError.generic(exception)
              complete(apiError.statusCode, apiError.message)
            case Success(todos) => complete(todos)
          }
        }
      )
    }
}
