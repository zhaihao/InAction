/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec

/**
  * NullSpec
  *
  * @author zhaihao
  * @version 1.0 2017/5/12 14:40
  */
class NullSpec extends SparkBaseSpec {

  "spark 可以正确处理 None" in {
    import spark.implicits._
    Seq((1, Some("a")), (2, Some("b")), (3, None)).toDF("id", "name").show
    Seq((1, "a"), (2, "b"), (3, null)).toDF("id", "name").show
  }

}
