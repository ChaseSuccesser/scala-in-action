organization := "com.ligx"

name := "scala-in-action"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  // akka
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "com.typesafe.akka" %% "akka-remote" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.11",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.11",
  "org.apache.kafka" %% "kafka" % "0.9.0.1",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "mysql" % "mysql-connector-java" % "5.0.8",
  "org.scalatest" %% "scalatest" % "3.0.0-M15",
  "org.apache.thrift" % "libthrift" % "0.9.3",
  "com.alibaba" % "fastjson" % "1.2.12",
  "org.fusesource.jansi" % "jansi" % "1.13",
  // 压缩工具
  "org.xerial.snappy" % "snappy-java" % "1.1.2.6",
  "org.apache.commons" % "commons-compress" % "1.13",
  "org.anarres.lzo" % "lzo-core" % "1.0.5",
  "net.jpountz.lz4" % "lz4" % "1.3.0",
  // common tool
  "commons-io" % "commons-io" % "2.5",
  "commons-codec" % "commons-codec" % "1.10"
)