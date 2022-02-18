package com.quadstingray.mongo.rest.exception

import sttp.model.StatusCode

case class MongoRestException(message: String, statusCode: StatusCode, errorCode: Int = -1, additionalInfo: String = "") extends Exception {
  override def getMessage: String = message
}

object MongoRestException {

  def unauthorizedException(message: String = "user not authorized", errorCode: Int = ErrorCodes.unauthorizedUser): MongoRestException =
    MongoRestException(message, StatusCode.Unauthorized, errorCode)

  def badAuthConfiguration(): MongoRestException =
    MongoRestException("Authentication method not configured correctly", StatusCode.NotImplemented, ErrorCodes.authMethodNotImplemented)

  def userNotFoundException   = MongoRestException("user  does not exists", StatusCode.NotFound)
  def userOrPasswordException = MongoRestException("user or password does not exists", StatusCode.Unauthorized)
  def apiKeyException         = MongoRestException("apiKey not valid", StatusCode.Unauthorized)
}
