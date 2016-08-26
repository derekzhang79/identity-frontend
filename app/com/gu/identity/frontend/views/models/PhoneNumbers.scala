package com.gu.identity.frontend.views.models

import com.google.i18n.phonenumbers.PhoneNumberUtil

import scala.collection.JavaConverters._
import scala.reflect.runtime.{universe => ru}

trait PhoneNumbers {

  val phoneNumberUtil = PhoneNumberUtil.getInstance()

  /**
    * Unfortunately the country code map is not accessible in PhoneNumberUtil. This code uses reflection to obtain access
    * then transform it to the correct structure.
    */

  val countryCodes = {
    val m = ru.runtimeMirror(phoneNumberUtil.getClass.getClassLoader)
    val countryCallingCodeToRegionCodeMapSymb = ru.typeOf[PhoneNumberUtil].decl(ru.TermName("countryCallingCodeToRegionCodeMap")).asTerm
    val im = m.reflect(phoneNumberUtil)
    val countryCallingCodeToRegionCodeMap = im.reflectField(countryCallingCodeToRegionCodeMapSymb).get.asInstanceOf[java.util.HashMap[java.lang.Integer,java.util.List[java.lang.String]]]
    countryCallingCodeToRegionCodeMap.keySet().asScala.toList.sorted
  }
}

case class CountryCodeViewModel(code: Integer) extends ViewModel

object CountryCodes extends PhoneNumbers {

  def apply = {
      val codes = countryCodes.map{ c => CountryCodeViewModel(c) }
      new CountryCodes(codes)
  }
}

case class CountryCodes(codes: Seq[CountryCodeViewModel]) {
  val s = codes.size
}

