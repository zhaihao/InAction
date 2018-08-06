/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.dataframe.udaf

import me.ooon.ia.spark.SparkBaseSpec
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}

import scala.reflect.runtime.universe._
import org.apache.spark.sql.catalyst.ScalaReflection.schemaFor
import org.apache.spark.sql.types.StructType

/**
  * GenericUDAFSpec
  *
  * @author zhaihao
  * @version 1.0 2018/8/6 17:47
  */
class GenericUDAFSpec extends SparkBaseSpec {
  import spark.implicits._

  Seq(("tom", 1), ("Lucy", 2), ("Jack", 3)).toDF("name", "age").createOrReplaceTempView("t1")

  val df2 = Seq(("tom", 1.0), ("Lucy", 2.0), ("Jack", 3.0))
    .toDF("name", "score")
    .createOrReplaceTempView("t2")

  "int" in {
    spark.udf.register("my_sum", new MySum[Int])
    spark.sql("select my_sum(age) from t1").show()
  }

  "double" in {
    spark.udf.register("my_sum", new MySum[Double])
    spark.sql("select my_sum(age) from t1").show()
  }
}

case class MySum[T: TypeTag](implicit n: Numeric[T]) extends UserDefinedAggregateFunction {

  val dt = schemaFor[T].dataType

  override def inputSchema = new StructType().add("x", dt)

  override def bufferSchema = new StructType().add("x", dt)

  override def dataType = dt

  override def deterministic = true

  override def initialize(buffer: MutableAggregationBuffer) = buffer.update(0, n.zero)

  override def update(buffer: MutableAggregationBuffer, input: Row) =
    if (!input.isNullAt(0))
      buffer.update(0, n.plus(buffer.getAs[T](0), input.getAs[T](0)))

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row) =
    buffer1.update(0, n.plus(buffer1.getAs[T](0), buffer2.getAs[T](0)))

  override def evaluate(buffer: Row) = buffer.getAs[T](0)
}
