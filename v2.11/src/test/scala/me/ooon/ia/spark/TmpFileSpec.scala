/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark

import java.io.File
import me.ooon.orison.base.syntax.file._

/**
  * TmpFileSpec
  *
  * @author zhaihao
  * @version 1.0 2018/5/30 15:35
  */
trait TmpFileSpec {
  val TmpFile = "tmp"
  val file    = new File(TmpFile)

  if (file.exists()) file.deleteRecursively
}
