package com.smartlakbay.app.models

import android.content.Context

data class CommunityReview(
    val id: String,
    val author: String,
    val tripName: String,
    val text: String,
    val date: String,
    val rating: Int = 5,
    val mediaType: String? = null, // "photo" or "video"
    val mediaResName: String? = null,
    val comments: MutableList<CommunityComment> = mutableListOf()
)

data class CommunityComment(
    val id: String,
    val author: String,
    val text: String
)

object CommunityStore {
    private const val PREFS = "smart_lakbay_community_reviews"
    private const val KEY = "reviews_v2"

    fun all(context: Context): List<CommunityReview> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: ""
        if (raw.isEmpty()) {
            val seeds = listOf(
                CommunityReview(
                    "rev-1", "Maria Santos", "Taal Heritage Trip",
                    "Amazing Taal Heritage trip! The walking tour of Taal Basilica and stopping at Café de Lipa was unforgettable.",
                    "Oct 28, 2026",
                    5,
                    "photo",
                    "taal.jpg",
                    mutableListOf(CommunityComment("com-1", "John Ray", "Totally agree! Did you try their local lomi too?"))
                ),
                CommunityReview(
                    "rev-2", "Carlos Mendez", "Nasugbu Beach Weekend",
                    "Breathtaking sunset views at Nasugbu! Crystal clear waters and very relaxing weekend getaway.",
                    "Nov 2, 2026",
                    5,
                    "photo",
                    "nasugbu.jpg",
                    mutableListOf()
                )
            )
            persist(context, seeds)
            return seeds
        }
        return raw.split("§§§").filter { it.isNotBlank() }.map { block ->
            val lines = block.split("\n")
            val id = lines.getOrNull(0) ?: ""
            val author = lines.getOrNull(1) ?: ""
            val trip = lines.getOrNull(2) ?: ""
            val date = lines.getOrNull(3) ?: ""
            val text = lines.getOrNull(4) ?: ""
            val rating = lines.getOrNull(5)?.toIntOrNull() ?: 5
            val mediaType = lines.getOrNull(6)?.takeIf { it.isNotBlank() }
            val mediaResName = lines.getOrNull(7)?.takeIf { it.isNotBlank() }
            val commentsRaw = lines.getOrNull(8) ?: ""
            val comments = commentsRaw.split("|||").filter { it.isNotBlank() }.map { c ->
                val cp = c.split("::: ")
                CommunityComment(cp.getOrNull(0) ?: "", cp.getOrNull(1) ?: "", cp.getOrNull(2) ?: "")
            }.toMutableList()
            CommunityReview(id, author, trip, text, date, rating, mediaType, mediaResName, comments)
        }
    }

    fun addReview(context: Context, review: CommunityReview) {
        val list = all(context) + review
        persist(context, list)
    }

    fun addComment(context: Context, reviewId: String, comment: CommunityComment) {
        val list = all(context).map { r ->
            if (r.id == reviewId) {
                r.comments.add(comment)
            }
            r
        }
        persist(context, list)
    }

    private fun persist(context: Context, list: List<CommunityReview>) {
        val encoded = list.joinToString("§§§") { r ->
            val coms = r.comments.joinToString("|||") { "${it.id}::: ${it.author}::: ${it.text}" }
            listOf(
                r.id,
                r.author,
                r.tripName,
                r.date,
                r.text,
                r.rating.toString(),
                r.mediaType ?: "",
                r.mediaResName ?: "",
                coms
            ).joinToString("\n")
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY, encoded)
            .apply()
    }
}
