enablePlugins(org.nlogo.build.NetLogoExtension)

name := "web"

netLogoClassManager := "org.nlogo.extensions.web.WebExtension"

netLogoVersion := "6.0.2-M1"

scalaVersion := "2.12.2"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii", "-language:_")

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "org.apache.httpcomponents" % "httpmime" % "4.2",
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "org.picocontainer" % "picocontainer" % "2.13.6",
  "log4j" % "log4j" % "1.2.17"
)

