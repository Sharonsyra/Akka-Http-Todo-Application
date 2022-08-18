package com.sharonsyra.todo

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

final case class ApiError private(statusCode: StatusCode, message: String)

object ApiError {

  private def apply(statusCode: StatusCode, message: String) = new ApiError(statusCode, message)

  def generic(exception: Throwable) = new ApiError(StatusCodes.InternalServerError,
    s"Exception occurred: ${exception.getMessage}")

  val emptyTitleField: ApiError = new ApiError(StatusCodes.BadRequest, "The title field must not be empty.")

  def todoNotFound(id: String): ApiError = new ApiError(StatusCodes.NotFound, s"Todo with id $id not found")
}
