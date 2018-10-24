/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.cats.typeclass
import me.ooon.base.test.BaseSpec

import scala.language.implicitConversions

/**
  * AnatomyTypeClassSpec
  *
  * @author zhaihao
  * @version 1.0 2018/10/23 17:27
  */
class AnatomyTypeClassSpec extends BaseSpec {

  "定义一个 typeclass" - {

    // typeclass
    trait JsonWriter[A] {
      def write(value: A): Json
    }

    // instance
    object JsonWriterInstances {
      implicit val stringWriter: JsonWriter[String] = (value: String) => JsString(value)
      implicit val personWriter: JsonWriter[Person] = (person: Person) =>
        JsObject(
          Map(
            "name"  -> JsString(person.name),
            "email" -> JsString(person.email)
          ))

      //noinspection MatchToPartialFunction
      implicit def optionWriter[A](implicit w: JsonWriter[A]): JsonWriter[Option[A]] =
        (oa: Option[A]) =>
          oa match {
            case Some(a) => w.write(a)
            case None    => JsNull
        }
    }

    "Interface Objects" in {
      object Json {
        def toJson[A](a: A)(implicit w: JsonWriter[A]): Json = w.write(a)
      }

      // import implicit typeclass instance
      import JsonWriterInstances._
      val json = Json.toJson(Person("Tom", "tom@gmail.com"))
      json ==> JsObject(Map("name" -> JsString("Tom"), "email" -> JsString("tom@gmail.com")))
    }

    "Interface Syntax" in {
      object JsonSyntax {
        implicit class JsonWriterOps[A](a: A) {
          def toJson(implicit w: JsonWriter[A]): Json = w.write(a)
        }
      }

      import JsonSyntax._
      import JsonWriterInstances._

      Person("Tom", "tom@gmail.com").toJson ==> JsObject(
        Map("name" -> JsString("Tom"), "email" -> JsString("tom@gmail.com")))
    }

    "Recursive Implicit Resolution" in {
      object Json {
        def toJson[A](a: A)(implicit w: JsonWriter[A]): Json = w.write(a)
      }

      import JsonWriterInstances._
      Json.toJson(Option("A String")) ==> JsString("A String")
    }
  }
}

// Json AST
private[typeclass] sealed trait Json
private[typeclass] final case class JsObject(get: Map[String, Json]) extends Json
private[typeclass] final case class JsString(get: String)            extends Json
private[typeclass] final case class JsNumber(get: Double)            extends Json
private[typeclass] case object JsNull extends Json

private[typeclass] case class Person(name: String, email: String)
