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

  val spark_version = "2.2.0"
  val akka_version  = "2.5.14"

  lazy val scopt                        = "com.github.scopt"           %% "scopt"                           % "3.7.0"
  lazy val base                         = "me.ooon"                    %% "base"                            % "1.0.25"
  lazy val scalatest                    = "org.scalatest"              %% "scalatest"                       % "3.0.4" % Test
  lazy val spark_core                   = "org.apache.spark"           %% "spark-core"                      % spark_version
  lazy val spark_sql                    = "org.apache.spark"           %% "spark-sql"                       % spark_version
  lazy val spark_mllib                  = "org.apache.spark"           %% "spark-mllib"                     % spark_version
  lazy val spark_streaming              = "org.apache.spark"           %% "spark-streaming"                 % spark_version
  lazy val graphframes                  = "graphframes"                % "graphframes"                      % "0.6.0-spark2.2-s_2.11"
  lazy val vegas                        = "org.vegas-viz"              %% "vegas"                           % "0.3.11"
  lazy val vegas_spark                  = "org.vegas-viz"              %% "vegas-spark"                     % "0.3.11"
  lazy val redisclient                  = "net.debasishg"              %% "redisclient"                     % "3.4"
  lazy val mysql                        = "mysql"                      % "mysql-connector-java"             % "6.0.6"
  lazy val upickle                      = "com.lihaoyi"                %% "upickle"                         % "0.6.5"
  lazy val slick                        = "com.typesafe.slick"         %% "slick"                           % "3.2.2"
  lazy val slick_hikaricp               = "com.typesafe.slick"         %% "slick-hikaricp"                  % "3.2.2"
  lazy val slick_codegen                = "com.typesafe.slick"         %% "slick-codegen"                   % "3.2.2"
  lazy val scala_logging                = "com.typesafe.scala-logging" %% "scala-logging"                   % "3.8.0"
  lazy val h2                           = "com.h2database"             % "h2"                               % "1.4.197" % Test
  lazy val logback                      = "ch.qos.logback"             % "logback-classic"                  % "1.2.3"
  lazy val akka_actor                   = "com.typesafe.akka"          %% "akka-actor"                      % akka_version
  lazy val akka_cluster                 = "com.typesafe.akka"          %% "akka-cluster"                    % akka_version
  lazy val akka_persistence             = "com.typesafe.akka"          %% "akka-persistence"                % akka_version
  lazy val akka_testkit                 = "com.typesafe.akka"          %% "akka-testkit"                    % akka_version % Test
  lazy val better_file                  = "com.github.pathikrit"       %% "better-files"                    % "3.6.0"
  lazy val playIteratees                = "com.typesafe.play"          %% "play-iteratees"                  % "2.6.1"
  lazy val playIterateesReactiveStreams = "com.typesafe.play"          %% "play-iteratees-reactive-streams" % "2.6.1"
}
