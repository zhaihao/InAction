/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.better.file

import me.ooon.orison.base.test.BaseSpec

/**
  * InstantiationSpec
  *
  * @author zhaihao
  * @version 1.0 2018/7/30 14:00
  */
class InstantiationSpec extends BaseSpec {

  import better.files._
  import java.io.{File => JFile}

  val name = "zhaihao"
  val path = "/Users/zhaihao/Code"
  val file = File(path) // using constructor

  "using string interpolator" in {
    val file2 = file"/Users/$name/Code"
    file2 ==> file
  }

  "convert a string path to a file" in {
    val file2 = path.toFile
    file2 ==> file
  }

  "convert a Java file to Scala" in {
    val file2 = new JFile(path).toScala
    file2 ==> file
  }

  "using root helper to start from root" in {
    import File._
    val file2 = root / "Users" / name / "Code"
    file2 ==> file
  }

  "using home helper to start from home" in {
    import File._
    val file2 = home / "Code"
    file2 ==> file
  }

  "using file separator DSL" in {
    val file2 = "/Users" / name / "Code"
    file2 ==> file
  }

  "use `..` to navigate up to parent" in {
    import File._
    import Dsl._
    val file2 = home / "Code" / "presentations" / `..`
    file2 ==> file
  }

  "using Symbols instead of Strings" in {
    import File._
    val file2 = root / 'Users / name / 'Code
    file2 ==> file
  }

}
