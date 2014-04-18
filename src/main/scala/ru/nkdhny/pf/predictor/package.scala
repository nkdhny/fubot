package ru.nkdhny.pf

import ru.nkdhny.pf.domain._

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 5:21 PM
 */
package object predictor {
  trait GenericPredictor[-NoiseType<:Noise] {
    def move(state: RobotState, noise: NoiseType): RobotState
  }
}
