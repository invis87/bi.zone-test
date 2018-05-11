package com.pronvis.bi.zone.test

import java.text.SimpleDateFormat

object Solution {

  val timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def findMultipleLogins(logins: Iterator[UserLogin], timeWindow: Long): Seq[MultipleLogins] = {
    val loginsMap = groupUsersByIpAddress(logins)
    loginsMap
      .filter(_._2.size > 1)
      .flatMap { case(ipAddress, loginsPerIp) => findMultipleLoginsInternal(ipAddress, loginsPerIp, timeWindow) }
      .toSeq
  }

  private def findMultipleLoginsInternal(ipAddress: String, logins: Seq[UserLogin], timeWindow: Long): Seq[MultipleLogins] = {
    val resultBuffer = new scala.collection.mutable.ListBuffer[MultipleLogins]()
    val userLoginTimesBuffer = new scala.collection.mutable.HashSet[UserLoginTime]()

    def addToResult(firstLogin: UserLogin, endTime: Long): Unit = {
      if(userLoginTimesBuffer.nonEmpty) {
        userLoginTimesBuffer.add(firstLogin.userLoginTime)
        resultBuffer.append(MultipleLogins(ipAddress, firstLogin.userLoginTime.loginTime, endTime, userLoginTimesBuffer.toList))
      }
      userLoginTimesBuffer.clear()
    }

    val sortedLogins = logins.sortBy(_.userLoginTime.loginTime).toIterator
    var firstLoginInSeq = sortedLogins.next()
    var endTime: Long = 0l
    while (sortedLogins.hasNext) {
      val next = sortedLogins.next()
      if(next.userLoginTime.loginTime - firstLoginInSeq.userLoginTime.loginTime <= timeWindow) {
        userLoginTimesBuffer.add(next.userLoginTime)
        endTime = next.userLoginTime.loginTime
      } else {
        addToResult(firstLoginInSeq, endTime)
        firstLoginInSeq = next
      }
    }

    addToResult(firstLoginInSeq, endTime)
    resultBuffer.toList
  }

  private def groupUsersByIpAddress(usersLogins: Iterator[UserLogin]): Map[String, Seq[UserLogin]] = {
    val result = new scala.collection.mutable.HashMap[String, Seq[UserLogin]]()
    while (usersLogins.hasNext) {
      val userLogin = usersLogins.next()
      val updatedLogins = result.get(userLogin.ipAddress) match {
        case None         => Seq(userLogin)
        case Some(logins) => logins :+ userLogin
      }
      result.update(userLogin.ipAddress, updatedLogins)
    }
    result.toMap
  }

  def parseUserLogin(str: String): UserLogin = {
    val cols = str.filterNot(_.equals('\"')).split(",").map(_.trim)
    val name = cols(0)
    val ipAddress = cols(1)
    val loginTime = timeFormatter.parse(cols(2))
    UserLogin(UserLoginTime(name, loginTime.getTime), ipAddress)
  }

}

case class UserLogin(userLoginTime: UserLoginTime, ipAddress: String)
case class UserLoginTime(name: String, loginTime: Long)
case class MultipleLogins(ipAddress: String, startTime: Long, endTime: Long, userLogins: Seq[UserLoginTime])
