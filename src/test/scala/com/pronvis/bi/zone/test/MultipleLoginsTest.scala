package com.pronvis.bi.zone.test
import java.text.SimpleDateFormat

import org.scalatest.{FunSpecLike, MustMatchers}

class MultipleLoginsTest extends FunSpecLike with MustMatchers {

  val timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  it("choose solution") {
    val login = "no_name"
    val ipAddress = "ip"
    val timeWindow = 2l
    val logins: Seq[UserLogin] = Seq(
      UserLogin(UserLoginTime(login, 1), ipAddress),
      UserLogin(UserLoginTime(login, 2), ipAddress),
      UserLogin(UserLoginTime(login, 3), ipAddress),
      UserLogin(UserLoginTime(login, 4), ipAddress),
      UserLogin(UserLoginTime(login, 5), ipAddress))

    //solution could be one of:
    //1) (1,2,3), (4,5)
    //2) (1,2,3), (2,3,4), (3,4,5)

    // I choose #1

    Solution.findMultipleLogins(logins.toIterator, timeWindow) must contain theSameElementsAs List(
      MultipleLogins(ipAddress, 4, 5, List(UserLoginTime(login, 4), UserLoginTime(login, 5))),
      MultipleLogins(ipAddress, 1, 3, List(UserLoginTime(login, 3), UserLoginTime(login, 2), UserLoginTime(login, 1))))
  }

  it("should correctly works with empty list") {
    val ipAddress = "ip"
    val timeWindow = 2l
    val logins: Seq[UserLogin] = Seq.empty
    Solution.findMultipleLogins(logins.toIterator, timeWindow) must be (empty)
  }

  it("should return empty list if there is no multiple logins in a time window") {
    val login = "no_name"
    val ipAddress = "ip"
    val timeWindow = 2l
    val logins: Seq[UserLogin] = Seq(
      UserLogin(UserLoginTime(login, 1), ipAddress),
      UserLogin(UserLoginTime(login, 20), ipAddress),
      UserLogin(UserLoginTime(login, 30), ipAddress),
      UserLogin(UserLoginTime(login, 40), ipAddress),
      UserLogin(UserLoginTime(login, 50), ipAddress))
    Solution.findMultipleLogins(logins.toIterator, timeWindow) must be (empty)
  }

  it("should return one seq of all connections if time window is big enough") {
    val ipAddress = "ip"
    val timeWindow = 20l
    val logins: Seq[UserLogin] = List(
      UserLogin(UserLoginTime("Lex", 1), ipAddress),
      UserLogin(UserLoginTime("Dracula", 2), ipAddress),
      UserLogin(UserLoginTime("SnowWhite", 3), ipAddress),
      UserLogin(UserLoginTime("Rembo", 4), ipAddress),
      UserLogin(UserLoginTime("Dukalis", 5), ipAddress))

    Solution.findMultipleLogins(logins.toIterator, timeWindow) must contain theSameElementsAs List(
      MultipleLogins(ipAddress, 1, 5, List(
        UserLoginTime("Dracula", 2),
        UserLoginTime("SnowWhite", 3),
        UserLoginTime("Dukalis", 5),
        UserLoginTime("Lex", 1),
        UserLoginTime("Rembo", 4))))
  }

  it("should find several multiple logins") {
    val ipAddress = "ip"
    val logins = List(
      UserLogin(UserLoginTime("Vasia", 22), ipAddress),
      UserLogin(UserLoginTime("Masha", 32), ipAddress),
      UserLogin(UserLoginTime("Petia", 52), ipAddress),
      UserLogin(UserLoginTime("Dasha", 62), ipAddress),
      UserLogin(UserLoginTime("Petr", 102), ipAddress),
      UserLogin(UserLoginTime("Petr", 112), ipAddress))

    val timeWindow = 10l
    Solution.findMultipleLogins(logins.toIterator, timeWindow) must contain theSameElementsAs List(
      MultipleLogins(ipAddress, 22, 32, List(UserLoginTime("Vasia", 22), UserLoginTime("Masha", 32))),
      MultipleLogins(ipAddress, 52, 62, List(UserLoginTime("Petia", 52), UserLoginTime("Dasha", 62))),
      MultipleLogins(ipAddress, 102, 112, List(UserLoginTime("Petr", 112), UserLoginTime("Petr", 102))))
  }

  it("should properly works for several ip addresses") {
    val ip1 = "ip1"
    val ip2 = "ip2"
    val ip3 = "ip3"
    val logins = List(
      UserLogin(UserLoginTime("Vasia", 22), ip1),
      UserLogin(UserLoginTime("Masha", 32), ip2),
      UserLogin(UserLoginTime("Petia", 52), ip3),
      UserLogin(UserLoginTime("Dasha", 25), ip1),
      UserLogin(UserLoginTime("Petr", 35), ip2),
      UserLogin(UserLoginTime("Petr", 55), ip3),
      UserLogin(UserLoginTime("ManFromNowhere", 40), ip2))

    val timeWindow = 10l
    Solution.findMultipleLogins(logins.toIterator, timeWindow) must contain theSameElementsAs List(
      MultipleLogins(ip1, 22, 25, List(UserLoginTime("Vasia", 22), UserLoginTime("Dasha", 25))),
      MultipleLogins(ip2, 32, 40, List(UserLoginTime("Petr", 35), UserLoginTime("Masha", 32), UserLoginTime("ManFromNowhere", 40))),
      MultipleLogins(ip3, 52, 55, List(UserLoginTime("Petr", 55), UserLoginTime("Petia", 52))))
  }

  it("should successfully parse input line") {
    val login = "loginAuthTest"
    val ipAddress = "37.48.80.201"
    val loginDate = "2015-11-30 23:11:51"
    val inputLine = s""""$login","$ipAddress","$loginDate""""
    Solution.parseUserLogin(inputLine) mustEqual UserLogin(UserLoginTime(login, timeFormatter.parse(loginDate).getTime), ipAddress)
  }

  it("should throw exception if can't parse input line") {
    val login = "loginAuthTest"
    val ipAddress = "37.48.80.201"
    val inputLine = s""""$login","$ipAddress""""
    an [Exception] must be thrownBy Solution.parseUserLogin(inputLine)
  }
}