organization := "com.ligx"

name := "scala-in-action"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.3"

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"

libraryDependencies ++= {
  val hbaseVersion = "1.3.1"
  val akkaVersion = "2.5.4"
  val akkaHttpVersion = "2.4.11.2"

  Seq(
    // akka
    "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-remote_2.11" % "2.4.11",
    "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaHttpVersion,
    "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaHttpVersion,
    "com.typesafe.akka" % "akka-cluster_2.11" % akkaVersion,
    "org.apache.kafka" % "kafka_2.11" % "0.9.0.1",
    // mysql
    "com.typesafe.slick" % "slick_2.12" % "3.2.1",
    "mysql" % "mysql-connector-java" % "5.0.8",
    // thrift
    "org.apache.thrift" % "libthrift" % "0.9.3",
    // json
    "com.alibaba" % "fastjson" % "1.2.12",
    "org.fusesource.jansi" % "jansi" % "1.13",
    // 压缩工具
    "org.xerial.snappy" % "snappy-java" % "1.1.2.6",
    "org.apache.commons" % "commons-compress" % "1.13",
    "org.anarres.lzo" % "lzo-core" % "1.0.5",
    "net.jpountz.lz4" % "lz4" % "1.3.0",
    // common tool
    "commons-io" % "commons-io" % "2.5",
    "commons-codec" % "commons-codec" % "1.10",
    "commons-collections" % "commons-collections" % "3.2",
    "org.apache.commons" % "commons-lang3" % "3.5",
    "commons-lang" % "commons-lang" % "2.6",
    // http client
    "org.scalaj" % "scalaj-http_2.11" % "2.3.0",
    "org.apache.httpcomponents" % "httpclient" % "4.3.4",
    // RxJava2
    "io.reactivex" % "rxjava" % "1.3.0",
    // test
    "junit" % "junit" % "4.12" % "test",
    "org.scalatest" % "scalatest_2.11" % "3.0.0-M15",
    // RocksDB
    "org.rocksdb" % "rocksdbjni" % "5.7.2",
    // ElasticSearch
    "org.elasticsearch.client" % "transport" % "5.2.2",
    "org.elasticsearch.client" % "transport" % "5.2.2",
    // HBase
    "org.apache.hbase" % "hbase-client" % hbaseVersion,
    "org.apache.hbase" % "hbase-common" % hbaseVersion,
    "org.apache.hbase" % "hbase-server" % hbaseVersion,
    "org.apache.hadoop" % "hadoop-common" % "2.8.1",
    "com.google.protobuf" % "protobuf-java" % "2.5.0",
    // Zookeeper
    "org.apache.zookeeper" % "zookeeper" % "3.4.5",
    "com.thoughtworks.feature" % "caller_2.11" % "2.1.0-M0",
    // lombok
    "org.projectlombok" % "lombok" % "1.16.10",
    // protostuff
    "io.protostuff" % "protostuff-core" % "1.6.0",
    "io.protostuff" % "protostuff-runtime" % "1.6.0",
    // reflection
    "org.objenesis" % "objenesis" % "2.6"
  )
}