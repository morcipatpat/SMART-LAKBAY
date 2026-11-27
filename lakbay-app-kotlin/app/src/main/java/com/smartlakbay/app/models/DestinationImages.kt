package com.smartlakbay.app.models

import com.smartlakbay.app.R

// Ported from getDestinationImages in App.js — maps a destination name
// to its local photo gallery (main image + extra views).
data class DestinationImages(
    val main: Int,
    val views: List<Int>
)

fun getDestinationImages(dest: String): DestinationImages = when {
    dest.contains("Taal") -> DestinationImages(
        R.drawable.taal,
        listOf(R.drawable.taal_2, R.drawable.taal_3, R.drawable.taal_4)
    )
    dest.contains("Camp John Hay") -> DestinationImages(
        R.drawable.camp_john_hay,
        listOf(R.drawable.camp_john_hay_2, R.drawable.camp_john_hay_3, R.drawable.camp_john_hay_4)
    )
    dest.contains("Baguio Cathedral") || dest.contains("Baguio") -> DestinationImages(
        R.drawable.baguio_cathedral,
        listOf(R.drawable.baguio_cathedral_2, R.drawable.baguio_cathedral_3, R.drawable.baguio_cathedral_4)
    )
    dest.contains("Nasugbu") -> DestinationImages(
        R.drawable.nasugbu,
        listOf(R.drawable.nasugbu_2, R.drawable.nasugbu_3, R.drawable.nasugbu_5)
    )
    dest.contains("People") || dest.contains("Tagaytay") -> DestinationImages(
        R.drawable.peoples_park,
        listOf(R.drawable.peoples_park_2, R.drawable.peoples_park_3, R.drawable.peoples_park_4)
    )
    dest.contains("Twin Lakes") -> DestinationImages(
        R.drawable.twin_lakes,
        listOf(R.drawable.twin_lakes_2, R.drawable.twin_lakes_3)
    )
    dest.contains("Lipa") -> DestinationImages(
        R.drawable.cafe_de_lipa,
        listOf(R.drawable.cafe_de_lipa_2, R.drawable.cafe_de_lipa_3, R.drawable.cafe_de_lipa_4)
    )
    dest.contains("Vigan") || dest.contains("Crisologo") -> DestinationImages(
        R.drawable.calle_crisologo,
        listOf(R.drawable.calle_crisologo_2, R.drawable.calle_crisologo_3, R.drawable.calle_crisologo_4)
    )
    dest.contains("La Union") || dest.contains("San Juan") -> DestinationImages(
        R.drawable.san_juan_beach,
        listOf(R.drawable.san_juan_beach_2, R.drawable.san_juan_beach_3, R.drawable.san_juan_beach_4)
    )
    dest.contains("Bicol") || dest.contains("Cagsawa") -> DestinationImages(
        R.drawable.cagsawa_ruins,
        listOf(R.drawable.cagsawa_ruins_2)
    )
    dest.contains("Manila") || dest.contains("Intramuros") -> DestinationImages(
        R.drawable.intramuros,
        listOf(R.drawable.intramuros_2, R.drawable.intramuros_3, R.drawable.intramuros_4)
    )
    dest.contains("Taguig") || dest.contains("BGC") -> DestinationImages(
        R.drawable.bgc,
        listOf(R.drawable.bgc_2, R.drawable.bgc_3, R.drawable.bgc_4)
    )
    else -> DestinationImages(R.drawable.default_place, emptyList())
}
