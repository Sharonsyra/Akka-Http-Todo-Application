package com.sharonsyra.todo

trait TodoValidator[T] {
  def validate(t: T): Option[ApiError]
}

object CreateTodoValidator extends TodoValidator[CreateTodo] {
  override def validate(t: CreateTodo): Option[ApiError] =
    if (t.title.isEmpty) Some(ApiError.emptyTitleField)
    else None
}

object UpdateTodoValidator extends TodoValidator[UpdateTodo] {
  override def validate(t: UpdateTodo): Option[ApiError] = {
    if (t.title.exists(_.isEmpty)) Some(ApiError.emptyTitleField)
    else None
  }
}
