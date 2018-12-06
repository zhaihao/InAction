/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.spark.ml

/**
  * package
  *
  *  手写数字识别
  *
  * @author zhaihao
  * @version 1.0 2018-12-06 14:58
  */
package object mnist {}

/**
  * 数据地址：http://yann.lecun.com/exdb/mnist/index.html
  *
  * train-* 是训练集 60,000 个样本，
  *
  * t10k-* 是测试集 1000 个样本
  *
  * *-images-*是feature
  * *-labels-*是label
  *
  * The digits have been size-normalized and centered in a fixed-size image.
  * 原始的数字图片，已经进行过缩放操作，和 把数字移动到正中间的操作。
  * 如果要预测自己的图片也要经过这两个操作
  *
  *
  */
