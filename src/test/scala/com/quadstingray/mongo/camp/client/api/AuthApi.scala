/** mongocamp No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
  *
  * The version of the OpenAPI document: 0.7.0
  *
  * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
  */
package com.quadstingray.mongo.camp.client.api

import com.quadstingray.mongo.camp.client.core.JsonSupport._
import com.quadstingray.mongo.camp.client.model._
import com.quadstingray.mongo.camp.converter.CirceSchema
import sttp.client3._
import sttp.client3.circe.asJson
import sttp.model.Method

object AuthApi {

  def apply(baseUrl: String = com.quadstingray.mongo.camp.server.TestServer.serverBaseUrl) = new AuthApi(baseUrl)
}

class AuthApi(baseUrl: String) extends CirceSchema {

  /** Generate new ApiKey of logged in User
    *
    * Expected answers: code 200 : JsonResultString code 400 : String (Invalid value for: query parameter userid) code 0 : ErrorDescription Headers :
    * x-error-code - Error Code x-error-message - Message of the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param userid
    *   UserId to update or create the ApiKey
    */
  def generateNewApiKey(apiKey: String, bearerToken: String)(
      userid: Option[String] = None
  ) =
    basicRequest
      .method(Method.PATCH, uri"$baseUrl/auth/profile/apikey?userid=$userid")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .response(asJson[JsonResultString])

  /** Login for one user and return Login Information
    *
    * Expected answers: code 200 : LoginResult code 400 : String (Invalid value for: body) code 0 : ErrorDescription Headers : x-error-code - Error Code
    * x-error-message - Message of the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * @param login
    *   Login Information for your Users
    */
  def login(login: Login) =
    basicRequest
      .method(Method.POST, uri"$baseUrl/auth/login")
      .contentType("application/json")
      .body(login)
      .response(asJson[LoginResult])

  /** Logout by bearer Token
    *
    * Expected answers: code 200 : JsonResultBoolean code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of the
    * MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    */
  def logout(apiKey: String, bearerToken: String)(
  ) =
    basicRequest
      .method(Method.POST, uri"$baseUrl/auth/logout")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .response(asJson[JsonResultBoolean])

  /** Logout by bearer Token
    *
    * Expected answers: code 200 : JsonResultBoolean code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of the
    * MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    */
  def logoutByDelete(apiKey: String, bearerToken: String)(
  ) =
    basicRequest
      .method(Method.DELETE, uri"$baseUrl/auth/logout")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .response(asJson[JsonResultBoolean])

  /** Refresh Token and return Login Information
    *
    * Expected answers: code 200 : LoginResult code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of the MongoCampException
    * x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    */
  def refreshToken(apiKey: String, bearerToken: String)(
  ) =
    basicRequest
      .method(Method.GET, uri"$baseUrl/auth/token/refresh")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .response(asJson[LoginResult])

  /** Change Password of logged in User
    *
    * Expected answers: code 200 : JsonResultBoolean code 400 : String (Invalid value for: body) code 0 : ErrorDescription Headers : x-error-code - Error Code
    * x-error-message - Message of the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param passwordUpdateRequest
    */
  def updatePassword(apiKey: String, bearerToken: String)(
      passwordUpdateRequest: PasswordUpdateRequest
  ) =
    basicRequest
      .method(Method.PATCH, uri"$baseUrl/auth/profile/password")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .body(passwordUpdateRequest)
      .response(asJson[JsonResultBoolean])

  /** Return the User Profile of loggedin user
    *
    * Expected answers: code 200 : UserProfile code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of the MongoCampException
    * x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    */
  def userProfile(apiKey: String, bearerToken: String)(
  ) =
    basicRequest
      .method(Method.GET, uri"$baseUrl/auth/profile")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .response(asJson[UserProfile])

}
