/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.mllib.datatype

import me.ooon.ia.spark.SparkBaseSpec
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD

/**
  * DistributedMatrixSpec
  * distributed matrix 的列和行元素个数都是[[Long.MaxValue]],值也是[[Double]]类型的
  *
  * MLlib提供了三种分布式矩阵的存储方案：行矩阵RowMatrix，索引行矩阵IndexedRowMatrix、坐标矩阵CoordinateMatrix和分块矩阵Block Matrix。
  * 它们都属于org.apache.spark.mllib.linalg.distributed包。
  *
  * 不同类型的分布式矩阵之间的转换需要进行一个全局的shuffle操作，非常耗费资源。所以，根据数据本身的性质和应用需求来选取恰当的分布式矩阵存储类型是非常重要的。
  *
  * @author zhaihao
  * @version 1.0 2017/5/4 22:07
  */
class DistributedMatrixSpec extends SparkBaseSpec {

  /**
    * 行矩阵RowMatrix是最基础的分布式矩阵类型。
    * 每行是一个本地向量，行索引无实际意义（即无法直接使用）。
    * 数据存储在一个由行组成的RDD中，其中每一行都使用一个本地向量来进行存储。
    * 由于行是通过本地向量来实现的，故列数（即行的维度）被限制在普通整型（integer）的范围内。
    * 在实际使用时，由于单机处理本地向量的存储和通信代价，行维度更是需要被控制在一个更小的范围之内
    */
  "RowMatrix Api" in {
    val row1   = Vectors.dense(1, 2, 3)
    val row2   = Vectors.dense(2, 3, 4)
    val rdd    = sc.parallelize(Array(row1, row2))
    val matrix = new RowMatrix(rdd)
    assert(matrix.numCols() <= Int.MaxValue)

    println(s"row size = ${matrix.numRows()}")
    println(s"col size = ${matrix.numCols()}")
  }

  "矩阵的统计摘要信息" in {
    val lps: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data.txt")
    val rdd                     = lps.map(_.features)
    val matrix                  = new RowMatrix(rdd)
    val columnSummaryStatistics = matrix.computeColumnSummaryStatistics()
    println(s"count: ${columnSummaryStatistics.count}")
    println(s"max: ${columnSummaryStatistics.max}")
    println(s"min: ${columnSummaryStatistics.min}")
    println(s"mean: ${columnSummaryStatistics.mean}")
    println(s"variance: ${columnSummaryStatistics.variance}")
    println(s"normL1: ${columnSummaryStatistics.normL1}")
    println(s"normL2: ${columnSummaryStatistics.normL2}")
  }

  /**
    * 索引行矩阵IndexedRowMatrix与RowMatrix相似，
    * 但它的每一行都带有一个有意义的行索引值，这个索引值可以被用来识别不同行，或是进行诸如join之类的操作。
    * 其数据存储在一个由IndexedRow组成的RDD里，即每一行都是一个带长整型索引的本地向量。
    * 与RowMatrix类似，IndexedRowMatrix的实例可以通过RDD[IndexedRow]实例来创建
    */
  "IndexedRowMatrix" in {
    val dv     = Vectors.dense(1, 2, 3)
    val dv1    = Vectors.dense(4, 5, 6)
    val row    = IndexedRow(1, dv)
    val row1   = IndexedRow(2, dv1)
    val rdd1   = sc.parallelize(Array(row, row1))
    val matrix = new IndexedRowMatrix(rdd1)
    matrix.rows.foreach(println)
  }

  /**
    * 坐标矩阵CoordinateMatrix是一个基于矩阵项构成的RDD的分布式矩阵。
    * 每一个矩阵项MatrixEntry都是一个三元组：(i: Long, j: Long, value: Double)，其中i是行索引，j是列索引，value是该位置的值。
    * 坐标矩阵一般在矩阵的两个维度都很大，且矩阵非常稀疏的时候使用。
    * CoordinateMatrix实例可通过RDD[MatrixEntry]实例来创建，其中每一个矩阵项都是一个(rowIndex, colIndex, elem)的三元组：
    */
  "CoordinateMatrix" - {
    "create" in {
      val entry  = MatrixEntry(0, 1, 1.0)
      val entry1 = MatrixEntry(2, 2, 2.0)
      val rdd1   = sc.parallelize(Array(entry, entry1))
      val matrix = new CoordinateMatrix(rdd1)
      matrix.entries.foreach(println)
      val tm = matrix.transpose() // 转置
      tm.entries.foreach(println)
    }

    "create via df" in {
      import spark.implicits._

      val df = List(
        (1, 2, 3.0),
        (1, 3, 4.0),
        (2, 5, 2.0)
      ).toDF("id1", "id2", "val")

      df.show()

      val rdd: RDD[MatrixEntry] =
        df.rdd.map(r => MatrixEntry(r.getInt(0), r.getInt(1), r.getDouble(2)))
      val tm = new CoordinateMatrix(rdd)
      tm.entries.foreach(println)
      println(tm.numCols())
      println(tm.numRows())
    }
  }

  /**
    * 分块矩阵是基于矩阵块MatrixBlock构成的RDD的分布式矩阵，其中每一个矩阵块MatrixBlock都是一个元组((Int, Int), Matrix)，
    * 其中(Int, Int)是块的索引，而Matrix则是在对应位置的子矩阵（sub-matrix），其尺寸由rowsPerBlock和colsPerBlock决定，默认值均为1024。
    * 分块矩阵支持和另一个分块矩阵进行加法操作和乘法操作，并提供了一个支持方法validate()来确认分块矩阵是否创建成功。
    *
    * 分块矩阵可由索引行矩阵IndexedRowMatrix或坐标矩阵CoordinateMatrix调用toBlockMatrix()方法来进行转换，
    * 该方法将矩阵划分成尺寸默认为1024x1024的分块，可以在调用toBlockMatrix(rowsPerBlock, colsPerBlock)方法时传入参数来调整分块的尺寸。
    */
  "BlockMatrix" in {
    val ent1 = MatrixEntry(0, 0, 1)
    val ent2 = MatrixEntry(1, 1, 1)
    val ent3 = MatrixEntry(2, 0, -1)
    val ent4 = MatrixEntry(2, 1, 2)
    val ent5 = MatrixEntry(2, 2, 1)
    val ent6 = MatrixEntry(3, 0, 1)
    val ent7 = MatrixEntry(3, 1, 1)
    val ent8 = MatrixEntry(3, 3, 1)

    val rdd1        = sc.parallelize(Seq(ent1, ent2, ent3, ent4, ent5, ent6, ent7, ent8))
    val matrix      = new CoordinateMatrix(rdd1)
    val blockMatrix = matrix.toBlockMatrix(2, 2)
    println(blockMatrix.validate())
    println(blockMatrix.toLocalMatrix())
  }
}
