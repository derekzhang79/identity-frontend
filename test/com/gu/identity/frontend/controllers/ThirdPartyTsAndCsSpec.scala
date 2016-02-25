package com.gu.identity.frontend.controllers

import akka.util.Timeout
import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.services.{ServiceGatewayError, IdentityService}
import com.gu.identity.service.client.models.{UserGroup, User}
import org.mockito.Matchers.{any => argAny}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito._
import play.api.mvc.Cookie
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext, Future}

class ThirdPartyTsAndCsSpec extends PlaySpec with MockitoSugar{

  trait WithControllerMockedDependencies {
    val mockIdentityService = mock[IdentityService]
    val mockIdentityCookieDecoder = mock[IdentityCookieDecoder]

    val thirdPartyTsAndCsController = new ThirdPartyTsAndCs(mockIdentityService, mockIdentityCookieDecoder)
  }



  "ThirdPartyTsAndCs" should {

    "return true if user is in group specified" in new WithControllerMockedDependencies {
      val groupCode = "ABC"
      val userGroup = UserGroup(groupCode, "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      thirdPartyTsAndCsController.isUserInGroup(user, groupCode) mustEqual true
    }

    "return false if user is not in the group specified" in  new WithControllerMockedDependencies {
      val groupCode = "123"
      val userGroup = UserGroup("ABC", "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      thirdPartyTsAndCsController.isUserInGroup(user, groupCode) mustEqual false
    }

    "return a future of true when the user is in already in the group specified" in new WithControllerMockedDependencies {
      val groupCode = "ABC"
      val userGroup = UserGroup(groupCode, "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      val timeout = Timeout(5 seconds)

      val cookie = Cookie("Name", "Value")

      when(mockIdentityService.getUser(argAny[Cookie])(argAny[ExecutionContext]))
        .thenReturn {
          Future.successful{
            Right(user)
          }
        }

      val future = thirdPartyTsAndCsController.checkForGroupMembership(groupCode, cookie)
      val result = Await.result(future, timeout.duration)

      result mustEqual Right(true)
    }

    "return a future of false when the user is not in the group specified" in new WithControllerMockedDependencies {
      val groupCode = "ABC"
      val userGroup = UserGroup("123", "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      val timeout = Timeout(5 seconds)

      val cookie = Cookie("Name", "Value")

      when(mockIdentityService.getUser(argAny[Cookie])(argAny[ExecutionContext]))
        .thenReturn {
          Future.successful{
            Right(user)
          }
        }

      val future = thirdPartyTsAndCsController.checkForGroupMembership(groupCode, cookie)
      val result = Await.result(future, timeout.duration)

      result mustEqual Right(false)
    }

    "return a future of sequence of service errors if it was not possible to check if the user is in the group" in new WithControllerMockedDependencies {
      val groupCode = "ABC"
      val userGroup = UserGroup(groupCode, "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      val timeout = Timeout(5 seconds)

      val cookie = Cookie("Name", "Value")

      when(mockIdentityService.getUser(argAny[Cookie])(argAny[ExecutionContext]))
        .thenReturn {
          Future.successful{
            Left(Seq(ServiceGatewayError("Unexpected 500 error")))
          }
        }

      val future = thirdPartyTsAndCsController.checkForGroupMembership(groupCode, cookie)
      val result = Await.result(future, timeout.duration)

      result mustEqual Left(Seq(ServiceGatewayError("Unexpected 500 error")))
    }
  }
}
