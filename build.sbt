name := """HappyHourApp"""

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.1",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)

(initialCommands in Compile) := """import akka.actor._
                                  |import akka.pattern._
                                  |import akka.util._
                                  |import com.scalapenos.riak._
                                  |import com.typesafe.training.hakkyhour._
                                  |import scala.concurrent._
                                  |import scala.concurrent.duration._""".stripMargin

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")