package com.sharonsyra.todo

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class TodoRouterSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  private val pendingTodos = Seq(
    Todo(id = UUID.randomUUID().toString, title = "docs", description = "travel docs", done = false),
    Todo(id = UUID.randomUUID().toString, title = "meal prep", description = "prep", done = false)
  )

  val packingTodoId: String = UUID.randomUUID().toString
  val packingTodo: Todo = Todo(id = packingTodoId, title = "pack", description = "pack up", done = true)
  private val doneTodos = Seq(
    packingTodo,
    Todo(id = UUID.randomUUID().toString, title = "cook", description = "cook dinner", done = true),
    Todo(id = UUID.randomUUID().toString, title = "travel", description = "travel", done = true)
  )

  private val todos = pendingTodos.appendedAll(doneTodos)

  private val validCreateTodo = CreateTodo(title = "ace interview", description = "ace the mofo!!!!!!")
  private val emptyTitleCreateTodo = CreateTodo(title = "", description = "I got to title")

  private val validUpdatePackingTodo = UpdateTodo(title = Some("packing"), description = None, done = None)
  private val missingTitleUpdatePackingTodo = UpdateTodo(title = None, description = Some("pack now"), done = None)
  private val emptyTitleUpdatePackingTodo = UpdateTodo(title = Some(""), description = None, done = None)

  val nonExistentTodoId: String = UUID.randomUUID().toString

  val todoRepository = new InMemoryTodoRepository(todos)
  val router = new TodoRouter(todoRepository)

  "Todo Router" should {

    "return all the todos successfully" in {
      Get("/todos") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[Seq[Todo]] shouldBe todos
      }
    }

    "return done todos successfully" in {
      Get("/todos/done") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[Seq[Todo]] shouldBe doneTodos
      }
    }

    "return pending todos successfully" in {
      Get("/todos/pending") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[Seq[Todo]] shouldBe pendingTodos
      }
    }

    "create a todo with valid data" in {
      Post("/todos", validCreateTodo) ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        val resp = responseAs[Todo]
        resp.title shouldBe validCreateTodo.title
        resp.description shouldBe validCreateTodo.description
      }
    }

    "reject a todo with empty title when creating todo" in {
      Post("/todos", emptyTitleCreateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.BadRequest
        responseAs[String] shouldBe ApiError.emptyTitleField.message
      }
    }

    "update a todo with valid data" in {
      Put(s"/todos/$packingTodoId", validUpdatePackingTodo) ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        val resp = responseAs[Todo]
        resp.title shouldBe validUpdatePackingTodo.title.get
        resp.description shouldBe packingTodo.description
        resp.done shouldBe packingTodo.done
      }
    }

    "update a todo update with missing title" in {
      Put(s"/todos/$packingTodoId", missingTitleUpdatePackingTodo) ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        val resp = responseAs[Todo]
        resp.title shouldBe packingTodo.title
        resp.description shouldBe missingTitleUpdatePackingTodo.description.get
        resp.done shouldBe packingTodo.done
      }
    }

    "error on update to non existent todo" in {
      Put(s"/todos/$nonExistentTodoId", validUpdatePackingTodo) ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
        responseAs[String] shouldBe ApiError.todoNotFound(nonExistentTodoId).message
      }
    }

    "reject a todo update with empty title" in {
      Put(s"/todos/$packingTodoId", emptyTitleUpdatePackingTodo) ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.BadRequest
        responseAs[String] shouldBe ApiError.emptyTitleField.message
      }
    }

    "delete an existing todo successfully" in {
      Delete(s"/todos/$packingTodoId") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe s"Todo with id $packingTodoId deleted successfully"
      }
    }

    "reject a non existent todo delete" in {
      Delete(s"/todos/$nonExistentTodoId") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
        responseAs[String] shouldBe ApiError.todoNotFound(nonExistentTodoId).message
      }
    }

    "reject non Get methods for all, done and pending todos endpoints" in {
      Post("/todos") ~> router.route ~> check {
        handled shouldBe false
      }
    }

    "reject failing requests" in {
      val failingRepository = new TodoMocksRepository
      val router = new TodoRouter(failingRepository)
      Get("/todos") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.InternalServerError
      }
    }

    "reject requests to unhandled paths" in {
      Get("/") ~> router.route ~> check {
        handled shouldBe false
      }
    }
  }
}
