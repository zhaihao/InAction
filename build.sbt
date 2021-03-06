import Dependencies._
import sbt.Keys.excludeDependencies

lazy val CommonSettings = Seq(
  organization := "me.ooon",
  name         := "scalaia",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  resolvers += Resolver.url("ooon ivy repo", url("http://repo.ooon.me/release"))(
    Resolver.ivyStylePatterns),
  externalResolvers := Resolver
    .combineDefaultResolvers(resolvers.value.toVector, mavenCentral = true),
  libraryDependencies ++= Seq(
    base,
    scalatest,
  )
)

CommonSettings

lazy val `v2-12` = (project in file("v2.12"))
  .settings(CommonSettings)
  .settings(
    version      := "1.0.0",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      upickle,
      mysql,
      h2,
      better_file,
      playIteratees,
      playIterateesReactiveStreams,
      scraper
    ),
    libraryDependencies ++= Seq(spark, cats, slick, akka, log, breeze).flatten,
    excludeDependencies ++= excludes,
    dependencyOverrides ++= overrides
  )
