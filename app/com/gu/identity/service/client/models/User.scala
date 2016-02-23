package com.gu.identity.service.client.models

import org.joda.time.DateTime

case class User(
    id: Option[String] = None,
    dates: Option[CreationDate] = None,
    primaryEmailAddress: Option[String] = None,
    privateFields: Option[PrivateFields] = None,
    publicFields: Option[PublicFields] = None,
    statusFields: Option[StatusFields] = None,
    userGroups: UserGroups)

case class CreationDate(accountCreatedDate: DateTime)

case class PrivateFields(
    lastActiveLocation: Option[LastActiveLocation] = None,
    registrationIp: Option[String] = None,
    lastActiveIpAddress: Option[String] = None,
    registrationPlatform: Option[String] = None,
    secondName: Option[String] = None,
    registrationType: Option[String] = None,
    firstName: Option[String] = None,
    legacyPackages: Option[String] = None,
    legacyProducts: Option[String] = None)

case class LastActiveLocation(cityCode: Option[String] = None, countryCode: Option[String] = None)

case class PublicFields(userName: Option[String] = None, displayName: Option[String] = None, vanityUrl: Option[String] = None, userNameLowerCase: Option[String] = None)

case class StatusFields(allowThirdPartyProfiling: Option[Boolean] = None, userEmailValidated: Option[Boolean] = None)

case class UserGroups(groups: List[Group])

case class Group(packageCode: String, path: String)
