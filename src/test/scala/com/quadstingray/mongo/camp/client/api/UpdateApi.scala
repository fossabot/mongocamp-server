/** mongocamp No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
  *
  * The version of the OpenAPI document: 0.3.1
  *
  * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
  */
package com.quadstingray.mongo.camp.client.api

import com.quadstingray.mongo.camp.client.core.JsonSupport._
import com.quadstingray.mongo.camp.client.model.{ ReplaceOrUpdateRequest, ReplaceResponse, UpdateResponse }
import sttp.client3._
import sttp.model.Method

object UpdateApi {

  def apply(baseUrl: String = com.quadstingray.mongo.camp.server.TestServer.serverBaseUrl) = new UpdateApi(baseUrl)
}

class UpdateApi(baseUrl: String) {

  /** Replace one Document in Collection
    *
    * Expected answers: code 200 : ReplaceResponse code 400 : String (Invalid value for: body) code 0 : ErrorDescription Headers : x-error-code - Error Code
    * x-error-message - Message of the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param collectionName
    *   The name of your MongoDb Collection
    * @param replaceOrUpdateRequest
    */
  def replace(
      apiKey: String,
      bearerToken: String
  )(collectionName: String, replaceOrUpdateRequest: ReplaceOrUpdateRequest) =
    basicRequest
      .method(Method.PATCH, uri"$baseUrl/mongodb/collections/$collectionName/replace")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .body(replaceOrUpdateRequest)
      .response(asJson[ReplaceResponse])

  /** Update one Document in Collection
    *
    * Expected answers: code 200 : UpdateResponse code 400 : String (Invalid value for: body) code 0 : ErrorDescription Headers : x-error-code - Error Code
    * x-error-message - Message of the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param collectionName
    *   The name of your MongoDb Collection
    * @param replaceOrUpdateRequest
    */
  def update(
      apiKey: String,
      bearerToken: String
  )(collectionName: String, replaceOrUpdateRequest: ReplaceOrUpdateRequest) =
    basicRequest
      .method(Method.PATCH, uri"$baseUrl/mongodb/collections/$collectionName/update")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .body(replaceOrUpdateRequest)
      .response(asJson[UpdateResponse])

  /** Update many Document in Collection
    *
    * Expected answers: code 200 : UpdateResponse code 400 : String (Invalid value for: body) code 0 : ErrorDescription Headers : x-error-code - Error Code
    * x-error-message - Message of the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param collectionName
    *   The name of your MongoDb Collection
    * @param replaceOrUpdateRequest
    */
  def updateMany(
      apiKey: String,
      bearerToken: String
  )(collectionName: String, replaceOrUpdateRequest: ReplaceOrUpdateRequest) =
    basicRequest
      .method(Method.PATCH, uri"$baseUrl/mongodb/collections/$collectionName/update/many")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .body(replaceOrUpdateRequest)
      .response(asJson[UpdateResponse])

}
