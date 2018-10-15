/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.better.file

import me.ooon.base.test.{BaseSpec, TestFileLike}
import better.files._

/**
  * SimpleIOSpec
  *
  * @author zhaihao
  * @version 1.0 2018/8/1 14:22
  */
class SimpleIOSpec extends BaseSpec with TestFileLike {

  "read and write" in {
    val file = testFile.toScala
    file.overwrite("world")
    file.contentAsString ==> "world"

    file.write("hello")
    file.contentAsString ==> "hello"

    file.appendLine() // \n
    file.append("world")
    file.contentAsString ==> "hello\nworld"
  }

  "pipeline" in {
    import Dsl._
    val file = testFile.toScala
    file < "world"
    file.contentAsString ==> "world"

    file < "hello"
    file.contentAsString ==> "hello"

    file << " world"
    file.contentAsString ==> "hello world\n"
  }

  "fluent chain" in {
    import File._
    val file = testFile.toScala

    val f1 = file
      .createIfNotExists()
      .appendLine()
      .appendLines("My name is", "Tom Jason")
      .moveToDirectory(home / "Downloads")
      .renameTo("test.data")
      .changeExtensionTo(".txt")

    (home / "Downloads" / "test.txt").contentAsString ==> "\nMy name is\nTom Jason\n"

    f1.deleteOnExit()
  }

}
