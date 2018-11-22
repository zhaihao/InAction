/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.breeze
import me.ooon.base.syntax.id._
import me.ooon.base.test.BaseSpec

/**
  * QuickStartSpec
  *
  * @author zhaihao
  * @version 1.0 2018/11/12 14:06
  */
class QuickStartSpec extends BaseSpec {
  import breeze.linalg._

  "向量" - {
    "创建向量" in {
      val x = DenseVector.zeros[Double](5)
      println(x)

      // 稀疏的向量 0 不会分配内存
      val y = SparseVector.zeros[Double](5)
      println(y)

      val value: DenseVector[Int] = DenseVector(1, 2, 3, 4)
      println(value)
    }

    "breeze 的向量都是列向量" in {
      val value: DenseVector[Int] = DenseVector(1, 2, 3, 4)
      // 行向量用转置表示
      val result: Transpose[DenseVector[Int]] = value.t
      result ==> Transpose(DenseVector(1, 2, 3, 4))
    }

    "访问向量的元素" in {
      val d = DenseVector(1, 2, 3, 4)
      // apply 偶尔会导致 scalac 崩溃 @see https://github.com/scalanlp/breeze/issues/65
      d(2) ==> 3
      d.valueAt(2) ==> 3
    }

    "更新向量的元素" in {
      val d = DenseVector(1, 2, 3, 4)
      d(1) = 0
      d ==> DenseVector(1, 0, 3, 4)
    }

    "向量分片" in {
      val d = DenseVector(1, 2, 3, 4, 5)
      d(3 to 4) ==> DenseVector(4, 5)
      d(3 to 4) := 2
      d         ==> DenseVector(1, 2, 3, 2, 2)
      d(1 to 2) := DenseVector(0, 0)
      d         ==> DenseVector(1, 0, 0, 2, 2)
    }
  }

  "矩阵" - {

    "创建矩阵" in {
      val a = DenseMatrix.zeros[Double](3, 4)
      a |> println
      (a.rows, a.cols) ==> (3, 4)
    }

    "访问矩阵" - {
      val A: DenseMatrix[Double] = DenseMatrix(
        (1.0, 2.0, 3.0),
        (2.0, 3.0, 4.0)
      )

      "访问列" in {
        A(::, 0) ==> DenseVector(1.0, 2.0)
      }

      "访问行" in {
        A(1, ::) ==> DenseVector(2.0, 3.0, 4.0).t
      }

      "访问元素" in {
        A(1, 2) ==> 4.0
      }

      "访问一块" in {
        val A: DenseMatrix[Double] = DenseMatrix(
          (1.0, 2.0, 3.0),
          (2.0, 3.0, 4.0),
          (2.0, 3.0, 4.0),
          (0.0, 0.0, 0.0)
        )

        A(0 to 2, 0 to 1) ==> DenseMatrix(
          (1.0, 2.0),
          (2.0, 3.0),
          (2.0, 3.0)
        )
      }
    }

    "更新值" - {
      "更新一个元素" in {
        val A: DenseMatrix[Double] = DenseMatrix(
          (1.0, 2.0, 3.0),
          (2.0, 3.0, 4.0)
        )

        A.update(0, 0, 3.0)
        A(::, 0) ==> DenseVector(3.0, 2.0)
      }
      "更新一行" in {
        val A: DenseMatrix[Double] = DenseMatrix(
          (1.0, 2.0, 3.0),
          (2.0, 3.0, 4.0)
        )

        A(1, ::) := DenseVector(0.0, 0.0, 0.0).t
        A(1, ::) ==> DenseVector(0.0, 0.0, 0.0).t
      }

      "更新一列" in {
        val A: DenseMatrix[Double] = DenseMatrix(
          (1.0, 2.0, 3.0),
          (2.0, 3.0, 4.0)
        )

        A(::, 1) := DenseVector(0.0, 0.0)
        A(::, 1) ==> DenseVector(0.0, 0.0)
      }
      "数据类型不匹配无法编译" in {
        val A: DenseMatrix[Double] = DenseMatrix(
          (1.0, 2.0, 3.0),
          (2.0, 3.0, 4.0)
        )
        assertDoesNotCompile("A(::, 1) := DenseVector(0, 0)")
      }

      "矩阵整体赋值形状不匹配会抛出异常" in {
        val A: DenseMatrix[Double] = DenseMatrix(
          (1.0, 2.0, 3.0),
          (2.0, 3.0, 4.0)
        )

        assertThrows[IllegalArgumentException] {
          A := DenseMatrix.zeros[Double](3, 3)
        }
      }
    }
  }

  "张量计算(矩阵，向量)" - {

    "加法" in {
      val a = DenseMatrix(
        (1, 2, 3),
        (2, 3, 4)
      )

      val b = DenseMatrix(
        (1, 1, 1),
        (0, 1, 0)
      )

      a + b ==> DenseMatrix(
        (2, 3, 4),
        (2, 4, 4)
      )
    }

    "乘法" in {
      val a = DenseMatrix(
        (1, 2, 3),
        (2, 3, 4)
      )

      val b = DenseMatrix(
        (1, 0),
        (0, 0),
        (0, 1)
      )

      a * b ==> DenseMatrix(
        (1, 3),
        (2, 4)
      )
    }

    "同位相加" in {
      val a = DenseMatrix(
        (1, 2, 3),
        (2, 3, 4)
      )

      val b = DenseMatrix(
        (1, 1, 1),
        (0, 1, 0)
      )

      a +:+ b ==> DenseMatrix(
        (2, 3, 4),
        (2, 4, 4)
      )

      // 也就是加法
      a +:+ b ==> a + b
    }

    "同位相乘" in {
      val a = DenseMatrix(
        (1, 2, 3),
        (2, 3, 4)
      )

      a *:* a ==> DenseMatrix(
        (1, 4, 9),
        (4, 9, 16)
      )
    }

    "同位相除" in {
      val a = DenseMatrix(
        (1, 2, 3),
        (2, 3, 4)
      )

      a /:/ a ==> DenseMatrix(
        (1, 1, 1),
        (1, 1, 1)
      )
    }

    "同位比较" in {
      val a = DenseMatrix(
        (1, 2),
        (2, 3)
      )

      val b = DenseMatrix(
        (1, 3),
        (4, 4)
      )

      a <:< b ==> DenseMatrix(
        (false, true),
        (true, true)
      )

      (a :== b) ==> DenseMatrix(
        (true, false),
        (false, false)
      )
    }

    "与常数相加" in {
      val a = DenseMatrix(
        (1, 2),
        (2, 3)
      )

      (a :+= 1) ==> DenseMatrix(
        (2, 3),
        (3, 4)
      )

    }

    "与常数相乘" in {
      val a = DenseMatrix(
        (1, 2),
        (2, 3)
      )

      (a :*= 2) ==> DenseMatrix(
        (2, 4),
        (4, 6)
      )
    }

    "最大/小值" in {
      val a = DenseMatrix(
        (1, 2),
        (2, 3)
      )

      max(a) ==> 3
      //最大值位置
      argmax(a) ==> (1, 1)
      min(a)    ==> 1
      //最小值位置
      argmin(a) ==> (0, 0)
    }

    "元素之和" in {
      val a = DenseMatrix(
        (1, 2),
        (2, 3)
      )

      sum(a) ==> 8
    }

    "行列式的值" in {
      val a = DenseMatrix(
        (1, 2),
        (2, 3)
      )
      det(a) ==> -1.0
    }

    "点积" in {
      DenseVector(1, 2, 3).dot(DenseVector(1, 0, 1)) ==> 4
    }

  }
}
