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

  val scopt           = "com.github.scopt"           %% "scopt"               % "3.7.0"
  val orison          = "me.ooon"                    %% "orison-base"         % "1.0.20"
  val scalatest       = "org.scalatest"              %% "scalatest"           % "3.0.4" % Test
  val spark_core      = "org.apache.spark"           %% "spark-core"          % spark_version
  val spark_sql       = "org.apache.spark"           %% "spark-sql"           % spark_version
  val spark_mllib     = "org.apache.spark"           %% "spark-mllib"         % spark_version
  val spark_streaming = "org.apache.spark"           %% "spark-streaming"     % spark_version
  val vegas           = "org.vegas-viz"              %% "vegas"               % "0.3.11"
  val vegas_spark     = "org.vegas-viz"              %% "vegas-spark"         % "0.3.11"
  val redisclient     = "net.debasishg"              %% "redisclient"         % "3.4"
  val mysql           = "mysql"                      % "mysql-connector-java" % "6.0.6"
  val upickle         = "com.lihaoyi"                %% "upickle"             % "0.6.5"
  val slick           = "com.typesafe.slick"         %% "slick"               % "3.2.2"
  val slick_hikaricp  = "com.typesafe.slick"         %% "slick-hikaricp"      % "3.2.2"
  val slick_codegen   = "com.typesafe.slick"         %% "slick-codegen"       % "3.2.2"
  val scala_logging   = "com.typesafe.scala-logging" %% "scala-logging"       % "3.8.0"
  val h2              = "com.h2database"             % "h2"                   % "1.4.197" % Test
  val logback         = "ch.qos.logback"             % "logback-classic"      % "1.2.3"

}
