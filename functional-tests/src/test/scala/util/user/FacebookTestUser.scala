package util.user

import java.net.URL

import com.squareup.okhttp.{OkHttpClient, Request, Response}
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import test.util.Config.FacebookAppCredentials
import test.util.user.TestUser

case class FacebookTestUser(name: String = "John Doe",
                            installed: String = "false",
                            password: Option[String] = None,
                            locale: String = "en_US",
                            permissions: String = "read_stream",
                            method: String = "post",
                            id: Option[String] = None,
                            email: Option[String] = None,
                            loginUrl: Option[String] = None,
                            created: Boolean = false
                           )
  extends TestUser

case class FaceBookTestUserException(msg: String) extends Exception(msg)

object FacebookTestUserService {

  def logger = LoggerFactory.getLogger(this.getClass)

  private val graphApiUrl = "https://graph.facebook.com"

  private val client = new OkHttpClient()

  private def GET(url: URL): Response = {
    val client = new OkHttpClient()
    val request = new Request.Builder().url(url).build()
    client.newCall(request).execute()
  }

  private val accessToken = {

    val authEndpoint = "https://graph.facebook.com/oauth/access_token"

    val queryString = Map(
      "client_id" -> FacebookAppCredentials.id,
      "client_secret" -> FacebookAppCredentials.secret,
      "grant_type" -> "client_credentials"
    ).map { case (k, v) => s"$k=$v" }.mkString("&")

    val response = GET(new URL(s"${authEndpoint}?${queryString}"))

    response.body().string().split("=")(1)
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
      GET(new URL(s"$graphApiUrl/${FacebookAppCredentials.id}/accounts/test-users?$queryString"))

    if (!response.isSuccessful)
      throw new FaceBookTestUserException(
        s"Could not create Facebook Test User. Response = ${response.body().string()}")

    val responseJson: JsValue = Json.parse(response.body().string())

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

    val fbUserResponse = responseJson.as[FacebookUserResponse]

    val mergedFacebookTestUser = FacebookTestUser(
      facebookTestUser.name,
      facebookTestUser.installed,
      Some(fbUserResponse.password),
      facebookTestUser.locale,
      facebookTestUser.permissions,
      facebookTestUser.method,
      Some(fbUserResponse.id),
      Some(fbUserResponse.email),
      Some(fbUserResponse.login_url),
      created = true
    )

    mergedFacebookTestUser
  }

  def deleteUser(fbTestUser: FacebookTestUser): Boolean = {

    val queryString = Map(
      "access_token"-> accessToken,
      "method" -> "delete"
    ).map { case (k, v) => s"$k=$v" }.mkString("&")

    val fbTestUserid = fbTestUser.id.getOrElse( throw new IllegalStateException(
          "FacebookTestUser is missing ID. Cannot delete FacebookTestUser."))

    val response = GET(new URL(s"$graphApiUrl/${fbTestUserid}?$queryString"))

    if (!response.isSuccessful)
      throw new FaceBookTestUserException(
        s"Could not delete Facebook Test User with ID ${fbTestUserid}. ${response.body().string()}")

    response.body().string().toBoolean
  }
}

