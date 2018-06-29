/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.scopt

import me.ooon.orison.base.test.BaseSpec
import me.ooon.orison.base.syntax.string._
import scala.language.postfixOps

/**
  * EnumSpec
  *
  * @author zhaihao
  * @version 1.0 2018/6/28 15:48
  */
class EnumSpec extends BaseSpec {

  "enum support" - {
    "ok" in {
      val args = Array("-e", "dev")
      ConfigEnv.parser.parse(args, ConfigEnv()) ==> Some(ConfigEnv(Env.dev))
    }

    "error" in {
      val args = Array("-e", "dev1")
      ConfigEnv.parser.parse(args, ConfigEnv()) ==> None
    }

    "help" in {
      val args = Array("--help")
      ConfigEnv.parser.parse(args, ConfigEnv())
    }
  }
}

case class ConfigEnv(env: Env.Env = Env.test)

object ConfigEnv {
  implicit val envRead: scopt.Read[Env.Value] = scopt.Read.reads(Env withName)

  val parser = new scopt.OptionParser[ConfigEnv]("scopt") {
    head("So very scopt", "3.x")
    help("help") text "display this message"
    version("version") text "display version info".newline

    opt[Env.Value]('e', "env") action { (x, c) =>
      c.copy(env = x)
    } text s"The possible values can be ${Env.values.mkString("\"", "\", \"", "\"")}"
  }
}

object Env extends Enumeration {
  type Env = Value
  val test, dev, prod = Value
}
