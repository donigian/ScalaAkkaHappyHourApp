/**
 * Created with IntelliJ IDEA.
 * User: Babalu
 * Date: 11/28/13
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
package com.happyhour

import akka.actor.{ Actor, ActorRef, Props }
import akka.actor.Actor.Receive
import com.happyhour.Waitress.ServeMeal

object Waitress {

  case class ServeMeal(meal: Meal)

  case class MealServed(meal: Meal)

  case class Complaint(meal: Meal)

  case class CustomerCompliant(meal: Meal, customer: ActorRef) extends IllegalStateException("Bad service complaint!")

  def props(maxCompliantCount: Int, happyHour: ActorRef, chef: ActorRef): Props =
    Props(new Waitress(maxCompliantCount, happyHour, chef))
}

class Waitress(maxComplaintCount: Int, happyHour: ActorRef, chef: ActorRef) extends Actor {
  var complaintCount = 0
  import Chef._
  import Waitress._

  def receive: Receive = {
    case ServeMeal(meal) =>
      happyHour ! PrepareMeal(meal, sender)
    case MealPrepared(meal, customer) =>
      customer ! MealServed(meal)
    case Complaint(meal) if complaintCount == maxComplaintCount =>
      throw new CustomerCompliant(meal, sender)
    case Complaint(meal) =>
      complaintCount += 1
      chef ! PrepareMeal(meal, sender)
  }
}
