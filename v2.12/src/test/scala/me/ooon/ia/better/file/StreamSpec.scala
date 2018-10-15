/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.better.file

import me.ooon.base.test.{BaseSpec, TestFileLike}

/**
  * StreamSpec
  *
  * @author zhaihao
  * @version 1.0 2018/8/2 13:35
  */
class StreamSpec extends BaseSpec with TestFileLike {
  import better.files._

  "bytes" in {
    val file = testFile.toScala
    file.append("hello ")
    val bytes = file.bytes
    bytes.foreach(i => print(i + " "))

    file.writeBytes(bytes)

    file.contentAsString ==> "hello  hello"

  }

  "chars" in {
    val file = testFile.toScala
    file.append("hello")

    file.chars.foreach(i => print(i + " "))
  }

  "lines" in {
    val file = testFile.toScala
    file.append("hello\n")
    file.append("world")

    file.lines.foreach(i => print(i + " "))
  }

}
