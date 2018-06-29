/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick.gen

import slick.codegen.SourceCodeGenerator
import slick.model.Model

/**
  * CustomSourceCodeGenerator
  *
  * @author zhaihao
  * @version 1.0 2018/3/24 16:39
  */
class CustomSourceCodeGenerator(model: Model)
    extends SourceCodeGenerator(model) {
  // 添加自定义的 code
  override def code =
    "import java.sql.Date\n" +
      "import java.sql.Time\n" +
      "import java.sql.Timestamp\n" +
      super.code

  //  override def code = "import com.github.tototoshi.slick.MySQLJodaSupport._\n" + "import org.joda.time._\n" + super.code
  override def Table = new Table(_) {

    override def Column = new Column(_) {
      // 主键 设为 option
      override def asOption = autoInc
    }
  }

  override def entityName = dbTableName => dbTableName.drop(2).toCamelCase

  override def tableName = dbTableName => dbTableName.toCamelCase
}
