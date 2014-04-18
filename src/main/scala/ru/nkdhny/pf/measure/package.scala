package ru.nkdhny.pf

import ru.nkdhny.pf.domain._

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 5:07 PM
 */
package object measure {

  trait GenericMeasurement[-MeasurementDataType <: MeasurementData] {
    def sense(data: MeasurementDataType, state: RobotState): Probability
  }

}
