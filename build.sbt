organization in ThisBuild := "com.ligx"

name := "scala-in-action"

version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.3"

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val core = Project(id = "core", base = file("core")).dependsOn(macros)
lazy val macros = Project(id = "macros", base = file("macros"))
lazy val root = Project(id = "root", base = file(".")).aggregate(core, macros)

