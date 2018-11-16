/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.design.pattern
import me.ooon.base.test.BaseSpec

/**
  * AdapterSpec
  *
  * @author zhaihao
  * @version 1.0 2018/11/16 15:42
  */
class AdapterSpec extends BaseSpec {

  class A(val name: String, val age: Int)

  trait StringProvider {
    def getStringData: String
  }
  def handleStringProvider(provider: StringProvider) = println(provider.getStringData)

  "oo" in {
    class ClassAFormatAdapter extends StringProvider {
      var classA: A = _

      override def getStringData = s"i am ${classA.name}, ${classA.age} years old"
    }
    object ClassAFormatAdapter {
      def apply(a: A) = {
        val adapter = new ClassAFormatAdapter()
        adapter.classA = a
        adapter
      }
    }

    // usage
    val a = new A("tom", 10)
    handleStringProvider(ClassAFormatAdapter(a))
  }

  "fp" in {
    implicit class ClassAFormatAdapter(a: A) extends StringProvider {
      override def getStringData = s"i am ${a.name}, ${a.age} years old"
    }

    val a = new A("tom", 10)
    handleStringProvider(a)
  }
}
