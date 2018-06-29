/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick.gen

import slick.ast.AnonSymbol
import slick.jdbc.meta.{MColumn, MTable}
import slick.jdbc.{
  JdbcModelBuilder,
  JdbcModelComponent,
  JdbcProfile,
  MySQLProfile
}

import scala.concurrent.ExecutionContext

/**
  * CustomMySQLProfile
  *
  * @author zhaihao
  * @version 1.0 2018/3/24 16:43
  */
object CustomMySQLProfile extends MySQLProfile with CustomJdbcModelComponent {

  def RowNum(sym: AnonSymbol, inc: Boolean) = MySQLProfile.RowNum(sym, inc)

  def RowNumGen(sym: AnonSymbol, init: Long) = MySQLProfile.RowNumGen(sym, init)
}

trait CustomJdbcModelComponent extends JdbcModelComponent {
  self: JdbcProfile =>
  override def createModelBuilder(
      tables:                Seq[MTable],
      ignoreInvalidDefaults: Boolean)(implicit ec: ExecutionContext) =
    new CustomJdbcModelBuilder(tables, ignoreInvalidDefaults)
}

class CustomJdbcModelBuilder(
    mTables:               Seq[MTable],
    ignoreInvalidDefaults: Boolean)(implicit ec: ExecutionContext)
    extends JdbcModelBuilder(mTables, ignoreInvalidDefaults)
    with Settings {

  private[this] val timestampType = config.getString("gen.customType.timestamp")

  override def createColumnBuilder(tableBuilder: TableBuilder,
                                   meta:         MColumn) = {
    new CustomColumnBuilder(tableBuilder, meta)
  }

  class CustomColumnBuilder(tableBuilder: TableBuilder, meta: MColumn)
      extends ColumnBuilder(tableBuilder, meta) {

    override def tpe = {
      jdbcTypeToScala(meta.sqlType, meta.typeName).toString match {
        case "java.lang.String" =>
          if (meta.size.contains(1)) "Char" else "String"
        case "java.sql.Timestamp" =>
          rawDefault match {
            case Some(_) if meta.typeName.toUpperCase == "TIMESTAMP" =>
              s"Option[$timestampType]"
            case _ => timestampType
          }
        case "java.sql.Date" => config.getString("gen.customType.date")
        case "java.sql.Time" => config.getString("gen.customType.time")
        case jdbcType        => jdbcType
      }
    }
  }

}
