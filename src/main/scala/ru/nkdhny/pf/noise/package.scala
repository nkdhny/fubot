package ru.nkdhny.pf

import domain._

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 5:50 PM
 */
package object noise {

  trait NoiseGenerator[NoiseType <: Noise] {
    def apply(): NoiseType
  }

}
