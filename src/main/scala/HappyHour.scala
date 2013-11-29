package com.happyhour

import akka.actor._
import scala.concurrent.duration._
import com.happyhour.HappyHour
import akka.routing.FromConfig


object HappyHour {
  def props(maxMealCount: Int): Props =
    Props(new HappyHour(maxMealCount))

  case object NoMoreMeals

}


class HappyHour(maxMealCount: Int) extends Actor with ActorLogging {

  val chefPrepareMealDuration =
    Duration(
      context.system.settings.config getMilliseconds "happy-hour.chef.prepare-meal-duration",
      MILLISECONDS
    )

  val chefAccuracy = context.system.settings.config getInt "happy-hour.meal.accuracy"

  val chef = context.actorOf(chefProps, "chef")

  def receive: Receive = {
    case _ =>
  }

  // Overridden in the tests to create a Chef without router
  protected def chefProps: Props =
    Chef.props(chefPrepareMealDuration, chefAccuracy).withRouter(FromConfig())
}