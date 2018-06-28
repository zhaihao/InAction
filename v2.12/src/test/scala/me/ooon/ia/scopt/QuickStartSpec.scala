/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.scopt

import me.ooon.orison.base.test.BaseSpec
import me.ooon.orison.base.syntax.string._

/**
  * QuickStartSpec
  *
  * @author zhaihao
  * @version 1.0 2018/6/14 17:53
  */
class QuickStartSpec extends BaseSpec {
  "help" in {
    val args = Array("--help")
    Config.parser.parse(args, Config())
  }
}

import java.io.File
case class Config(foo:       Int = -1,
                  out:       File = new File("."),
                  xyz:       Boolean = false,
                  libName:   String = "",
                  maxCount:  Int = -1,
                  verbose:   Boolean = false,
                  debug:     Boolean = false,
                  mode:      String = "",
                  files:     Seq[File] = Seq(),
                  keepAlive: Boolean = false,
                  jars:      Seq[File] = Seq(),
                  kwArgs:    Map[String, String] = Map())

object Config {
  val parser = new scopt.OptionParser[Config]("scopt") {
    head("scopt", "3.x")

    opt[Int]('f', "foo").action((x, c) => c.copy(foo = x)).text("foo is an integer property")

    opt[File]('o', "out")
      .required()
      .valueName("<file>")
      .action((x, c) => c.copy(out = x))
      .text("out is a required file property")

    opt[(String, Int)]("max")
      .action({
        case ((k, v), c) => c.copy(libName = k, maxCount = v)
      })
      .validate(x =>
        if (x._2 > 0) success
        else failure("Value <max> must be >0"))
      .keyValueName("<libname>", "<max>")
      .text("maximum count for <libname>")

    opt[Seq[File]]('j', "jars")
      .valueName("<jar1>,<jar2>...")
      .action((x, c) => c.copy(jars = x))
      .text("jars to include")

    opt[Map[String, String]]("kwargs")
      .valueName("k1=v1,k2=v2...")
      .action((x, c) => c.copy(kwArgs = x))
      .text("other arguments")

    opt[Unit]("verbose").action((_, c) => c.copy(verbose = true)).text("verbose is a flag")

    opt[Unit]("debug")
      .hidden()
      .action((_, c) => c.copy(debug = true))
      .text("this option is hidden in the usage text")

    help("help").text("prints this usage text")

    version("version").text("show the version")

    arg[File]("<file>...")
      .unbounded()
      .optional()
      .action((x, c) => c.copy(files = c.files :+ x))
      .text("optional unbounded args")

    note("some notes.".newline)

    cmd("update")
      .action((_, c) => c.copy(mode = "update"))
      .text("update is a command.")
      .children(
        opt[Unit]("not-keepalive")
          .abbr("nk")
          .action((_, c) => c.copy(keepAlive = false))
          .text("disable keepalive"),
        opt[Boolean]("xyz").action((x, c) => c.copy(xyz = x)).text("xyz is a boolean property"),
        opt[Unit]("debug-update")
          .hidden()
          .action((_, c) => c.copy(debug = true))
          .text("this option is hidden in the usage text"),
        checkConfig(
          c =>
            if (c.keepAlive && c.xyz) failure("xyz cannot keep alive")
            else success)
      )
  }
}
