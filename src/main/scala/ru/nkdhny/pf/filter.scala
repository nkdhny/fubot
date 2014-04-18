package ru.nkdhny.pf

import domain._
import measure._
import predictor._
import noise._
import sampling._
import java.util.concurrent.atomic.AtomicInteger

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 5:29 PM
 */


object ParticleFilter {

  def estimate(population: Traversable[RobotState]) = {
    case class EstimationAcc(total_pos: Position, total_att: Quaternion, pos_norm: Double, att_norm: Double) {
      def this() = {
        this(
          Position(0, 0, 0),
          Quaternion(1.0, 0.0, 0.0, 0.0),
          0.0,
          0.0
        )
      }
    }

    val acc = population.foldLeft(new EstimationAcc())((acc, state_to_visit) => {
      EstimationAcc(
        acc.total_pos + state_to_visit.position,
        acc.total_att + state_to_visit.attitude,
        acc.pos_norm + 1.0,
        acc.att_norm + state_to_visit.attitude.norm()
      )
    })

    RobotState(
      acc.total_pos/acc.pos_norm,
      acc.total_att/acc.att_norm
    )
  }


  case class Population(particles: Seq[RobotState])
}

trait ParticleFilter[NoiseType<:Noise] {

  protected val sampler: Sampler

  import ParticleFilter._

  protected def predict
  (state: RobotState, noise: NoiseType, predictor: GenericPredictor[NoiseType]): RobotState = {

    predictor.move(state, noise)
  }

  protected def weight[DataType<:MeasurementData]
    (state: RobotState, data: DataType, measurement: GenericMeasurement[DataType]): Probability = {

    measurement.sense(data, state)
  }

  def move[DataType<:MeasurementData]
   (state: RobotState, data: DataType)
   (implicit predictor: GenericPredictor[NoiseType],
             measurement: GenericMeasurement[DataType],
             noise: NoiseGenerator[NoiseType]):
    (Probability, RobotState) = {

    val predicted = predict(state, noise(), predictor)

    (weight(predicted, data, measurement), predicted)
  }

  def apply[DataType<:MeasurementData]
  (population: Population, data: DataType)
  (implicit predictor: GenericPredictor[NoiseType],
            measurement: GenericMeasurement[DataType],
            noise: NoiseGenerator[NoiseType]):
  Population = {

    val weighted_population = population.particles.par.map(particle => move(particle, data)(predictor, measurement, noise))
    val new_population = Population(sampler(weighted_population.seq))

    new_population
  }

}