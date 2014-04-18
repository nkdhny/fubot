package ru.nkdhny.pf.domain

import org.scalatest.{Matchers, FlatSpec}

/**
 * User: alexey
 * Date: 4/16/14
 * Time: 7:00 PM
 */
class MathTest extends FlatSpec with Matchers {

  "Position" should "define addition as a vector" in {
    val i = Position(1,0,0)
    val k = Position(0,0,1)

    val ik = i + k

    ik.x shouldBe 1.0
    ik.y shouldBe 0
    ik.z shouldBe 1.0

  }

  it should "rotated properly" in {

    val i = Position(1, 0, 0)
    val j = Position(0, 1, 0)

    val fullRotation = new Quaternion(math.Pi/2.0)
    val halfRotation = new Quaternion(math.Pi/4.0)

    val fullRotated  = i rotate fullRotation
    val rotatedTwice = i rotate halfRotation rotate halfRotation
    val rotatedThereAndBack =i rotate halfRotation rotate halfRotation.inv()

    near(fullRotated, j) shouldBe true
    near(fullRotated, rotatedTwice) shouldBe true
    near(rotatedThereAndBack, i) shouldBe true
  }

  def random_vector(): Position = {
    Position(math.random, math.random, math.random)
  }

  def random_state(): RobotState = {
    RobotState(
      random_vector(),
      Quaternion(math.random, math.random, math.random, math.random).normed()
    )
  }

  def near(left: Position, right: Position): Boolean = {
    math.abs(left.x - right.x) < 1e-5 &&
      math.abs(left.y - right.y) < 1e-5 &&
      math.abs(left.z - right.z) < 1e-5
  }

  def near(left: Quaternion, right: Quaternion): Boolean = {

    val composition = left*right.inv()

    math.abs(composition.w - 1.0) < 1e-5 &&
    near(composition.vec(), Position(0,0,0))
  }

  def near(left: RobotState, right: RobotState): Boolean = {
    near(left.position, right.position) &&
    near(left.attitude, right.attitude)
  }

  "RobotState composition" should "fold inner state" in {
    val aInB = random_state()
    val bInC = random_state()

    val aInC = aInB*bInC

    val aInB_prime = aInC*(-bInC)
    val bInC_prime = (-aInB)*aInC

    near(aInB_prime, aInB) shouldBe true
    near(bInC_prime, bInC) shouldBe true

  }

  it should "project a vector" in {

    val vInA = random_vector()
    val aInB = random_state()
    val bInC = random_state()
    val aInC = aInB*bInC

    val vInC = vInA*aInC
    val vInC_prime = (vInA*aInB)*bInC
    val vInA_prime = vInC*(-aInC)

    near(vInC, vInC_prime) shouldBe true
    near(vInA, vInA_prime) shouldBe true

  }

}
