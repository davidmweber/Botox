name := "Botox"

version := "0.0.1"

val akkaVersion = "2.3+"
val scalaTestVersion = "2.1.2"

scalacOptions ++= Seq("-feature", "-language:postfixOps")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion,

