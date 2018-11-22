/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

import sbt._

/**
  * Dependencies
  *
  * @author zhaihao
  * @version 1.0 2018/6/14 17:27
  */
object Dependencies {

  val spark_version = "2.3.2"
  val akka_version  = "2.5.14"

  lazy val spark = Seq(
    "org.apache.spark" %% "spark-core"      % spark_version,
    "org.apache.spark" %% "spark-sql"       % spark_version,
    "org.apache.spark" %% "spark-mllib"     % spark_version,
    "org.apache.spark" %% "spark-streaming" % spark_version
  )

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor"       % akka_version,
    "com.typesafe.akka" %% "akka-cluster"     % akka_version,
    "com.typesafe.akka" %% "akka-persistence" % akka_version,
    "com.typesafe.akka" %% "akka-testkit"     % akka_version % Test
  )

  lazy val slick = Seq(
    "com.typesafe.slick" %% "slick"          % "3.2.2",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.2",
    "com.typesafe.slick" %% "slick-codegen"  % "3.2.2"
  )

  lazy val breeze = Seq(
    "org.scalanlp" %% "breeze"         % "1.0-RC2",
    "org.scalanlp" %% "breeze-natives" % "1.0-RC2",
    "org.scalanlp" %% "breeze-viz"     % "1.0-RC2"
  )

  lazy val log = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.0",
    "ch.qos.logback"             % "logback-classic" % "1.2.3"
  )

  lazy val cats = Seq(
    "org.typelevel" %% "cats-core" % "1.4.0"
  )

  lazy val base                         = "me.ooon"              %% "base"                            % "1.0.27"
  lazy val scalatest                    = "org.scalatest"        %% "scalatest"                       % "3.0.4" % Test
  lazy val xgboost_spark                = "ml.dmlc"              % "xgboost4j-spark"                  % "0.80"
  lazy val graphframes                  = "org.graphframes"      % "graphframes_2.11"                 % "0.7.0-spark2.3-SNAPSHOT"
  lazy val vegas                        = "org.vegas-viz"        %% "vegas"                           % "9.3.12"
  lazy val vegas_spark                  = "org.vegas-viz"        %% "vegas-spark"                     % "9.3.12"
  lazy val redisclient                  = "net.debasishg"        %% "redisclient"                     % "3.4"
  lazy val mysql                        = "mysql"                % "mysql-connector-java"             % "6.0.6"
  lazy val upickle                      = "com.lihaoyi"          %% "upickle"                         % "0.6.5"
  lazy val scraper                      = "net.ruippeixotog"     %% "scala-scraper"                   % "2.1.0"
  lazy val h2                           = "com.h2database"       % "h2"                               % "1.4.197" % Test
  lazy val better_file                  = "com.github.pathikrit" %% "better-files"                    % "3.6.0"
  lazy val playIteratees                = "com.typesafe.play"    %% "play-iteratees"                  % "2.6.1"
  lazy val playIterateesReactiveStreams = "com.typesafe.play"    %% "play-iteratees-reactive-streams" % "2.6.1"
  lazy val play_json                    = "com.typesafe.play"    %% "play-json"                       % "2.6.10"
  lazy val ujson                        = "com.lihaoyi"          %% "ujson"                           % "0.7.1"

  val exclude = Seq(
    ExclusionRule("com.sun.jersey"),
    ExclusionRule("com.sun.jersey.contribs"),
    ExclusionRule("javax.servlet.jsp", "jsp-api"),
    ExclusionRule("javax.servlet", "servlet-api"),
    ExclusionRule("org.mortbay.jetty", "servlet-api"),
    ExclusionRule("org.slf4j", "slf4j-log4j12"),
    ExclusionRule("log4j", "log4j")
  )

}
