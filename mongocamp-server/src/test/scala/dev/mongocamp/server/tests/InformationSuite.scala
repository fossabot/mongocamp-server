package dev.mongocamp.server.tests

import dev.mongocamp.server.BuildInfo
import dev.mongocamp.server.test.client.api.InformationApi
import org.joda.time.DateTime

class InformationSuite extends BaseServerSuite {

  val informationApi: InformationApi = InformationApi()

  test("check version by api request") {
    val version = executeRequestToResponse(informationApi.version())
    assertEquals(version.name, BuildInfo.name)
    assertEquals(version.version, BuildInfo.version)
    assertEquals(version.builtAt, new DateTime(BuildInfo.builtAtMillis))
  }

}
