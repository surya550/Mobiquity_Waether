package com.mobiquity.surya.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng?, b: LatLng?): LatLng?
    class Spherical : LatLngInterpolator {
        override fun interpolate(fraction: Float, from: LatLng?, to: LatLng?): LatLng? {
            val fromLat = from?.let { Math.toRadians(it.latitude) }
            val fromLng = from?.let { Math.toRadians(it.longitude) }
            val toLat = to?.let { Math.toRadians(it.latitude) }
            val toLng = to?.let { Math.toRadians(it.longitude) }
            val cosFromLat = fromLat?.let { Math.cos(it) }
            val cosToLat = toLat?.let { Math.cos(it) }

            val angle = computeAngleBetween(fromLat!!, fromLng!!, toLat!!, toLng!!)
            val sinAngle = sin(angle)
            if (sinAngle < 1E-6) {
                return from
            }
            val a = sin((1 - fraction) * angle) / sinAngle
            val b = sin(fraction * angle) / sinAngle

            val x = a * cosFromLat!! * cos(fromLng) + b * cosToLat!! * cos(toLng)
            val y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng)
            val z = a * sin(fromLat) + b * sin(toLat)

            // Converts interpolated vector back to polar.
            val lat = atan2(z, Math.sqrt(x * x + y * y))
            val lng = atan2(y, x)
            return LatLng(Math.toDegrees(lat), Math.toDegrees(lng))
        }

        private fun computeAngleBetween(
            fromLat: Double,
            fromLng: Double,
            toLat: Double,
            toLng: Double
        ): Double {
            // Haversine's formula
            val dLat = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * Math.asin(
                Math.sqrt(
                    Math.pow(Math.sin(dLat / 2), 2.0) +
                            Math.cos(fromLat) * Math.cos(toLat) * Math.pow(Math.sin(dLng / 2), 2.0)
                )
            )
        }
    }
}
