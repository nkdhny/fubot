package ru.nkdhny.pf.domain

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 9:38 PM
 */

object Quaternion {
  val i = Quaternion(0, 1, 0, 0)
  val j = Quaternion(0, 0, 1, 0)
  val k = Quaternion(0, 0, 0, 1)
}

case class Quaternion(w: Double, x: Double, y: Double, z: Double) {
  def this() = {
    this(1,0,0,0)
  }

  def this(w: Double, v: Position) = {
    this(w, v.x, v.y, v.z)
  }

  def this(yaw: Double) = {
    this(math.cos(yaw/2.0), 0, 0, math.sin(yaw/2.0))
  }

  def *(other: Quaternion): Quaternion = {
    Quaternion(
      w*other.w - x*other.x - y*other.y - z*other.z,
      w*other.x + x*other.w + y*other.z - z*other.y,
      w*other.y - x*other.z + y*other.w + z*other.x,
      w*other.z + x*other.y - y*other.x + z*other.w
    )
  }

  def +(other: Quaternion) = {
    Quaternion(
      w + other.w,
      x + other.x,
      y + other.y,
      z + other.z
    )
  }

  def *(scalar: Double) = {
    Quaternion(
      scalar*w,
      scalar*x,
      scalar*y,
      scalar*z
    )
  }

  def /(scalar: Double) = {
    this*(1/scalar)
  }

  def inv(): Quaternion = {
    import Quaternion._
    val q = this
    (q + i*q*i + j*q*j + k*q*k)*(-0.5)
  }

  def norm(): Double = {
    math.sqrt(w*w + x*x + y*y + z*z)
  }

  def normed(): Quaternion = {
    val q = this

    q*(1/q.norm())
  }

  def vec(): Position = {
    Position(x, y, z)
  }

}