package ru.nkdhny.pf.sampling

import ru.nkdhny.pf.domain._

/**
 * User: alexey
 * Date: 4/15/14
 * Time: 10:09 AM
 */
class WheelSampler extends Sampler {


  def apply(weighted_population: Seq[(Probability, RobotState)]): Seq[RobotState] = {
    val max_weight = weighted_population.maxBy(_._1)._1
    val number_of_particles = weighted_population.size

    def prob_step(): Probability = math.random*2.0*max_weight

    def drop_while_weight_is_big_enough(choice:Int, rest_of_weight: Probability): (Int, Probability) = {
      val weight_of_the_choice= weighted_population(choice)._1

      if( weight_of_the_choice >= rest_of_weight) (choice, rest_of_weight)
      else drop_while_weight_is_big_enough((choice+1)%number_of_particles, rest_of_weight - weight_of_the_choice)
    }

    def choose_one_more(prev_choice: Int, prev_prob: Probability, new_population: List[RobotState]): List[RobotState] = {
      val (next_choice, next_prob) = drop_while_weight_is_big_enough(prev_choice, prev_prob+prob_step())

      if(new_population.size == number_of_particles - 1) weighted_population(next_choice)._2::new_population
      else choose_one_more(next_choice, next_prob, weighted_population(next_choice)._2::new_population)
    }

    val start_from = (math.random*number_of_particles).toInt

    choose_one_more(start_from, 0, Nil)

  }
}
