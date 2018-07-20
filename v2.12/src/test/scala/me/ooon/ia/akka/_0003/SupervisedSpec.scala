/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.akka._0003

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestKit
import me.ooon.orison.base.test.BaseSpecLike

/**
  * SupervisedSpec
  *
  * @author zhaihao
  * @version 1.0 2018/7/20 15:17
  */
class SupervisedSpec extends TestKit(ActorSystem("test-system")) with BaseSpecLike {
  "test" in {
    val supervisingActor = system.actorOf(Props[SupervisingActor], "supervising-actor")
    supervisingActor ! "failChild"
  }
}

class SupervisingActor extends Actor {

  val child = context.actorOf(Props[SupervisedActor], "supervised-actor")

  override def receive = {
    case "failChild" => child ! "fail"
  }
}

class SupervisedActor extends Actor {

  override def preStart(): Unit = println("supervised actor started")
  override def postStop(): Unit = println("supervised actor stopped")

  override def receive = {
    case "fail" =>
      println("supervised actor fails now")
      throw new Exception("I failed!")
  }
}
