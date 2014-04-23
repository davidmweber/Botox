import com.typesafe.sbt.packager.Keys._

name := "Botox"

version := "0.0.1"

val akkaVersion = "2.3+"
val scalaTestVersion = "2.1.2"

packageArchetype.java_application

packageDescription in Debian := "Botox GSM modem server"

maintainer in Debian := "David Weber"

scalacOptions ++= Seq("-feature", "-language:postfixOps")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

