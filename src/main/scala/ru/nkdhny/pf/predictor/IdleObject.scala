package ru.nkdhny.pf.predictor

import ru.nkdhny.pf.domain._

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 7:57 PM
 */
object IdleObject {

  implicit val idle = new GenericPredictor[NoNoise] {
    def move(state: RobotState, noise: NoNoise): RobotState = {
      state
    }
  }

}
