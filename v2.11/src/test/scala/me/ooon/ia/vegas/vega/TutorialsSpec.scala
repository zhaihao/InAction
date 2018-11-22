/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.vegas.vega
import me.ooon.base.test.BaseSpec
import me.ooon.ia.vegas.PlotLike

/**
  * TutorialsSpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-01 15:00
  */
class TutorialsSpec extends BaseSpec with PlotLike {
  import vegas._

  "Getting Started" - {

    val data = Seq(
      P("C", 2),
      P("C", 7),
      P("C", 4),
      P("D", 1),
      P("D", 2),
      P("D", 6),
      P("E", 8),
      P("E", 4),
      P("E", 7)
    )

    "Encoding Data with Marks" - {

      "每个数据显示的形状" in {
        plot = Vegas(width = 800.0, height = 600.0)
          .withCaseClasses(data)
          .mark(Point)
      }

      "x 轴" in {
        plot = Vegas(width = 800.0, height = 600.0)
          .withCaseClasses(data)
          .mark(Point)
          .encodeX(field = "a", dataType = Nom)
      }

      "y 轴" in {
        plot = Vegas(width = 800.0, height = 600.0)
          .withCaseClasses(data)
          .mark(Point)
          .encodeX(field = "a", dataType = Nom)
          .encodeY(field = "b", dataType = Quant)
      }
    }

    "Data Transformation: Aggregation" - {
      "竖着的柱状图" in {
        plot = Vegas(width = 800.0, height = 600.0)
          .withCaseClasses(data)
          .mark(Bar)
          .encodeX(field = "a", dataType = Nom)
          .encodeY(field = "b", dataType = Quant, aggregate = AggOps.Average) // 平均值
      }

      "横着的柱状图" in {
        plot = Vegas(width = 800.0, height = 600.0)
          .withCaseClasses(data)
          .mark(Bar)
          .encodeY(field = "a", dataType = Nom)
          .encodeX(field = "b", dataType = Quant, aggregate = AggOps.Average, title = "MEAN OF B") // 平均值
      }
    }
  }

  //explore weather data for Seattle
  "Exploring Data" - {
    val url = buildInServerHost + "scalaia/data/vegas/seattle-weather.csv"

    "降雨量 tick 图" in {
      plot = Vegas()
        .withURL(url, DataFormat.Csv)
        .mark(Tick)
        .encodeX("precipitation", Quant)
    }

    "降雨量直方图" in {
      plot = Vegas()
        .withURL(url, DataFormat.Csv)
        .mark(Bar)
        .encodeX("precipitation", Quant, bin = Bin(step = 10.0))
        .encodeY("precipitation", dataType = Quant, aggregate = AggOps.Count)
    }

    "降雨量与月份的关系" in {
      plot = Vegas()
        .withURL(url, DataFormat.Csv)
        .mark(Line)
        .encodeX("date", Temporal, timeUnit = TimeUnit.Month)
        .encodeY("precipitation", Quantitative, aggregate = AggOps.Mean)
    }

    "日最高温度最大值与年月的关系" in {
      plot = Vegas(width = 500.0, height = 500.0)
        .withURL(url, DataFormat.Csv)
        .mark(Line)
        .encodeX("date", Temporal, timeUnit = TimeUnit.Yearmonth)
        .encodeY("temp_max", Quantitative, aggregate = AggOps.Max)
    }

    "日最高温度均值与年的关系" in {
      plot = Vegas(width = 500.0, height = 500.0)
        .withURL(url, DataFormat.Csv)
        .mark(Line)
        .encodeX("date", Temporal, timeUnit = TimeUnit.Year)
        .encodeY("temp_max", Quantitative, aggregate = AggOps.Mean)
    }

    // VEGA 4 不兼容
    "温差" in {
      plot = Vegas(width = 500.0, height = 500.0)
        .withURL(url, DataFormat.Csv)
        .addTransformCalculation("temp_range", "datum.temp_max - datum.temp_min")
        .mark(Line)
        .encodeX("date", Temporal, timeUnit = TimeUnit.Month)
        .encodeY("temp_range", Quantitative, aggregate = AggOps.Mean)
    }

    "各种天气比例" in {
      plot = Vegas(width = 500.0, height = 500.0)
        .withURL(url, DataFormat.Csv)
        .mark(Bar)
        .encodeX("date", Temporal, timeUnit = TimeUnit.Month)
        .encodeY("*", Quantitative, aggregate = AggOps.Count)
        .encodeColor("weather", Nominal)
    }

    "各种天气比例2" in {
      plot = Vegas(width = 500.0, height = 500.0)
        .withURL(url, DataFormat.Csv)
        .mark(Bar)
        .encodeX("date", Temporal, timeUnit = TimeUnit.Month)
        .encodeY("*", Quantitative, aggregate = AggOps.Count)
        .encodeColor(
          "weather",
          Nominal,
          scale = Scale(
            domainNominals = List("sun", "fog", "drizzle", "rain", "snow"),
            rangeNominals = List("#e7ba52", "#c7c7c7", "#aec7e8", "#1f77b4", "#9467bd")
          ),
          legend = Legend(title = "Weather type")
        )
    }
  }
}
case class P(a: String, b: Int)
