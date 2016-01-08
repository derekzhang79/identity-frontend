package com.gu.identity.frontend.views.models


object ContentSecurityPolicy {

  val CSP_DEFAULT_SRC = "default-src"
  val CSP_SCRIPT_SRC = "script-src"
  val CSP_STYLE_SRC = "style-src"
  val CSP_FONT_SRC = "font-src"
  val CSP_IMG_SRC = "img-src"

  val CSP_SELF_DOMAIN = "'self'"
  val CSP_DATA_PROTOCOL = "data:"

  val defaultCsp = Map(
    CSP_DEFAULT_SRC -> Seq(CSP_SELF_DOMAIN)
  )


  def cspForViewModel(viewModel: ViewModel with ViewModelResources) = {
    val allResources = viewModel.resources ++ viewModel.indirectResources
    val grouped = allResources.groupBy {
      case r: ScriptResource => CSP_SCRIPT_SRC
      case r: StylesResource => CSP_STYLE_SRC
      case r: ImageResource => CSP_IMG_SRC
      case r: FontResource => CSP_FONT_SRC
    }

    val transformed = grouped.mapValues(_.map(cspForResource).distinct)

    "Content-Security-Policy" -> toCSPHeader(defaultCsp ++ transformed)
  }


  private def cspForResource(resource: PageResource): String = resource match {
    case r: LocalResource => CSP_SELF_DOMAIN
    case r: ScriptResource with InlinedSource => toCSPShaDefinition(r.sha256)
    case r: InlinedResource => CSP_DATA_PROTOCOL
    case r: ExternalResource => r.domain
  }


  def toCSPHeader(csp: Map[String, Seq[String]]) =
    csp.map {
      case (key, value) => s"$key ${value.mkString(" ")}"
    }.mkString("; ")


  def toCSPShaDefinition(in: String) =
    s"'sha256-$in'"

}
