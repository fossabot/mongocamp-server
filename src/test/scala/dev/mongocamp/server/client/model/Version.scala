/** mongocamp-server No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
  *
  * The version of the OpenAPI document: 1.3.4
  *
  * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
  */
package dev.mongocamp.server.client.model

import org.joda.time.DateTime

case class Version(
    name: String,
    version: String,
    builtAt: DateTime
)
