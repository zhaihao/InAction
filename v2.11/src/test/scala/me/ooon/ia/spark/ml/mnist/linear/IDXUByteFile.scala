/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml.mnist.linear
import java.awt.color.ColorSpace
import java.awt.image.{BufferedImage, ColorConvertOp, DataBufferByte}
import java.awt.{AlphaComposite, RenderingHints}
import java.io.{DataInputStream, File, FileInputStream}
import java.util.zip.GZIPInputStream

import breeze.linalg.DenseMatrix
import javax.imageio.ImageIO
import me.ooon.ia.spark.ml.MLDataFile

import scala.collection.mutable.ArrayBuffer

/**
  * IDXUByteFile
  *
  * IDX 是一种存储矩阵的格式, 文件格式参考以下链接
  *
  * @see http://yann.lecun.com/exdb/mnist/index.html
  * @author zhaihao
  * @version 1.0 2018-12-06 15:28
  */
class IDXUByteFile(path: String) extends MLDataFile(path) {
  protected lazy val stream = new DataInputStream(new GZIPInputStream(new FileInputStream(this)))
}

// label data
class IDX1UByteFile(path: String) extends IDXUByteFile(path) {
  assert(stream.readInt() == 2049) // magic number
  val size = stream.readInt() // 记录数

  def labels: Stream[Int] = {
    def loop(a: Int): Stream[Int] =
      if (a == 0) Stream.empty else Stream.cons(stream.readUnsignedByte(), loop(a - 1))

    loop(size)
  }
}

// feature data
class IDX3UByteFile(path: String) extends IDXUByteFile(path) {

  assert(stream.readInt() == 2051) // magic number

  val size = stream.readInt() // 样本数
    val rows = stream.readInt() // 单个样本矩阵的 行数
    val cols = stream.readInt() // 单个样本矩阵的 列数

  val featureSize = rows * cols

  lazy val show: Stream[String] = features.map(_.t.toString(500, 500))

  lazy val features: Stream[DenseMatrix[Byte]] = {
      def loop(a: Int): Stream[DenseMatrix[Byte]] = {
        if (a == 0) Stream.empty else Stream.cons(readOneMatrix, loop(a - 1))
      }

      def readOneMatrix: DenseMatrix[Byte] = {
        var a    = featureSize
        val data = ArrayBuffer.empty[Byte]
        while (a > 0) {
          data += stream.readUnsignedByte().toByte
          a -= 1
        }
        new DenseMatrix[Byte](rows, cols, data.toArray)
      }

      loop(size)
    }

  // stream 只能使用一次
    lazy val images: Stream[Image] =
      features.map(fs => Image(cols, rows, BufferedImage.TYPE_BYTE_GRAY, fs.data))

}

case class Image(width: Int, height: Int, imageType: Int, bytes: Array[Byte]) {
    val len = width * height

    def save(path: String, fileType: String) = {
      val bufferedImage = new BufferedImage(width, height, imageType)

      var k = 0
      while (k < len) {
        bufferedImage.setRGB(k % 28, k / 28, bytes(k))
        k += 1
      }
      val file = new File(path + fileType)
      if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
      ImageIO.write(bufferedImage, fileType, file)
    }

    // show need transpose
    def toMatrix: DenseMatrix[Byte] = new DenseMatrix[Byte](height, width, bytes)

  }

object Image {

  def apply(file: File): Image = {
      val src    = ImageIO.read(file)
      val `type` = src.getType
      val width  = src.getWidth
      val height = src.getHeight

      Image(width, height, `type`, src.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData)

    }

  def toGray(src: BufferedImage) = {
      val `type`   = src.getType
      val width    = src.getWidth
      val height   = src.getHeight
      val grayDest = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
      new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(src, grayDest)
      Image(width,
            height,
            `type`,
            grayDest.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData)
    }

  def resize(src: BufferedImage, targetW: Int, targetH: Int): BufferedImage = {
      val dest       = new BufferedImage(targetW, targetH, BufferedImage.TYPE_BYTE_GRAY)
      val graphics2D = dest.createGraphics()
      graphics2D.setComposite(AlphaComposite.Src)
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                  RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                  RenderingHints.VALUE_ANTIALIAS_ON)

      graphics2D.drawImage(src, 0, 0, targetW, targetH, null)
      graphics2D.dispose()
      dest
    }

}
