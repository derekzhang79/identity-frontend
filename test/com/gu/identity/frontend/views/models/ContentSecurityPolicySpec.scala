package com.gu.identity.frontend.views.models

import org.scalatest.{Matchers, WordSpec}


class ContentSecurityPolicySpec extends WordSpec with Matchers {

  case class TestViewModel(
      resources: Seq[PageResource with Product] = Seq.empty,
      indirectResources: Seq[PageResource with Product] = Seq.empty)
    extends ViewModel with ViewModelResources


  "Content Security Policy header" should {
    """contain "'self'" for resources on the same domain""" in {
      val testModel = TestViewModel(
        resources = Seq(
          LocalJavascriptResource.fromAsset("/thing.js", isInHead = false)
        )
      )

      val result = ContentSecurityPolicy.cspForViewModel(testModel)

      result._1 shouldEqual "Content-Security-Policy"
      result._2 shouldEqual "default-src 'self'; script-src 'self'; report-uri /csp/report"
    }

    "contain hash for inlined resources" in {
      val jsSource = "document.write('Hello World!')"
      val sha256 = "itGgFLnPHOj2VA1QXNgyyzJo67oyofrN5R3HFUSQ8zE="
      val testModel = TestViewModel(
        resources = Seq(
          InlinedJavascriptResource(jsSource)
        )
      )

      val result = ContentSecurityPolicy.cspForViewModel(testModel)

      result._1 shouldEqual "Content-Security-Policy"
      result._2 shouldEqual s"default-src 'self'; script-src 'sha256-$sha256'; report-uri /csp/report"
    }

  }

}
