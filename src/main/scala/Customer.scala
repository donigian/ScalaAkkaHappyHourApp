/**
 * Created with IntelliJ IDEA.
 * User: Babalu
 * Date: 11/28/13
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
package com.happyhour

import scala.concurrent.duration.FiniteDuration
import akka.actor.{ActorLogging, Actor, Props, ActorRef}
import com.happyhour.Waitress.{MealServed, Complaint, ServeMeal}
import com.happyhour.HappyHour
import com.happyhour.Waitress.MealServed
import com.happyhour.Waitress.Complaint
import com.happyhour.HappyHour.NoMoreMeals
import com.happyhour.Waitress.ServeMeal


object Customer {

  private case object MealFinished

  case object StomachFullException extends IllegalStateException("Too many meals!")

  def props(
             favoriteMeal: Meal,
             finishedMealDuration: FiniteDuration,
             isStubborn: Boolean,
             maxMealCount: Int,
             waiter: ActorRef): Props =
    Props(new Customer(favoriteMeal, finishedMealDuration, isStubborn, maxMealCount, waiter))
}

class Customer(
                favoriteMeal: Meal,
                finishMealDuration: FiniteDuration,
                isStubborn: Boolean,
                maxMealCount: Int,
                waiter: ActorRef)
  extends Actor with ActorLogging {

  import Customer._
  import HappyHour._
  import context.dispatcher

  var mealCount = 0

  orderFavoriteMeal()

  override def receive: Receive = {
    case MealServed(`favoriteMeal`) =>
      mealCount += 1
      log.info("Eating my {}, delicious {} ", mealCount, favoriteMeal)
      context.system.scheduler.scheduleOnce(finishMealDuration, self, MealFinished)
    case MealServed(meal) =>
      log.info("Expected a {}, but got a {}!", favoriteMeal, meal)
      waiter ! Complaint(favoriteMeal)
    case MealFinished if mealCount > maxMealCount =>
      throw StomachFullException
    case MealFinished =>
      orderFavoriteMeal()
    case NoMoreMeals if isStubborn =>
      orderFavoriteMeal()
    case NoMoreMeals =>
      log.info("All right, time to go home!")
      context.stop(self)
  }
  override def postStop(): Unit =
    log.info("Good-bye!")

  def orderFavoriteMeal(): Unit =
    waiter ! ServeMeal(favoriteMeal)

}
