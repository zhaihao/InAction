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
    "me.ooon"       %% "orison-base" % "1.0.19",
    "org.scalatest" %% "scalatest"   % "3.0.4" % Test
  )
)

CommonSettings

lazy val `v2-12` = (project in file("v2.12"))
  .settings(CommonSettings)
  .settings(
    version      := "1.0.0",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.6.5"
    )
  )

lazy val `v2-11` = (project in file("v2.11"))
  .settings(CommonSettings)
  .settings(
    version      := "1.0.0",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"          % spark_version,
      "org.apache.spark" %% "spark-sql"           % spark_version,
      "org.apache.spark" %% "spark-mllib"         % spark_version,
      "org.apache.spark" %% "spark-streaming"     % spark_version,
      "org.vegas-viz"    %% "vegas"               % "0.3.11",
      "org.vegas-viz"    %% "vegas-spark"         % "0.3.11",
      "net.debasishg"    %% "redisclient"         % "3.4",
      "mysql"            % "mysql-connector-java" % "6.0.6"
    )
  )

val spark_version = "2.2.0"
