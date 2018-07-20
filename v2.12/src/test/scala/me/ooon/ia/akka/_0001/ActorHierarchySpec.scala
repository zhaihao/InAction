/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.akka._0001

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestKit
import me.ooon.orison.base.test.BaseSpecLike

/**
  * ActorHierarchySpec
  *
  * actor path 路径
  *
  * @author zhaihao
  * @version 1.0 2018/7/20 11:37
  */
class ActorHierarchySpec extends TestKit(ActorSystem("test-system")) with BaseSpecLike {

  "test" in {
    val firstRef = system.actorOf(Props[PrintMyActorRefActor], "first-actor")
    println(s"First: $firstRef")

    firstRef ! "print"
  }
}

class PrintMyActorRefActor extends Actor {
  override def receive = {
    case "print" =>
      val secondRef = context.actorOf(Props.empty, "second-actor")
      println(s"Second: $secondRef")
  }
}
