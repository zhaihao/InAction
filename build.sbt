name := "InAction"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
resolvers += Resolver.url("ooon ivy repo", url("http://repo.ooon.me/release"))(
  Resolver.ivyStylePatterns)
externalResolvers := Resolver.combineDefaultResolvers(resolvers.value.toVector, mavenCentral = true)

libraryDependencies ++= Seq(
  "me.ooon"       %% "orison-base" % "1.0.19",
  "com.lihaoyi"   %% "upickle"     % "0.6.5",
  "org.scalatest" %% "scalatest"   % "3.0.4" % Test
)
