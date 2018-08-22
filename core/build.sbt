name := "core"

version := "1.0-SNAPSHOT"


libraryDependencies ++= {
  val hbaseVersion = "1.3.1"
  val akkaVersion = "2.5.4"
  val akkaHttpVersion = "10.0.11"
  val log4jVersion = "2.6.2"
  val slf4jVersion = "1.7.2"

  Seq(
    // ------------ scala dependencies -----------
    // akka
    "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-remote_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-http_2.12" % akkaHttpVersion,
    "com.typesafe.akka" % "akka-http-testkit_2.12" % akkaHttpVersion % Test,
    "com.typesafe.akka" % "akka-http-spray-json_2.12" % akkaHttpVersion,
    "com.typesafe.akka" % "akka-cluster_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-stream-testkit_2.12" % akkaVersion % Test,
    "com.typesafe.akka" % "akka-persistence_2.12" % akkaVersion,
    // kafka
    "org.apache.kafka" % "kafka_2.12" % "1.0.0",
    // http client
    "org.scalaj" % "scalaj-http_2.12" % "2.3.0",
    //"com.thoughtworks.feature" % "caller_2.11" % "2.1.0-M0",
    // parse HTML
    "net.ruippeixotog" % "scala-scraper_2.12" % "2.1.0",
    // inject
    "net.codingwell" %% "scala-guice" % "4.1.1"
  )
}