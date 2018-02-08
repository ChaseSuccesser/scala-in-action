name := "common"

version := "1.0-SNAPSHOT"


libraryDependencies ++= {
  val akkaVersion = "2.5.4"
  val akkaHttpVersion = "10.0.11"
  val circeVersion = "0.9.1"

  Seq(
    // Akka
    "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-http_2.12" % akkaHttpVersion,
    "com.typesafe.akka" % "akka-http-testkit_2.12" % "10.0.11" % Test,
    "com.typesafe.akka" % "akka-http-spray-json_2.12" % akkaHttpVersion,
    // mysql
    "com.typesafe.slick" % "slick_2.12" % "3.2.1",
    "com.typesafe.slick" % "slick-hikaricp_2.12" % "3.2.1",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "mysql" % "mysql-connector-java" % "5.0.8",
    // test
    "org.scalatest" % "scalatest_2.12" % "3.0.4",
    // file
    "com.github.pathikrit" % "better-files_2.12" % "3.4.0",
    // serialize
    "me.chrons" % "boopickle_2.12" % "1.2.5",
    // json (https://circe.github.io/circe)
    "io.circe" % "circe-core_2.12" % circeVersion,
    "io.circe" % "circe-generic_2.12" % circeVersion,
    "io.circe" % "circe-parser_2.12" % circeVersion,
    "io.circe" % "circe-optics_2.12" % circeVersion
  )
}