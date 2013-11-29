package com.example.akka

import akka.actor._
import scala.concurrent.duration.{Duration, MILLISECONDS}
import org.scalatest.{FlatSpec, BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.{MustMatchers, Matchers, ShouldMatchers}
import akka.testkit.{TestActorRef, TestKit, ImplicitSender}
import org.scalatest._
import akka.testkit.{TestKit, TestProbe}
import com.happyhour.{HappyHour, Meal, Chef}


class HappyHourSpec extends TestKit(ActorSystem("happy-hour-spec")) with WordSpec with MustMatchers with BeforeAndAfterAll {

  import Chef._
  import Meal._
  import HappyHour._

  "Sending an PrepareMeal message to HappyHour" should {
    "result in forwarding the message to the chef if maxMealCount not yet reached" in {
      val happyHour = system.actorOf(Props(new HappyHour(1) {
        override def chefProps = Props(new Chef(chefPrepareMealDuration, chefAccuracy))

        override val chef = testActor
      }))
      val guest = TestProbe()
      happyHour ! PrepareMeal(Sushi, guest.testActor)
      expectMsg(PrepareMeal(Sushi, guest.testActor))
      guest.expectNoMsg()
    }
    "result in a NoMoreMeals reply to the guest if maxMealCount reached" in {
      val happyHour = system.actorOf(Props(new HappyHour(0) {
        override def chefProps = Props(new Chef(chefPrepareMealDuration, chefAccuracy))
      }))
      happyHour ! PrepareMeal(Sushi, testActor)
      expectMsg(NoMoreMeals)
    }
  }
//  override protected def afterAll() = shutdown(system)
}
