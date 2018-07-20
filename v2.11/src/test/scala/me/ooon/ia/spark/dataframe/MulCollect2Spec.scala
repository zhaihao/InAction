/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe

import me.ooon.ia.spark.SparkBaseSpec
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
  * MulCollect2Spec
  *
  * @author zhaihao
  * @version 1.0 2018/7/20 09:19
  */
class MulCollect2Spec extends SparkBaseSpec {

  "test" in {
    import spark.implicits._

    val df = Seq(
      (1.1, 1.1, 1.1, 1.1),
      (2.2, 2.2, 2.2, 2.2),
      (3.3, 3.3, 3.3, 3.3),
      (4.4, 4.4, 4.4, 4.4),
      (5.5, 5.5, 5.5, 5.5),
      (6.6, 6.6, 6.6, 6.6)
    ).toDF("f1", "f2", "f3", "f4")

    df.repartition(6).createOrReplaceTempView("t")

    spark.udf.register("list", new CollectUDAFHolder)

    spark.sql("select list(f1,f2,f3,f4) from t").show(false)
  }

}

class CollectUDAFHolder extends UserDefinedAggregateFunction {
  override def inputSchema =
    StructType(
      Seq(StructField("f1", DoubleType),
          StructField("f2", DoubleType),
          StructField("f3", DoubleType),
          StructField("f4", DoubleType)))

  override def bufferSchema =
    StructType(
      Seq(
        StructField("fs1", ArrayType(DoubleType)),
        StructField("fs2", ArrayType(DoubleType)),
        StructField("fs3", ArrayType(DoubleType)),
        StructField("fs4", ArrayType(DoubleType))
      ))

  override def dataType = ArrayType(StringType)

  override def deterministic = true

  override def initialize(buffer: MutableAggregationBuffer) = {
    buffer.update(0, Seq.empty[Double])
    buffer.update(1, Seq.empty[Double])
    buffer.update(2, Seq.empty[Double])
    buffer.update(3, Seq.empty[Double])
  }

  override def update(buffer: MutableAggregationBuffer, input: Row) = {
    var i = 0
    while (i <= 3) {
      val a = buffer.getAs[Seq[Double]](i)
      val b = input.getAs[Double](i)
      buffer(i) = a.+:(b)
      i += 1
    }
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row) = {
    var i = 0
    while (i <= 3) {
      val a = buffer1.getAs[Seq[Double]](i)
      val b = buffer2.getAs[Seq[Double]](i)
      buffer1(i) = a.++:(b)
      i += 1
    }
  }

  override def evaluate(buffer: Row) = {
    buffer.toSeq.asInstanceOf[Seq[Seq[Double]]].map(_.mkString("|"))
  }
}
