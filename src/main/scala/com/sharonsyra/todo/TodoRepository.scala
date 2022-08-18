package com.sharonsyra.todo

import com.sharonsyra.todo.TodoRepository.TodoNotFound

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait TodoRepository {
  def allTodos(): Future[Seq[Todo]]
  def doneTodos(): Future[Seq[Todo]]
  def pendingTodos(): Future[Seq[Todo]]
  def createTodo(createTodo: CreateTodo): Future[Todo]
  def updateTodo(id: String, updateTodo: UpdateTodo): Future[Todo]
  def deleteTodo(id: String): Future[String]
}

object TodoRepository {
  final case class TodoNotFound(id: String) extends Exception(s"Todo with id $id not found")
}

class InMemoryTodoRepository(initialTodos: Seq[Todo] = Seq.empty[Todo])(implicit ec: ExecutionContext)
  extends TodoRepository {

  private var todos: Seq[Todo] = initialTodos

  override def allTodos(): Future[Seq[Todo]] = Future.successful(todos)

  override def doneTodos(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pendingTodos(): Future[Seq[Todo]] = Future.successful(todos.filterNot(_.done))

  override def createTodo(createTodo: CreateTodo): Future[Todo] = Future.successful {
    val todo = Todo(
      id = UUID.randomUUID().toString,
      title = createTodo.title,
      description = createTodo.description,
      done = false
    )
    todos = todos :+ todo
    todo
  }

  override def updateTodo(id: String, updateTodo: UpdateTodo): Future[Todo] =
    todos.find(_.id == id) match {
      case Some(todo) =>
        val newTodo = updateHelper(todo, updateTodo)
        todos.map(t => if (t.id == id) newTodo else todo)
        Future.successful(newTodo)
      case None => Future.failed(TodoNotFound(id))
    }


  override def deleteTodo(id: String): Future[String] =
    todos.find(_.id == id) match {
      case Some(_) =>
        todos = todos.filterNot(_.id == id)
        Future.successful(s"Todo with id $id deleted successfully")
      case None => Future.failed(TodoNotFound(id))
    }

  private def updateHelper(todo: Todo, updateTodo: UpdateTodo): Todo = {
    val t1 = updateTodo.title.map(title => todo.copy(title = title)).getOrElse(todo)
    val t2 = updateTodo.description.map(description => t1.copy(description = description)).getOrElse(t1)
    updateTodo.done.map(done => t2.copy(done = done)).getOrElse(t2)
  }

}