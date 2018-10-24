import Dependencies._

lazy val CommonSettings = Seq(
  organization := "me.ooon",
  name         := "scalaia",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
  resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  resolvers += "spark-packages" at "https://dl.bintray.com/spark-packages/maven",
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
      logback,
      akka_actor,
      akka_testkit,
      akka_persistence,
      akka_cluster,
      better_file,
      playIteratees,
      playIterateesReactiveStreams
    ),
    libraryDependencies ++= cats
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
      graphframes,
      vegas,
      vegas_spark,
      redisclient,
      mysql
    )
  )
