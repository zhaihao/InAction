import Dependencies._

lazy val CommonSettings = Seq(
  organization := "me.ooon",
  name         := "scalaia",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
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
      scala_logging,
      logback,
      better_file,
      playIteratees,
      playIterateesReactiveStreams,
      scraper
    ),
    libraryDependencies ++= cats,
    libraryDependencies ++= slick,
    libraryDependencies ++= akka,
    libraryDependencies ++= breeze
  )

lazy val `v2-11` = (project in file("v2.11"))
  .settings(CommonSettings)
  .settings(
    version      := "1.0.0",
    scalaVersion := "2.11.11",
    libraryDependencies ++= spark,
    libraryDependencies ++= Seq(
      xgboost_spark,
      graphframes,
      vegas,
      vegas_spark,
      redisclient,
      mysql
    )
  )
