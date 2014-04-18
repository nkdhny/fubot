package ru.nkdhny.pf.noise

import ru.nkdhny.pf.domain._

/**
 * User: alexey
 * Date: 4/12/14
 * Time: 7:29 PM
 */
object IdealCase {
       implicit val nonoise = new NoiseGenerator[NoNoise] {
         def apply(): NoNoise = new NoNoise()
       }
}
