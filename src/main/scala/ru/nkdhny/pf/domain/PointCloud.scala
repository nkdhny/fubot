package ru.nkdhny.pf.domain

import org.opencv.core.{CvType, Mat}

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 8:07 PM
 */


case class PointCloud(points: Traversable[Position]) extends MeasurementData {
  def toMat(low_x: Double, high_x: Double, low_y: Double, high_y: Double, rows: Int, cols: Int): Mat = {
    def row(y: Double) = {
      val low = low_y
      val high = high_y

      assert(high >= low)

      val all = high - low

      if(y<=low)
        rows
      else if(y>=high)
        0
      else
        ((high-y)/all*(rows-1)).toInt
    }

    def col(x: Double) = {
      val low = low_x
      val high = high_x

      assert(high >= low)

      val all = high - low

      if(x<=low)
        0
      else if(x>=high)
        cols
      else
        ((x-low)/all*(cols-1)).toInt
    }

    def make_a_mat(cloud: PointCloud) = {
      val mat = Mat.zeros(rows, cols, CvType.CV_8UC1)

      for{
        point <- cloud.points
      } {
        val i = row(point.y)
        val j = col(point.x)

        mat.put(i, j, 255.0)
      }
      mat
    }

    make_a_mat(this)
  }

  def toMat(rows: Int = points.size, cols: Int = points.size): Mat = {

    def zeroOr(extract: Traversable[Position] => Double): Double = {
      if(points.isEmpty) 0
      else extract(points)
    }

    val low_x  = zeroOr(_.map(_.x).min)
    val low_y  = zeroOr(_.map(_.y).min)
    val high_x = zeroOr(_.map(_.x).max)
    val high_y = zeroOr(_.map(_.y).max)

    toMat(low_x, high_x, low_y, high_y, rows, cols)
  }
}