/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.vegas

import me.ooon.base.test.BaseSpec

/**
  * GallerySpec
  *
  * @author zhaihao
  * @version 1.0 2018-12-01 12:56
  */
class GallerySpec extends BaseSpec with PlotLike {
  import vegas._

  val Cars   = buildInServerHost + "scalaia/data/vegas/cars.json"
  val Movies = buildInServerHost + "scalaia/data/vegas/movies.json"
  val Stocks = buildInServerHost + "scalaia/data/vegas/stocks.csv"

  "a simple area chart" in {

    plot = Vegas("Sample Area Chart", width = 800.0, height = 600.0)
      .withURL(Cars)
      .mark(Area)
      .encodeX("Acceleration", Quantitative, bin = Bin())
      .encodeY("Horsepower", Quantitative, AggOps.Mean, enableBin = false)
      .encodeColor(field = "Cylinders", dataType = Nominal)
  }

  "a binned scatter plot" in {
    plot = Vegas("Sample Binned Scatter plot", width = 800.0, height = 600.0)
      .withURL(Movies)
      .mark(Point)
      .encodeX("IMDB_Rating", Quantitative, bin = Bin(maxbins = 10.0))
      .encodeY("Rotten_Tomatoes_Rating", Quantitative, bin = Bin(maxbins = 10.0))
      .encodeSize(aggregate = AggOps.Count, field = "*", dataType = Quantitative)
  }

  "scatter plot with binned color coding" in {
    plot = Vegas("Sample Scatter plot", width = 800.0, height = 600.0)
      .withURL(Cars)
      .mark(Point)
      .encodeX("Horsepower", Quantitative)
      .encodeY("Miles_per_Gallon", Quantitative)
      .encodeColor("Acceleration", Quantitative, bin = Bin(maxbins = 5.0))
  }

  "a multi-series line chart" in {
    plot = Vegas("Sample Multi Series Line Chart", width = 800.0, height = 600.0)
      .withURL(Stocks, DataFormat.Csv)
      .mark(Line)
      .encodeX("date", Temporal)
      .encodeY("price", Quantitative)
      .encodeColor("symbol", Nominal, legend = Legend(orient = "left", title = "Stock Symbol"))
      .encodeDetailFields(Field(field = "symbol", dataType = Nominal))
  }

}
