/**
 * Created with IntelliJ IDEA.
 * User: Babalu
 * Date: 10/2/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
package com.happyhour

import scala.util.Random

sealed trait Meal

object Meal {

  val meals : Set[Meal]= Set(Sushi, Steak, Burger)

  case object Sushi extends Meal

  case object Steak extends Meal

  case object Burger extends Meal

  def anyOther(meal: Meal) : Meal = {
    val others = meals - meal
    others.toSeq(Random.nextInt(others.size))
  }
}
