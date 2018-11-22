/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package org.apache.spark.sql.syntax
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.Dataset

import scala.language.implicitConversions

/**
  * DatasetOps
  *
  * @author zhaihao
  * @version 1.0 2018/10/17 17:13
  */
final class DatasetOps[T](dataset: Dataset[T]) extends StrictLogging {

  /**
    * log 需要开启 org.apache.spark.sql.syntax 的 logger 级别是 info
    *
    * spark show 日志输出在 std，故用 log 输出到 logback 中
    */
  def log(numRows: Int = 20, truncate: Int = 20, vertical: Boolean = false) =
    logger.info("\n" + dataset.showString(numRows, truncate, vertical))
}

trait ToDatasetOps {
  @inline implicit def toDatasetOps[T](dataset: Dataset[T]) = new DatasetOps[T](dataset)
}
