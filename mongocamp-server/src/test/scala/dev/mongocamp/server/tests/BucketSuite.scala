package dev.mongocamp.server.tests

import better.files.{File, Resource}
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.server.database.TestAdditions.copyResourceFileToTempDir
import dev.mongocamp.server.database.{MongoDatabase, TestAdditions}
import dev.mongocamp.server.test.client.api.{BucketApi, DatabaseApi}

import scala.util.Random

class BucketSuite extends BaseServerSuite {

  val api: BucketApi           = BucketApi()
  val databaseApi: DatabaseApi = DatabaseApi()

  test("list all buckets as admin") {
    val response = executeRequestToResponse(api.listBuckets("", "", adminBearerToken, "")())
    assertEquals(response.size, 1)
    assertEquals(response, List("sample-files"))
  }

  test("buckets sample-files as admin") {
    val response = executeRequestToResponse(api.getBucket("", "", adminBearerToken, "")("sample-files"))
    assertEquals(response.files, 4L)
    val minSize = 400000
    val maxSize = 410000
    assertEquals(response.size > minSize, true, s"size (${response.size}) is not larger than $minSize")
    assertEquals(response.size < maxSize, true, s"size (${response.size}) is not smaller than $maxSize")
    val minObjectSize = 100000
    val maxObjectSize = 100500
    assertEquals(response.avgObjectSize > minObjectSize, true, s"avgObjectSize (${response.avgObjectSize}) is not larger than $minObjectSize")
    assertEquals(response.avgObjectSize < maxObjectSize, true, s"avgObjectSize (${response.avgObjectSize}) is not smaller than $maxObjectSize")
  }

  test("clear bucket as admin") {
    val bucketName = "delete-files"
    object FilesDAO extends GridFSDAO(MongoDatabase.databaseProvider, bucketName)
    val accountFile = File.newTemporaryFile()
    accountFile.append(Resource.asString("accounts.json").getOrElse(""))
    FilesDAO.uploadFile(accountFile.name, accountFile, Map("test" -> Random.alphanumeric.take(10).mkString, "fullPath" -> accountFile.toString())).result()

    val response = executeRequestToResponse(api.getBucket("", "", adminBearerToken, "")(bucketName))
    assertEquals(response.name, bucketName)
    val deleteResponse = executeRequestToResponse(api.clearBucket("", "", adminBearerToken, "")(bucketName))
    assertEquals(deleteResponse.value, true)
  }

  test("delete buckets as admin") {
    val bucketName = "delete-files"
    object FilesDAO extends GridFSDAO(MongoDatabase.databaseProvider, bucketName)
    val accountFile: File = copyResourceFileToTempDir(TestAdditions.tempDir, "accounts.json")
    FilesDAO.uploadFile(accountFile.name, accountFile, Map("test" -> Random.alphanumeric.take(10).mkString, "fullPath" -> accountFile.toString())).result()

    val response = executeRequestToResponse(api.getBucket("", "", adminBearerToken, "")(bucketName))
    assertEquals(response.name, bucketName)
    val deleteResponse = executeRequestToResponse(api.deleteBucket("", "", adminBearerToken, "")(bucketName))
    assertEquals(deleteResponse.value, true)

    val collectionNames = MongoDatabase.databaseProvider.collectionNames()
    assertEquals(collectionNames.exists(_.contains(bucketName)), false)

  }

  test("list all buckets as user") {
    val response = executeRequestToResponse(api.listBuckets("", "", testUserBearerToken, "")())
    assertEquals(response.size, 0)
    assertEquals(response, List())
  }

  test("buckets sample-files as user") {
    val response = executeRequest(api.getBucket("", "", testUserBearerToken, "")("sample-files"))
    assertEquals(response.code.code, 401)
    assertEquals(response.header("x-error-message").isDefined, true)
    assertEquals(response.header("x-error-message").get, "user not authorized for bucket")
  }

  test("clear bucket as user") {
    val bucketName = "delete-files"
    val response   = executeRequest(api.getBucket("", "", testUserBearerToken, "")(bucketName))
    assertEquals(response.code.code, 401)
    assertEquals(response.header("x-error-message").isDefined, true)
    assertEquals(response.header("x-error-message").get, "user not authorized for bucket")
  }

  test("delete buckets as user") {
    val bucketName = "delete-files"
    val response   = executeRequest(api.getBucket("", "", testUserBearerToken, "")(bucketName))
    assertEquals(response.code.code, 401)
    assertEquals(response.header("x-error-message").isDefined, true)
    assertEquals(response.header("x-error-message").get, "user not authorized for bucket")
  }
}
