package com.smartlakbay.app.models

import android.content.Context

data class SavedTrip(
    val id: String,
    val name: String,
    val date: String,
    val meta: String,
    val done: Boolean,
    val destination: String,
    val budget: Int,
    val spotKeys: String
)

object TripStore {
    private const val PREFS = "smart_lakbay_trips"
    private const val KEY = "list"

    fun all(context: Context): List<SavedTrip> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: ""
        if (raw.isEmpty()) {
            val seeds = listOf(
                SavedTrip(
                    "1", "Taal Heritage Trip", "2026-11-08",
                    "Nov 8, 2026 • 4 Places Included", false,
                    "Taal Heritage Town", 3500,
                    "marianOrchard,outletsLima,srLipa,smLipa"
                ),
                SavedTrip(
                    "2", "Nasugbu Beach Weekend", "2026-11-15",
                    "Rated: ⭐⭐⭐⭐⭐", true,
                    "Nasugbu", 3500,
                    "peoplesPark,twinLakesTagaytay"
                )
            )
            persist(context, seeds)
            return seeds
        }
        return raw.split("\n").filter { it.isNotBlank() }.mapNotNull { line ->
            val p = line.split("|")
            if (p.size < 8) null else SavedTrip(
                id = p[0], name = p[1], date = p[2], meta = p[3],
                done = p[4] == "true", destination = p[5],
                budget = p[6].toIntOrNull() ?: 0, spotKeys = p[7]
            )
        }
    }

    fun add(context: Context, trip: SavedTrip) = persist(context, all(context) + trip)
    fun update(context: Context, trip: SavedTrip) = persist(context, all(context).map { if (it.id == trip.id) trip else it })
    fun delete(context: Context, id: String) = persist(context, all(context).filter { it.id != id })

    private fun persist(context: Context, trips: List<SavedTrip>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY, trips.joinToString("\n") { t ->
                listOf(t.id, t.name, t.date, t.meta, t.done.toString(), t.destination, t.budget.toString(), t.spotKeys).joinToString("|")
            }).apply()
    }
}
