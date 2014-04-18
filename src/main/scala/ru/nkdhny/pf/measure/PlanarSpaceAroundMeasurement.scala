package ru.nkdhny.pf.measure

import ru.nkdhny.pf.domain._

import org.opencv.core.{Core=>cv, Mat, CvType}
import org.opencv.highgui.Highgui

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 8:10 PM
 */

trait IntersectionDot {

  protected val cols: Int
  protected val rows: Int

  def dot(measured: PointCloud, expected: PointCloud): Probability = {

    if(expected.points.isEmpty) {
      if(measured.points.isEmpty) {
        return 1.0
      } else {
        return 0.0
      }
    }

    def min_max(cloud: PointCloud, extract: Position => Double) = {
      val extracted = cloud.points.map(extract)

      (extracted.min, extracted.max)
    }

    val expected_square = (
        min_max(expected, _.x),
        min_max(expected, _.y)
    )



    val exp = expected.toMat(expected_square._1._1, expected_square._1._2, expected_square._2._1, expected_square._2._2, rows, cols)
    val mes = measured.toMat(expected_square._1._1, expected_square._1._2, expected_square._2._1, expected_square._2._2, rows, cols)

    val intersection = Mat.zeros(rows, cols, CvType.CV_8UC1)
    cv.bitwise_and(exp, mes, intersection)

    val total = cv.countNonZero(intersection)
    val norm = cv.countNonZero(exp)

    if(norm == 0 && total == 0){
      1.0
    } else if(norm == 0 && total !=0){
      0.0
    } else {
      math.exp(-10*math.pow(1 - total.toDouble/norm.toDouble, 2.0))
    }

  }
}

object PlanarSpaceAroundMeasurement {

  trait CompareWithMap extends GenericMeasurement[PointCloud] {

    def around(state: RobotState): PointCloud
    def dot(measured: PointCloud, expected: PointCloud): Probability

    def sense(data: PointCloud, state: RobotState): Probability = {
      dot(data, around(state))
    }
  }

}
