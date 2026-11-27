import 'package:shared_preferences/shared_preferences.dart';
import '../models/models.dart';

class CommunityStore {
  static const String _key = "smart_lakbay_community_reviews_v2";

  static Future<List<CommunityReview>> all() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_key) ?? "";
    if (raw.isEmpty) {
      final seeds = [
        CommunityReview(
          id: "rev-1",
          author: "Maria Santos",
          tripName: "Taal Heritage Trip",
          text: "Amazing Taal Heritage trip! The walking tour of Taal Basilica and stopping at Café de Lipa was unforgettable.",
          date: "Oct 28, 2026",
          rating: 5,
          mediaType: "photo",
          mediaResName: "taal.jpg",
          comments: [
            CommunityComment(
              id: "com-1",
              author: "John Ray",
              text: "Totally agree! Did you try their local lomi too?",
            )
          ],
        ),
        CommunityReview(
          id: "rev-2",
          author: "Carlos Mendez",
          tripName: "Nasugbu Beach Weekend",
          text: "Breathtaking sunset views at Nasugbu! Crystal clear waters and very relaxing weekend getaway.",
          date: "Nov 2, 2026",
          rating: 5,
          mediaType: "photo",
          mediaResName: "nasugbu.jpg",
        ),
      ];
      await _persist(seeds);
      return seeds;
    }

    return raw
        .split("§§§")
        .where((block) => block.trim().isNotEmpty)
        .map((block) {
          final lines = block.split("\n");
          final id = lines.isNotEmpty ? lines[0] : "";
          final author = lines.length > 1 ? lines[1] : "";
          final trip = lines.length > 2 ? lines[2] : "";
          final date = lines.length > 3 ? lines[3] : "";
          final text = lines.length > 4 ? lines[4] : "";
          final rating = lines.length > 5 ? int.tryParse(lines[5]) ?? 5 : 5;
          final mediaType = lines.length > 6 && lines[6].isNotEmpty ? lines[6] : null;
          final mediaResName = lines.length > 7 && lines[7].isNotEmpty ? lines[7] : null;

          final commentsRaw = lines.length > 8 ? lines[8] : "";
          final comments = commentsRaw
              .split("|||")
              .where((c) => c.trim().isNotEmpty)
              .map((c) {
            final cp = c.split("::: ");
            return CommunityComment(
              id: cp.isNotEmpty ? cp[0] : "",
              author: cp.length > 1 ? cp[1] : "",
              text: cp.length > 2 ? cp[2] : "",
            );
          }).toList();

          return CommunityReview(
            id: id,
            author: author,
            tripName: trip,
            text: text,
            date: date,
            rating: rating,
            mediaType: mediaType,
            mediaResName: mediaResName,
            comments: comments,
          );
        })
        .toList();
  }

  static Future<void> addReview(CommunityReview review) async {
    final list = await all();
    await _persist([...list, review]);
  }

  static Future<void> addComment(String reviewId, CommunityComment comment) async {
    final list = await all();
    final updated = list.map((r) {
      if (r.id == reviewId) {
        r.comments.add(comment);
      }
      return r;
    }).toList();
    await _persist(updated);
  }

  static Future<void> _persist(List<CommunityReview> list) async {
    final prefs = await SharedPreferences.getInstance();
    final encoded = list.map((r) {
      final coms = r.comments.map((c) => "${c.id}::: ${c.author}::: ${c.text}").join("|||");
      return [
        r.id,
        r.author,
        r.tripName,
        r.date,
        r.text,
        r.rating.toString(),
        r.mediaType ?? "",
        r.mediaResName ?? "",
        coms,
      ].join("\n");
    }).join("§§§");
    await prefs.setString(_key, encoded);
  }
}
