package test.util.user

import org.slf4j.LoggerFactory
import play.api.http.Status.OK
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import test.util.Config.FacebookAppCredentials
import play.api.libs.ws.{WSResponse, WS}
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.test._
import scala.util.Try

case class FacebookTestUser(
    name: String = "John Doe",
    installed: String = "false",
    password: String = "",
    locale: String = "en_US",
    permissions: String = "read_stream",
    method: String = "post",
    id: String = "",
    email: String = "",
    loginUrl: String = "",
    created: Boolean = false)
  extends TestUser

case class FaceBookTestUserException(msg: String) extends Exception(msg)

/**
  * Facebook Test User API
  * https://developers.facebook.com/docs/graph-api/reference/v2.5/app/accounts/test-users
  */
object FacebookTestUserService {

  def logger = LoggerFactory.getLogger(this.getClass)

  private val graphApiUrl = "https://graph.facebook.com/v2.2"

  implicit val app: play.api.test.FakeApplication = new FakeApplication()

  private def GET(url: String): WSResponse = {

    def repeater(url: String, count: Int): WSResponse = {
      val response = Try(Await.result(WS.url(url).get(), 10.second))

      if (count == 0)
        throw new FaceBookTestUserException("Could not query Facebook Graph API.")
      else response.toOption.filter(_.status == OK).getOrElse {
        logger.warn(s"Repeating GET call. Remaining attempts ... ${count-1}")
        repeater(url, count - 1)
      }
    }

    repeater(url, 5)
  }

  private val accessToken = {

    val authEndpoint = s"${graphApiUrl}/oauth/access_token"

    val queryString = Map(
      "client_id" -> FacebookAppCredentials.id,
      "client_secret" -> FacebookAppCredentials.secret,
      "grant_type" -> "client_credentials"
    ).map { case (k, v) => s"$k=$v" }.mkString("&")

    val response = GET(s"${authEndpoint}?${queryString}")
    response.body.split("=")(1)
  }

  def createUser(facebookTestUser: FacebookTestUser = new FacebookTestUser): FacebookTestUser = {

    val queryString = Map(
      "installed" -> facebookTestUser.installed,
      "name" -> facebookTestUser.name,
      "locale" -> facebookTestUser.locale,
      "permissions" -> facebookTestUser.permissions,
      "method" -> facebookTestUser.method,
      "access_token"-> accessToken
    ).map { case (k, v) => s"$k=$v" }.mkString("&")

    val response =
      GET(s"$graphApiUrl/${FacebookAppCredentials.id}/accounts/test-users?${queryString}")

    if (response.status != 200)
      throw new FaceBookTestUserException(
        s"Could not create Facebook Test User. Response = ${response.body}")

    case class FacebookUserResponse(id: String,
                                    password: String,
                                    email: String,
                                    login_url: String)

    implicit val facebookUserJsonReads: Reads[FacebookUserResponse] = (
       (JsPath \ "id").read[String] and
         (JsPath \ "password").read[String] and
         (JsPath \ "email").read[String] and
         (JsPath \ "login_url").read[String]
       )(FacebookUserResponse.apply _)

    val fbUserResponse = response.json.as[FacebookUserResponse]

    val mergedFacebookTestUser = FacebookTestUser(
      facebookTestUser.name,
      facebookTestUser.installed,
      fbUserResponse.password,
      facebookTestUser.locale,
      facebookTestUser.permissions,
      facebookTestUser.method,
      fbUserResponse.id,
      fbUserResponse.email,
      fbUserResponse.login_url,
      created = true)

    mergedFacebookTestUser
  }

  def deleteUser(fbTestUser: FacebookTestUser): Boolean = {

    val queryString = Map(
      "access_token"-> accessToken,
      "method" -> "delete"
    ).map { case (k, v) => s"$k=$v" }.mkString("&")

    val response = GET(s"$graphApiUrl/${fbTestUser.id}?${queryString}")

    if (response.status != 200)
      throw new FaceBookTestUserException(
        s"Could not delete Facebook Test User with ID ${fbTestUser.id}. ${response.body}")

    (response.json \ "success").as[Boolean]
  }
}

