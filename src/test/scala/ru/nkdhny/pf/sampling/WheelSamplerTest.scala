package ru.nkdhny.pf.sampling

import org.scalatest.{Matchers, FlatSpec}
import ru.nkdhny.pf.domain._
import scala.collection.mutable

/**
 * User: alexey
 * Date: 4/15/14
 * Time: 10:37 AM
 */
class WheelSamplerTest extends FlatSpec with Matchers {

  "A wheel sampler" should "produce new population without impossible states" in {
    val first_state = new RobotState(0,0,0)
    val impossible_state = new RobotState(0,0, 3.1415)

    val population = (0.5, first_state)::(0.5, first_state)::(0.0, impossible_state)::Nil

    val sampler = new WheelSampler()
    val resampled_population = sampler(population)

    resampled_population.size shouldBe population.size
    resampled_population should not contain(impossible_state)
    resampled_population should contain(first_state)
  }

  it should "produce uniform distribution when weights are all equal" in {
    def make_a_state(i:Int): RobotState = {
      new RobotState(i,0,0)
    }

    val population = (0 to 100).map(make_a_state).map((1.0, _))

    val sampler = new WheelSampler()
    val resampled_builder = mutable.HashSet[RobotState]()
    (0 to 10).foreach(i=> resampled_builder ++= sampler(population))
    val resampled_population = resampled_builder.result()

    resampled_population.size shouldBe population.size

  }

}
