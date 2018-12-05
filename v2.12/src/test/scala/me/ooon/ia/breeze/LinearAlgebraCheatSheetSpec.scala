/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.breeze
import me.ooon.base.test.BaseSpec

/**
  * LinearAlgebraCheatSheetSpec
  * [[https://github.com/scalanlp/breeze/wiki/Breeze-Linear-Algebra]]
  *
  * @author zhaihao
  * @version 1.0 2018/11/22 17:43
  */
class LinearAlgebraCheatSheetSpec extends BaseSpec {
  import breeze.linalg._
  import me.ooon.base.syntax.id._

  "创建" - {
    "零矩阵" in {
      DenseMatrix.zeros[Int](3, 4) |> println
    }

    "零向量" in {
      DenseVector.zeros[Int](3) |> println
    }

    "全为1的向量" in {
      DenseVector.ones[Int](3) |> println
    }

    "全为1的矩阵" in {
      DenseMatrix.ones[Int](3, 4) |> println
    }

    "全为x的向量" in {
      DenseVector.fill(4)(5) |> println
    }

    "全为x的矩阵" in {
      DenseMatrix.fill(3, 4)(5) |> println
    }

    "单位矩阵" in {
      DenseMatrix.eye[Int](4) |> println
    }

    "对角矩阵" in {
      diag(DenseVector(1, 2, 3)) |> println
    }

    "向量构造函数" in {
      DenseVector(1, 2, 3) |> println

      new DenseVector(Array(1, 2, 3)) |> println
    }

    "矩阵构造函数" in {
      DenseMatrix(
        (1, 2, 3),
        (4, 511, 6)
      ) |> println

      new DenseMatrix(3, 4, Array(1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3)) |> println
    }

    "通过函数构造向量" in {
      DenseVector.tabulate(5) {
        i =>
      i * 3
      } |> println
    }

    "通过函数构造矩阵" in {
      DenseMatrix.tabulate(3, 4) {
        (i, j) =>
      i + j
      } |> println
    }

    "随机向量" in {
      DenseVector.rand(4) |> println
    }

    "随机矩阵" in {
      DenseMatrix.rand(3, 4) |> println
    }
  }

  "indexing and slicing" - {
    val A = DenseMatrix(
      (1, 2, 4, 3),
      (2, 3, 7, 4),
      (0, 1, 9, 1)
    )

    val a = DenseVector(2, 3, 7, 4)

    "索引" in {
      A(2, 3) ==> 1
      a(3)    ==> 4
      a(-1)   ==> 4
    }

    "切片" in {
      a(1 to 3)       ==> DenseVector(3, 7, 4)
      a(3 to 0 by -1) ==> DenseVector(4, 7, 3, 2)
      a(1 to -1)      ==> DenseVector(3, 7, 4)
      A(::, 2)        ==> DenseVector(4, 7, 9)
      A(1, ::)        ==> DenseVector(2, 3, 7, 4).t
    }
  }

  "其他常用操作" - {
    val A = DenseMatrix(
      (1, 2, 4, 3),
      (2, 3, 7, 4),
      (0, 1, 9, 1)
    )
    val a = DenseVector(1, 2, 4, 3)

    "reshape" in {
      A.reshape(2, 6) ==> DenseMatrix(
        (1, 0, 3, 4, 9, 4),
        (2, 2, 1, 7, 3, 1)
      )

      a.toDenseMatrix.reshape(2, 2) ==> DenseMatrix(
        (1, 4),
        (2, 3)
      )
    }
  }
}
