package ru.nkdhny.pf

import domain._

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 6:15 PM
 */
package object sampling {

  trait Sampler {
    def apply(weighted_population: Seq[(Probability, RobotState)]): Seq[RobotState]
  }

}
