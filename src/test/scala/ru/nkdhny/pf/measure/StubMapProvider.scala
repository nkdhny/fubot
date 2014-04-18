package ru.nkdhny.pf.measure

import ru.nkdhny.pf.domain._

import org.opencv.highgui.Highgui
import org.opencv.imgproc.Imgproc
import org.opencv.core._
import ru.nkdhny.pf.domain.{PointCloud, RobotState}

/**
 * User: alexey
 * Date: 4/15/14
 * Time: 11:15 AM
 */
trait StubMapProvider {


  val x_scale: Double
  val y_scale: Double

  val map_name = "./resources/map.png"
  val image_mat =  Highgui.imread(map_name)

  assert(image_mat.cols() > 0 && image_mat.rows() > 0)
  val map_mat: Mat = Mat.eye(image_mat.size(), CvType.CV_8UC1)

  if(image_mat.`type`() != CvType.CV_8UC1) {
    Imgproc.cvtColor(image_mat, map_mat, Imgproc.COLOR_RGB2GRAY)
  } else {
    image_mat.copyTo(map_mat)
  }
  val vision_dist = (0.2 * image_mat.rows()).toInt

  val whole_area = new Rect(0,0, map_mat.cols(), map_mat.rows())

  def intersection(a: Rect, b: Rect): Option[Rect] = {
    case class Segment(start: Int, end: Int) {
      def *(other: Segment): Option[Segment] = {
        if(other.start > end || other.end < start)
          None
        else {
          Some(Segment(math.max(start, other.start), math.min(end, other.end)))
        }
      }

      def length: Int = {
        val l = end-start
        assert(l>=0)

        l
      }
    }

    val a_x = Segment(a.x, a.x+a.width)
    val b_x = Segment(b.x, b.x+b.width)
    val a_y = Segment(a.y, a.y+a.height)
    val b_y = Segment(b.y, b.y+b.height)

    for {
      x_intersection <- a_x*b_x
      y_intersection <- a_y*b_y
    } yield {
      new Rect(
        x_intersection.start,
        y_intersection.start,
        x_intersection.length,
        y_intersection.length
      )
    }

  }

  protected def select_points_from_roi(roi: Rect, sensor_state: RobotState): PointCloud = {
    val world_mat = map_mat.submat(roi)
    val world_seen_from_sensor = -sensor_state

    PointCloud(
      (
        for{
          row <- 0 until world_mat.rows()
          col <- 0 until world_mat.cols()
        } yield {
          if(world_mat.get(row, col)(0) > 0)
            Some(
              Position(
                col*x_scale,
                -row*y_scale,
                sensor_state.position.z
              )*world_seen_from_sensor
            )
          else
            None
        }
        ).flatten
    )
  }

  def around(sensor_state: RobotState): PointCloud = {

    val view_rect = intersection(new Rect(
      ( sensor_state.position.x*x_scale - vision_dist/2).toInt,
      (-sensor_state.position.y*y_scale - vision_dist/2).toInt,
      vision_dist,
      vision_dist
    ), whole_area)

    view_rect match {
      case None => PointCloud(Nil)
      case Some(roi: Rect) => {
        select_points_from_roi(roi, sensor_state)
      }
    }

  }

  def measure(state: RobotState): PointCloud = around(state)

}
