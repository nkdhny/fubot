package ru.nkdhny.pf.domain

/**
 * User: alexey
 * Date: 4/16/14
 * Time: 6:05 PM
 */
case class RobotState(position: Position, attitude: Quaternion) {
  def this(x: Double, y: Double, yaw: Double) = {
    this(
      Position(x, y, 0),
      Quaternion(math.cos(yaw/2.0), 0, 0, math.sin(yaw/2.0))
    )
  }

  def unary_-(): RobotState = {
    val q = attitude.inv()

    val translated = RobotState(Position(0,0,0), q)

    RobotState(-position*translated, translated.attitude)
  }

  def *(other: RobotState): RobotState = {
    val q10 = other.attitude
    val q21 = attitude

    val pos = position*other
    val att =q10*q21

    RobotState(pos, att)
  }
}
