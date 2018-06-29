import Dependencies._

lazy val CommonSettings = Seq(
  organization := "me.ooon",
  name         := "ScalaInAction",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
  resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  resolvers += Resolver.url("ooon ivy repo", url("http://repo.ooon.me/release"))(
    Resolver.ivyStylePatterns),
  externalResolvers := Resolver
    .combineDefaultResolvers(resolvers.value.toVector, mavenCentral = true),
  libraryDependencies ++= Seq(
    orison,
    scalatest,
  )
)

CommonSettings

lazy val `v2-12` = (project in file("v2.12"))
  .settings(CommonSettings)
  .settings(
    version      := "1.0.0",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      upickle,
      scopt,
      mysql,
      h2,
      slick,
      slick_codegen,
      slick_hikaricp,
      scala_logging,
      logback
    )
  )

lazy val `v2-11` = (project in file("v2.11"))
  .settings(CommonSettings)
  .settings(
    version      := "1.0.0",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Seq(
      spark_core,
      spark_sql,
      spark_mllib,
      spark_streaming,
      vegas,
      vegas_spark,
      redisclient,
      mysql
    )
  )
