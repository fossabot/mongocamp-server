/** mongocamp No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
  *
  * The version of the OpenAPI document: 0.3.1
  *
  * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
  */
package com.quadstingray.mongo.camp.client.api

import com.quadstingray.mongo.camp.client.core.JsonSupport._
import com.quadstingray.mongo.camp.client.model.{ MongoAggregateRequest, MongoFindRequest }
import com.quadstingray.mongo.camp.converter.CirceSchema
import sttp.client._
import sttp.model.Method

object ReadApi {

  def apply(baseUrl: String = com.quadstingray.mongo.camp.server.TestServer.serverBaseUrl) = new ReadApi(baseUrl)
}

class ReadApi(baseUrl: String) extends CirceSchema {

  /** Aggregate in your MongoDatabase Collection
    *
    * Expected answers: code 200 : Seq[Map[String, Any]] Headers : x-pagination-count-rows - count all elements x-pagination-rows-per-page - Count elements per
    * page x-pagination-current-page - Current page x-pagination-count-pages - Count pages code 400 : String (Invalid value for: body, Invalid value for: query
    * parameter rowsPerPage, Invalid value for: query parameter page) code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of
    * the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param collectionName
    *   The name of your MongoDb Collection
    * @param mongoAggregateRequest
    *   @param rowsPerPage Count elements per page
    * @param page
    *   Requested page of the ResultSets
    */
  def aggregate(apiKey: String, bearerToken: String)(
      collectionName: String,
      mongoAggregateRequest: MongoAggregateRequest,
      rowsPerPage: Option[Long] = None,
      page: Option[Long] = None
  ): Request[Either[ResponseError[Exception], Seq[Map[String, Any]]], Nothing] =
    basicRequest
      .method(Method.POST, uri"$baseUrl/mongodb/collections/$collectionName/aggregate?rowsPerPage=$rowsPerPage&page=$page")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .body(mongoAggregateRequest)
      .response(asJson[Seq[Map[String, Any]]])

  /** Distinct for Field in your MongoDatabase Collection
    *
    * Expected answers: code 200 : Seq[String] Headers : x-pagination-count-rows - count all elements x-pagination-rows-per-page - Count elements per page
    * x-pagination-current-page - Current page x-pagination-count-pages - Count pages code 400 : String (Invalid value for: query parameter rowsPerPage, Invalid
    * value for: query parameter page) code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of the MongoCampException
    * x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param collectionName
    *   The name of your MongoDb Collection
    * @param field
    *   The field for your distinct Request.
    * @param rowsPerPage
    *   Count elements per page
    * @param page
    *   Requested page of the ResultSets
    */
  def distinct(apiKey: String, bearerToken: String)(
      collectionName: String,
      field: String,
      rowsPerPage: Option[Long] = None,
      page: Option[Long] = None
  ): Request[Either[ResponseError[Exception], Seq[String]], Nothing] =
    basicRequest
      .method(Method.POST, uri"$baseUrl/mongodb/collections/$collectionName/distinct/$field?rowsPerPage=$rowsPerPage&page=$page")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .response(asJson[Seq[String]])

  /** Search in your MongoDatabase Collection
    *
    * Expected answers: code 200 : Seq[Map[String, Any]] Headers : x-pagination-count-rows - count all elements x-pagination-rows-per-page - Count elements per
    * page x-pagination-current-page - Current page x-pagination-count-pages - Count pages code 400 : String (Invalid value for: body, Invalid value for: query
    * parameter rowsPerPage, Invalid value for: query parameter page) code 0 : ErrorDescription Headers : x-error-code - Error Code x-error-message - Message of
    * the MongoCampException x-error-additional-info - Additional information for the MongoCampException
    *
    * Available security schemes: apiKeyAuth (apiKey) httpAuth (http)
    *
    * @param collectionName
    *   The name of your MongoDb Collection
    * @param mongoFindRequest
    *   @param rowsPerPage Count elements per page
    * @param page
    *   Requested page of the ResultSets
    */
  def find(apiKey: String, bearerToken: String)(
      collectionName: String,
      mongoFindRequest: MongoFindRequest,
      rowsPerPage: Option[Long] = None,
      page: Option[Long] = None
  ): Request[Either[ResponseError[Exception], Seq[Map[String, Any]]], Nothing] =
    basicRequest
      .method(Method.POST, uri"$baseUrl/mongodb/collections/$collectionName/find?rowsPerPage=$rowsPerPage&page=$page")
      .contentType("application/json")
      .header("X-AUTH-APIKEY", apiKey)
      .auth
      .bearer(bearerToken)
      .body(mongoFindRequest)
      .response(asJson[Seq[Map[String, Any]]])

}
