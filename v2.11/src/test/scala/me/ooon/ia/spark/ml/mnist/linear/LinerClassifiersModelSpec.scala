/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.mnist.linear

import java.io.File

import com.typesafe.scalalogging.StrictLogging
import javax.imageio.ImageIO
import me.ooon.base.test.BaseSpec

import scala.language.postfixOps
import scala.sys.process._

/**
  * LinerClassifiersModelSpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-06 15:44
  */
class LinerClassifiersModelSpec extends BaseSpec with StrictLogging {

  val train_idx1 = new IDX1UByteFile("data/mnist/train-labels-idx1-ubyte.gz")
  val train_idx3 = new IDX3UByteFile("data/mnist/train-images-idx3-ubyte.gz")
  val test_idx1  = new IDX1UByteFile("data/mnist/t10k-labels-idx1-ubyte.gz")
  val test_idx3  = new IDX3UByteFile("data/mnist/t10k-images-idx3-ubyte.gz")

  "train" - {

    "读取数据" in {
      logger.info("样本数：" + train_idx1.size)
      logger.info("前30个样本" + train_idx1.labels.take(10).mkString(","))

      logger.info("样本数：" + train_idx3.size)
      logger.info("单个图片矩阵行数：" + train_idx3.rows)
      logger.info("单个图片矩阵列数：" + train_idx3.cols)

      logger.info({ val n = 10; s"前 $n 个样本:\n" + train_idx3.show.take(n).mkString("\n\n") })
      logger.info({ val n = 1; s"第 $n 个样本:\n" + train_idx3.show(n - 1) })
    }

    "idx3 to image" in {
      val labels = train_idx1.labels
      val images = train_idx3.images

      val saveFolderPath = sys.props
        .getOrElse("user.home", "log") + File.separator + "Downloads" + File.separator + "train_images"
      val saveFolder = new File(saveFolderPath)

      s"rm -rf ${saveFolder.getPath}" !

      saveFolder.mkdirs()

      (labels zip images).zipWithIndex.foreach(tuple => {
        val ((label, image), index) = tuple
        image.save(saveFolderPath + File.separator + index + "_" + label + ".", "png")
      })
    }

    "fit" in {}
  }

  "test" - {
    "idx3 to image" in {
      val labels = test_idx1.labels
      val images = test_idx3.images

      val saveFolderPath = sys.props
        .getOrElse("user.home", "log") + File.separator + "Downloads" + File.separator + "test_images"
      val saveFolder = new File(saveFolderPath)

      s"rm -rf ${saveFolder.getPath}" !

      saveFolder.mkdirs()

      (labels zip images).zipWithIndex.foreach(tuple => {
        val ((label, image), index) = tuple
        image.save(saveFolderPath + File.separator + index + "_" + label + ".", "jpg")
      })
    }
  }

  "predict" - {}
}
