

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestKit, TestActorRef}
import com.happyhour.Customer
import com.happyhour.Meal.{Burger, Steak, Sushi}
import com.happyhour.Waitress.{ServeMeal, MealServed}
import org.scalatest._
import org.scalatest.matchers.{ShouldMatchers, Matchers, ClassicMatchers}
import scala.concurrent.duration._
import org.scalatest.WordSpec
import org.scalatest._


/**
 * Created with IntelliJ IDEA.
 * User: Babalu
 * Date: 11/28/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
class CustomerSpec extends TestKit(ActorSystem("guest-spec")) with WordSpec with ClassicMatchers with BeforeAndAfterAll with ShouldMatchers {
  "Sending a MealServed message to a guest" should {
    "increase the mealCount" in {
      val guest = TestActorRef(new Customer(Sushi, 200 milliseconds, false, Int.MaxValue, system.deadLetters))
      guest.underlyingActor.mealCount should equal(0)
      guest ! MealServed(Sushi)
      guest.underlyingActor.mealCount should equal(1)
    }
  }

  "Creating a Customer" should {
    "result in sending a ServeMeal message to the Waiter" in {
      system.actorOf(Customer.props(Sushi, 500 milliseconds, false, Int.MaxValue, testActor))
      expectMsg(ServeMeal(Sushi))
    }
  }

  "Sending a MealServed message to a Customer" should {
    "result in sending a ServeMeal message to the Waiter after the finishMealDuration" in {
      val guest = system.actorOf(Customer.props(Burger, 500 milliseconds, false, Int.MaxValue, testActor))
      expectMsg(ServeMeal(Burger)) // Because of the startup behavior of a Customer
      within(450 milliseconds, 1 second) { // Relax the requirements for the durations, because the scheduler doesn't give real-time guarantees
        guest ! MealServed(Burger)
        expectMsg(ServeMeal(Burger))
      }
    }
  }

  "Sending a MealServed message to a Customer with a maxMealCount of 0" should {
    "result in a DrunkException after the finishMealDuration" in {
      val guest = system.actorOf(Customer.props(Steak, 500 milliseconds, false, 0, testActor))
      expectMsg(ServeMeal(Steak)) // Because of the startup behavior of a Customer
      within(450 milliseconds, 1 second) { // Relax the requirements for the durations, because the scheduler doesn't give real-time guarantees
        EventFilter[IllegalStateException](occurrences = 1) intercept (
          guest ! MealServed(Steak)
          )
      }
    }
  }
//  override protected def afterAll() = shutdown(system)
}
