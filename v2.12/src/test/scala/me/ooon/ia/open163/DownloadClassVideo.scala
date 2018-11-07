/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.open163
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import scala.language.postfixOps
import me.ooon.base.sys.ShellCMD._

/**
  * DownloadClassVideo
  *
  * @author zhaihao
  * @version 1.0 2018/11/7 16:40
  */
object DownloadClassVideo {

  def main(args: Array[String]): Unit = {
    val url     = "http://open.163.com/special/opencourse/daishu.html"
    val browser = JsoupBrowser()
    val doc     = browser.get(url)

    val title = doc >> text("title")

    s"cd ~ && mkdir -p Movies/$title" !

    val tuple2 = ((doc >> elementList("#list2")).head >> elementList("tr"))
      .drop(1)
      .map(tr => {
        val name = tr >> text("tr")
        val url  = tr >> "a" >> attr("href")
        (name, url)
      })

    System.setProperty("scala.concurrent.context.minThreads", "4")
    System.setProperty("scala.concurrent.context.maxThreads", "4")

    tuple2.par.foreach(t => {
      val cmd = s"""cd ~/Movies/$title && you-get -O '${t._1}' '${t._2}'"""
      println(cmd)
      cmd !
    })
  }
}
