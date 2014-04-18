package ru.nkdhny.pf.measure

import org.scalatest.{Matchers, FlatSpec}
import ru.nkdhny.pf.measure.PlanarSpaceAroundMeasurement.CompareWithMap
import ru.nkdhny.pf.domain._
import org.opencv.core.{Core, Mat, CvType}
import org.opencv.highgui.Highgui
import ru.nkdhny.pf.domain.Position
import ru.nkdhny.pf.domain.RobotState


/**
 * User: alexey
 * Date: 4/17/14
 * Time: 12:42 PM
 */
class PlanarSpaceAroundMeasurementTest extends FlatSpec with Matchers {

  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val dataProvider = new StubMapProvider {
    val x_scale: Double = 1.0
    val y_scale: Double = 1.0
  }


  "Intersection dot" should "count colored points" in {


    val m = new CompareWithMap with IntersectionDot with StubMapProvider {
      protected val cols: Int = 100
      protected val rows: Int = 100
      val x_scale: Double = 1.0
      val y_scale: Double = 1.0
    }

    val top_left = RobotState(Position(0,0,0), new Quaternion(0.0))

    val dat = dataProvider.around(top_left)

    m.dot(dat, dat) shouldBe 1.0

    m.dot(dat, PointCloud(Nil)) shouldBe 0.0

  }

}
