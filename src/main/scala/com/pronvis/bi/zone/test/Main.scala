package com.pronvis.bi.zone.test

import java.io.{File, FileWriter}

object Main {

  def main(args: Array[String]): Unit = {
    val csvFilePath = args(0)
    val timeWindow = args(1).toLong
    println(s"Input csv file path: $csvFilePath. TimeWindow: $timeWindow\nStart calculating...")

    if(timeWindow <= 0) throw new RuntimeException("TimeWindow can't be less or equal to zero!")
    val bufferedSource = io.Source.fromFile(csvFilePath)
    val userLogins = bufferedSource.getLines.map(Solution.parseUserLogin)
    val solution = Solution.findMultipleLogins(userLogins, timeWindow)
    writeResults(solution, csvFilePath)
    bufferedSource.close
  }

  def writeResults(solution: Seq[MultipleLogins], inputCsv: String): Unit = {
    val outputDir = new File(inputCsv).getParent
    val fw = new FileWriter(outputDir + "/bi-zone_test_results.csv", false)
    try {
      fw.write("IP address, Start, Stop, Users\n")
      solution.foreach { ml =>
        val userLoginsStr = ml.userLogins.map(ul => s"${ul.name}:${formatLoginTime(ul.loginTime)}").mkString(",")
        val str = s""""${ml.ipAddress}","${formatLoginTime(ml.startTime)}","${formatLoginTime(ml.endTime)}","$userLoginsStr"\n"""
        fw.write(str)
      }
    } finally fw.close()
  }

  private def formatLoginTime(time: Long): String = {
    Solution.timeFormatter.format(time)
  }
}

