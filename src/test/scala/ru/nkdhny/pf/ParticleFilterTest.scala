package ru.nkdhny.pf

import org.scalatest.{Matchers, FlatSpec}
import ru.nkdhny.pf.domain._
import ru.nkdhny.pf.sampling.{WheelSampler, Sampler}
import ru.nkdhny.pf.measure.{IntersectionDot, StubMapProvider}
import ru.nkdhny.pf.measure.PlanarSpaceAroundMeasurement.CompareWithMap
import ru.nkdhny.pf.domain.Position
import ru.nkdhny.pf.ParticleFilter.Population
import ru.nkdhny.pf.domain.RobotState
import com.atul.JavaOpenCV.Imshow
import org.opencv.core._
import scala.collection.JavaConversions._
import ru.nkdhny.pf.domain.Position
import ru.nkdhny.pf.ParticleFilter.Population
import ru.nkdhny.pf.domain.RobotState
import ru.nkdhny.pf.domain.PointCloud

/**
 * User: alexey
 * Date: 4/16/14
 * Time: 8:29 PM
 */
class ParticleFilterTest extends FlatSpec with Matchers {

  def random_state_near(base: RobotState, displacement: Double = 100) = {
    val x = base.position.x + (math.random - 0.1)*displacement
    val y = base.position.y + (math.random - 0.1)*displacement
    val z = 0

    RobotState(
      Position(if(x<0) 0 else x, if(y>0) 0 else y, z),
      new Quaternion(0.0)
    )
  }

  "A particle filter" should "provide an estimation in a sound time" ignore  {

    val base  = RobotState(
      Position(300,-190,0),
      new Quaternion(0.0)
    )
    val population = new Population((0 until 500).map(i => random_state_near(base)))

    val filter = new ParticleFilter[NoNoise] {
      val sampler: Sampler = new WheelSampler
    }

    val map_provider = new StubMapProvider {
      val y_scale: Double = 1.0
      val x_scale: Double = 1.0
    }

    import ru.nkdhny.pf.noise.IdealCase._
    import ru.nkdhny.pf.predictor.IdleObject._

    implicit val measurement = new CompareWithMap with IntersectionDot with StubMapProvider {
      protected val cols: Int = 100
      protected val rows: Int = 100
      val x_scale: Double = 1.0
      val y_scale: Double = 1.0
    }

    val data = map_provider.measure(base)
    val start = System.currentTimeMillis()
    val estimation = filter.apply(population, data)
    val end = System.currentTimeMillis()
    println(s"pf itself took ${(end-start).toDouble/1000.0} sec")

    val w = new Imshow("measured")
    w.showImage(data.toMat(100, 100))


    val map = map_provider.map_mat
    val initial = PointCloud(population.particles.map(_.position)).toMat(0, map.cols(), -map.rows(), 0, map.rows(),map.cols())
    val pop = PointCloud(estimation.particles.map(_.position)).toMat(0, map.cols(), -map.rows(), 0, map.rows(),map.cols())

    val popOnTheMap: Mat = new Mat()

    Core.merge(map::pop::initial::Nil, popOnTheMap)

    val was =  ParticleFilter.estimate(population.particles)
    val now =  ParticleFilter.estimate(estimation.particles)

    Core.circle(popOnTheMap, new Point(base.position.x, - base.position.y), 10, new Scalar(100,0,0), 2)
    Core.circle(popOnTheMap, new Point(was.position.x, - was.position.y), 10, new Scalar(0,0,100), 2)
    Core.circle(popOnTheMap, new Point(now.position.x, - now.position.y), 10, new Scalar(0,255,0), 2)


    val r = new Imshow("population")
    r.showImage(popOnTheMap)

    println(now)
    println(was)

  }

}
