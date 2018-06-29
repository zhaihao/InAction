/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.slick

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile

  import profile.api._
  import java.sql.Date
  import java.sql.Time
  import java.sql.Timestamp
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = TUser.schema

  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table TUser
    *
    * @param id         Database column id SqlType(BIGINT), AutoInc, PrimaryKey
    * @param name       Database column name SqlType(VARCHAR), Length(200,true)
    * @param mail       Database column mail SqlType(VARCHAR), Length(200,true)
    * @param age        Database column age SqlType(INT)
    * @param address    Database column address SqlType(VARCHAR), Length(200,true)
    * @param createTime Database column create_time SqlType(TIMESTAMP)
    * @param updateTime Database column update_time SqlType(TIMESTAMP) */
  case class User(id:         Option[Long] = None,
                  name:       String,
                  mail:       String,
                  age:        Option[Int],
                  address:    Option[String],
                  createTime: Option[Timestamp],
                  updateTime: Option[Timestamp])

  /** GetResult implicit for fetching User objects using plain SQL queries */
  implicit def GetResultUser(implicit e0: GR[Option[Long]],
                             e1:          GR[String],
                             e2:          GR[Option[Int]],
                             e3:          GR[Option[String]],
                             e4:          GR[Option[Timestamp]]): GR[User] = GR { prs =>
    import prs._
    User.tupled(
      (<<?[Long],
       <<[String],
       <<[String],
       <<?[Int],
       <<?[String],
       <<[Option[Timestamp]],
       <<[Option[Timestamp]]))
  }

  /** Table description of table T_User. Objects of this class serve as prototypes for rows in queries. */
  class TUser(_tableTag: Tag)
      extends profile.api.Table[User](_tableTag, "T_User") {
    def * =
      (Rep.Some(id), name, mail, age, address, createTime, updateTime) <> (User.tupled, User.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (Rep.Some(id),
       Rep.Some(name),
       Rep.Some(mail),
       age,
       address,
       Rep.Some(createTime),
       Rep.Some(updateTime)).shaped.<>(
        { r =>
          import r._;
          _1.map(_ => User.tupled((_1, _2.get, _3.get, _4, _5, _6.get, _7.get)))
        },
        (_: Any) =>
          throw new Exception("Inserting into ? projection not supported.")
      )

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

    /** Database column name SqlType(VARCHAR), Length(200,true) */
    val name: Rep[String] =
      column[String]("name", O.Length(200, varying = true))

    /** Database column mail SqlType(VARCHAR), Length(200,true) */
    val mail: Rep[String] =
      column[String]("mail", O.Length(200, varying = true))

    /** Database column age SqlType(INT) */
    val age: Rep[Option[Int]] = column[Option[Int]]("age")

    /** Database column address SqlType(VARCHAR), Length(200,true) */
    val address: Rep[Option[String]] =
      column[Option[String]]("address", O.Length(200, varying = true))

    /** Database column create_time SqlType(TIMESTAMP) */
    val createTime: Rep[Option[Timestamp]] =
      column[Option[Timestamp]]("create_time")

    /** Database column update_time SqlType(TIMESTAMP) */
    val updateTime: Rep[Option[Timestamp]] =
      column[Option[Timestamp]]("update_time")

    /** Uniqueness Index over (mail) (database name mail) */
    val index1 = index("mail", mail, unique = true)
  }

  /** Collection-like TableQuery object for table TUser */
  lazy val TUser = new TableQuery(tag => new TUser(tag))
}
