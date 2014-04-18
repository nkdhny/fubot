package ru.nkdhny.pf.domain

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 9:37 PM
 */
case class Position(x: Double, y: Double, z: Double) {
  def this() = {
    this(0,0,0)
  }

  def rotate(q: Quaternion): Position = {
    val v = new Quaternion(0, this)
    ((q*v)*q.inv()).vec()
  }

  def +(other: Position): Position  = {
    Position(
      x + other.x,
      y + other.y,
      z + other.z
    )
  }

  def *(rs: RobotState): Position = {
    rs.position + this.rotate(rs.attitude)
  }

  def unary_-(): Position = {
    Position(-x,-y,-z)
  }

  def *(scalar: Double): Position = {
    Position(x*scalar, y*scalar, z*scalar)
  }

  def /(scalar: Double) = this*(1/scalar)
}