package com.gu.identity.frontend.utils

object UrlDecoder {

  def getQueryParams(url: String): Map[String, String] = {

    def createParamMap(counter: Int, acc: Map[String, String], params: Array[String]): Map[String, String] = {
      if(counter == params.length){
        acc
      } else {
        val param = params(counter).split("=")
        if(param.length == 2) {
          val pair = Map(param(0) -> param(1))
          createParamMap(counter + 1, acc ++ pair, params)
        } else {
          createParamMap(counter + 1, acc, params)
        }
      }
    }

    createParamMap(0, Map(),extractQueryParams(url))
  }

  private def extractQueryParams(url: String): Array[String] = {
    val x = url.split("\\?")
    if (x.length != 2) {
      Array()
    } else {
      x(1).split("&")
    }
  }
}
