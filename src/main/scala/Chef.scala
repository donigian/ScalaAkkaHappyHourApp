/**
 * Created with IntelliJ IDEA.
 * User: Babalu
 * Date: 10/2/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
package com.happyhour


import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.duration.FiniteDuration
import scala.util.Random
import com.happyhour.Chef.{MealPrepared, PrepareMeal}
import com.happyhour.Meal

object Chef {

  case class PrepareMeal(meal: Meal, guest: ActorRef)

  case class MealPrepared(meal: Meal, guest: ActorRef)

  def props(prepareMealDuration: FiniteDuration, accuracy: Int): Props =
    Props(new Chef(prepareMealDuration, accuracy))
}

class Chef(prepareMealDuration: FiniteDuration, accuracy: Int) extends Actor {

  def receive: Receive = {
    case PrepareMeal(meal, guest) => {
      busy(prepareMealDuration)
      sender ! MealPrepared(pickMeal(meal), guest)
    }

  }

  def pickMeal(meal: Meal): Meal = {
    if (Random.nextInt(100) < accuracy)
      meal
    else
      Meal.anyOther(meal)

  }

  def busy(duration: FiniteDuration): Unit = duration.toMillis * 500

}


