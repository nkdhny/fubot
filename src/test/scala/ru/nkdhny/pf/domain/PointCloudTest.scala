package ru.nkdhny.pf.domain

import org.scalatest.{Matchers, FlatSpec}
import org.opencv.core._
import ru.nkdhny.pf.domain.Position
import ru.nkdhny.pf.domain.PointCloud

/**
 * Created by golomedov on 4/18/14.
 */
class PointCloudTest extends FlatSpec with Matchers {

  def pick_a_point_from_circle(phi: Double): Position = Position(math.cos(phi), math.sin(phi), math.random)

  def pick_a_point_from_line(l: Double): Position = Position(l, 0, math.random)

  "A point cloud" should "convert to a matrix properly" in {

    val points_on_a_circle = (0 to (2*math.Pi*100).toInt) map (_/100.0) map pick_a_point_from_circle
    val points_on_x_axis = (0 to 100) map (_/100.) map pick_a_point_from_line

    val cloud = PointCloud(points_on_a_circle ++ points_on_x_axis)

    val cloud_mat = cloud.toMat(100, 100)
    val draw = Mat.zeros(cloud_mat.rows(), cloud_mat.cols(), CvType.CV_8UC1)

    Core.circle(draw, new Point(50,50), 49, new Scalar(255), 4)
    Core.line(draw, new Point(50,50), new Point(100, 50), new Scalar(255))
    Core.line(draw, new Point(50,49), new Point(100, 49), new Scalar(255))
    Core.line(draw, new Point(50,51), new Point(100, 51), new Scalar(255))

    val and = new Mat()
    Core.bitwise_and(draw, cloud_mat, and)

    Core.countNonZero(and) shouldBe Core.countNonZero(cloud_mat)

  }

}
