package com.happyhour

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import com.happyhour.{Meal, Chef}
import org.scalatest.matchers.Matchers
import scala.concurrent.duration._
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import akka.testkit.TestKit
import akka.testkit.ImplicitSender

/**
 * Created with IntelliJ IDEA.
 * User: Babalu
 * Date: 10/19/13
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */
class ChefSpec extends TestKit(ActorSystem("chef-spec"))
with ImplicitSender with WordSpec with Matchers with BeforeAndAfterAll {

  import Chef._
  import Meal._

  "Sending a PrepareMeal message to a Chef" should {
    "result in a MealPrepared reply after the prepareMealDuration" in {
      val chef = system.actorOf(Chef.props(500 milliseconds, 100))
      val guest = system.deadLetters
      within(250 milliseconds, 2 seconds) { // The chef is not too accurate, hence we must relax the duration values!
        chef ! PrepareMeal(Sushi, guest)
        expectMsg(MealPrepared(Sushi, guest))
      }
    }
  }

//  override protected def afterAll() = shutdown(system)
}