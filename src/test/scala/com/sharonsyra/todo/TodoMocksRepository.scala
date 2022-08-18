package com.sharonsyra.todo
import scala.concurrent.Future

class TodoMocksRepository extends TodoRepository {
  override def allTodos(): Future[Seq[Todo]] = Future.failed(new Exception("oops"))

  override def doneTodos(): Future[Seq[Todo]] = Future.failed(new Exception("oops"))

  override def pendingTodos(): Future[Seq[Todo]] = Future.failed(new Exception("oops"))

  override def createTodo(createTodo: CreateTodo): Future[Todo] = Future.failed(new Exception("oops"))

  override def updateTodo(id: String, updateTodo: UpdateTodo): Future[Todo]=
    Future.failed(new Exception("oops"))

  override def deleteTodo(id: String): Future[String] = Future.failed(new Exception("oops"))
}
