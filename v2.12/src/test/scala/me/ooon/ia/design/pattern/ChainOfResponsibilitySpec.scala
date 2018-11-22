/*
 * Copyright (c) 2018.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package me.ooon.ia.design.pattern
import me.ooon.base.test.BaseSpec

/**
  * ChainOfResponsibilitySpec
  *
  * @author zhaihao
  * @version 1.0 2018/11/16 16:01
  */
class ChainOfResponsibilitySpec extends BaseSpec {

  "oo" in {
    case class Event(level: Int, title: String)
    abstract class Handler {
      val successor: Option[Handler]
      def handleEvent(event: Event): Unit
    }

    class Agent(val successor: Option[Handler]) extends Handler {
      override def handleEvent(event: Event) = {
        event match {
          case e if e.level < 2 => println("CS Agent Handled event: " + e.title)
          case e if e.level > 1 =>
            successor match {
              case Some(h: Handler) => h.handleEvent(e)
              case None => println("Agent: This event cannot be handled.")
            }
        }
      }
    }

    class Supervisor(val successor: Option[Handler]) extends Handler {
      override def handleEvent(event: Event): Unit = {
        event match {
          case e if e.level < 3 => println("Supervisor handled event: " + e.title)
          case e if e.level > 2 =>
            successor match {
              case Some(h: Handler) => h.handleEvent(e)
              case None => println("Supervisor: This event cannot be handled")
            }
        }
      }
    }

    class Boss(val successor: Option[Handler]) extends Handler {
      override def handleEvent(event: Event): Unit = {
        event match {
          case e if e.level < 4 => println("Boss handled event: " + e.title)
          case e if e.level > 3 =>
            successor match {
              case Some(h: Handler) => h.handleEvent(e)
              case None => println("Boss: This event cannot be handled")
            }
        }
      }
    }

    val boss       = new Boss(None)
    val supervisor = new Supervisor(Some(boss))
    val agent      = new Agent(Some(supervisor))

    val events = Array(
      Event(1, "Technical support"),
      Event(2, "Billing query"),
      Event(1, "Product information query"),
      Event(3, "Bug report"),
      Event(5, "Police subpoena"),
      Event(2, "Enterprise client request")
    )

    events foreach {
      e: Event => agent.handleEvent(e)
    }
  }

  "fp" - {

    "Partial Functions" in {
      case class DemoState(number: Int)
      type PFRule = PartialFunction[DemoState, Option[String]]

      def numberRule(f: Int => Boolean, result: String): PFRule = {
        case DemoState(n) if f(n) => Option(result)
      }

      val GreaterThanFiveRule: PFRule = numberRule(_ > 5, "Greater than five")
      val LessThanFiveRule:    PFRule = numberRule(_ < 5, "Less than five")
      val EqualsFiveRule:      PFRule = numberRule(_ == 5, "Equals five")

      val NumberRules
        : PartialFunction[DemoState, Option[String]] = GreaterThanFiveRule orElse LessThanFiveRule orElse EqualsFiveRule

      NumberRules(DemoState(5)) ==> Some("Equals five")
      NumberRules(DemoState(1)) ==> Some("Less than five")
      NumberRules(DemoState(7)) ==> Some("Greater than five")
    }

    "scalaz disjunction" in {
      import scalaz._
      import Scalaz._

      import scala.annotation.tailrec

      case class DemoState(number: Int)

      trait Rule[A, B] {
        def handle(request: B): A \/ B
      }
      type FiveRule = Rule[String, DemoState]

      case object GreaterThanFiveRule extends FiveRule {
        override def handle(request: DemoState): String \/ DemoState =
          if (request.number > 5) "Greater than five".left
          else request.right
      }

      case object LessThanFiveRule extends FiveRule {
        override def handle(state: DemoState): String \/ DemoState =
          if (state.number < 5) "Less than five".left
          else state.right
      }

      case object EqualsFiveRule extends FiveRule {
        override def handle(state: DemoState): String \/ DemoState =
          if (state.number == 5) "Equals five".left
          else state.right
      }

      implicit class RuleListHandler[A, B](list: List[Rule[A, B]]) {
        def applyOnlyOneTailRec(request: B): A \/ B = {
          @tailrec
          def loop(req: B, rules: List[Rule[A, B]]): A \/ B = {
            if (rules.isEmpty) req.right
            else
              rules.head.handle(req) match {
                case -\/(value) => value.left
                case \/-(_)     => loop(req, rules.tail)
              }
          }
          loop(request, list)
        }
      }

      val NumberRules = List(GreaterThanFiveRule, LessThanFiveRule, EqualsFiveRule)

      NumberRules.applyOnlyOneTailRec(DemoState(5)) ==> -\/("Equals five")
    }

    "applicative" in {
      import scalaz._
      import Scalaz._

      import scala.annotation.tailrec
      import scala.language.postfixOps

      case class DemoState(number: Int)

      trait Rule[A, B] {
        def handle(request: B): A \/ B
      }
      type FiveRule = Rule[String, DemoState]

      case object GreaterThanFiveRule extends FiveRule {
        override def handle(request: DemoState): String \/ DemoState =
          if (request.number > 5) "Greater than five".left
          else request.right
      }

      case object LessThanFiveRule extends FiveRule {
        override def handle(state: DemoState): String \/ DemoState =
          if (state.number < 5) "Less than five".left
          else state.right
      }

      case object EqualsFiveRule extends FiveRule {
        override def handle(state: DemoState): String \/ DemoState =
          if (state.number == 5) "Equals five".left
          else state.right
      }

      implicit class RuleListHandler[A, B](list: List[Rule[A, B]]) {
        def applyOnlyOneTailRec(request: B): A \/ B = {
          @tailrec
          def loop(req: B, rules: List[Rule[A, B]]): A \/ B = {
            if (rules.isEmpty) req.right
            else
              rules.head.handle(req) match {
                case -\/(value) => value.left
                case \/-(_)     => loop(req, rules.tail)
              }
          }
          loop(request, list)
        }

        def applyOnlyOneTraverse(request: B): List[B] \/ A = {
          list traverseU (_.handle(request)) swap
        }
      }

      val NumberRules = List(GreaterThanFiveRule, LessThanFiveRule, EqualsFiveRule)

      (NumberRules applyOnlyOneTraverse DemoState(8)) ==> \/-("Greater than five")
    }
  }
}
