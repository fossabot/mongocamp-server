/** mongocamp No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
  *
  * The version of the OpenAPI document: 0.7.0
  *
  * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
  */
package com.quadstingray.mongo.camp.client.model

import org.joda.time.DateTime

case class MongoIndex(
    name: String,
    fields: Seq[String],
    unique: Boolean,
    version: Int,
    namespace: String,
    keys: Map[String, Any],
    weights: Map[String, Any],
    expire: Boolean,
    expireAfterSeconds: Long,
    text: Boolean,
    fetched: DateTime,
    map: Map[String, Any]
)
