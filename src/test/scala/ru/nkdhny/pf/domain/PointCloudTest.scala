package ru.nkdhny.pf.domain

import org.scalatest.{Matchers, FlatSpec}
import org.opencv.core._

/**
 * Created by golomedov on 4/18/14.
 */
class PointCloudTest extends FlatSpec with Matchers {

  //System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  def pick_a_point_from_circle(phi: Double): Position = Position(math.cos(phi), math.sin(phi), math.random)

  def pick_a_point_from_line(l: Double): Position = Position(l, 0, math.random)

  "A point cloud" should "convert to a matrix properly" in {

    val points_on_a_circle = (0 to (2*math.Pi*100).toInt) map (_/100.0) map pick_a_point_from_circle
    val points_on_x_axis = (0 to 100) map (_/100.0) map pick_a_point_from_line

    val cloud = PointCloud(points_on_a_circle ++ points_on_x_axis)

    val cloud_mat = cloud.toMat(100, 100)
    val draw = Mat.zeros(cloud_mat.rows(), cloud_mat.cols(), CvType.CV_8UC1)

    Core.circle(draw, new Point(50,50), 50, new Scalar(255), 5)
    Core.line(draw, new Point(48,50), new Point(100, 50), new Scalar(255))
    Core.line(draw, new Point(48,49), new Point(100, 49), new Scalar(255))
    Core.line(draw, new Point(48,51), new Point(100, 51), new Scalar(255))

    val and = new Mat()
    Core.bitwise_and(draw, cloud_mat, and)

    Core.countNonZero(and) shouldBe Core.countNonZero(cloud_mat)

    val cloud_large = cloud.toMat(-1.0, 2.0, -1.5, 0.05, 150, 300)

    cloud_large.rows() shouldBe 150
    cloud_large.cols() shouldBe 300

    val draw_large = Mat.zeros(cloud_large.rows(), cloud_large.cols(), CvType.CV_8UC1)

    Core.ellipse(draw_large, new Point(100, 0), new Size(100, 100), 0.0 , -180 , 180, new Scalar(255), 7)
    Core.line(draw_large, new Point(0,0), new Point(200, 0), new Scalar(255))
    Core.line(draw_large, new Point(0,1), new Point(200, 1), new Scalar(255))
    Core.line(draw_large, new Point(90,2), new Point(200, 2), new Scalar(255))
    Core.line(draw_large, new Point(90,3), new Point(200, 3), new Scalar(255))
    Core.line(draw_large, new Point(90,4), new Point(200, 4), new Scalar(255))
    Core.line(draw_large, new Point(90,5), new Point(200, 5), new Scalar(255))
    Core.line(draw_large, new Point(90,6), new Point(200, 6), new Scalar(255))
    Core.line(draw_large, new Point(90,7), new Point(200, 7), new Scalar(255))

    val and_large = new Mat()
    Core.bitwise_and(draw_large, cloud_large, and_large)

    Core.countNonZero(and_large) shouldBe Core.countNonZero(cloud_large)

  }

  it should "be translated androtated there and back gives same cloud" in {

    val points_on_a_circle = (0 to (2*math.Pi*100).toInt) map (_/100.0) map pick_a_point_from_circle
    val points_on_x_axis = (0 to 100) map (_/100.0) map pick_a_point_from_line

    val cloud = PointCloud(points_on_a_circle ++ points_on_x_axis)

    val rotation = new Quaternion(2*(math.random-0.5)*math.Pi)
    val translation = Position(math.random, math.random, math.random)

    val state = RobotState(translation, rotation)

    val cloud_prime = PointCloud(cloud.points.map(_*state).map(_*(-state)))

    val cloud_mat = cloud.toMat()
    val cloud_prime_mat = cloud_prime.toMat()

    val and = new Mat()

    Core.bitwise_and(cloud_mat, cloud_prime_mat, and)

    Core.countNonZero(and) shouldBe Core.countNonZero(cloud_mat)

  }

  it should "rotate properly" in {
    val points_on_a_circle = (0 to (2*math.Pi*100).toInt) map (_/100.0) map pick_a_point_from_circle
    val points_on_x_axis = (0 to 100) map (_/100.0) map pick_a_point_from_line

    val rotation = new Quaternion(math.Pi/2)
    val translation = Position(0,0,0)
    val move = RobotState(translation, rotation)

    val cloud = PointCloud((points_on_a_circle ++ points_on_x_axis).map(_*move))

    val cloud_mat = cloud.toMat(100, 100)
    val draw = Mat.zeros(cloud_mat.rows(), cloud_mat.cols(), CvType.CV_8UC1)

    Core.circle(draw, new Point(50,50), 50, new Scalar(255), 5)
    Core.line(draw, new Point(49,51), new Point(49, 0), new Scalar(255))
    Core.line(draw, new Point(50,51), new Point(50, 0), new Scalar(255))
    Core.line(draw, new Point(51,51), new Point(51, 0), new Scalar(255))

    val and = new Mat()
    Core.bitwise_and(draw, cloud_mat, and)

    Core.countNonZero(and) shouldBe Core.countNonZero(cloud_mat)
  }

}
