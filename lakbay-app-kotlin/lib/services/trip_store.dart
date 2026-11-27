import 'package:shared_preferences/shared_preferences.dart';
import '../models/models.dart';

class TripStore {
  static const String _key = "smart_lakbay_trips_list";

  static Future<List<SavedTrip>> all() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_key);
    if (raw == null || raw.isEmpty) {
      final seeds = [
        SavedTrip(
          id: "1",
          name: "Taal Heritage Trip",
          date: "2026-11-08",
          meta: "Nov 8, 2026 • 4 Places Included",
          done: false,
          destination: "Taal Heritage Town",
          budget: 3500,
          spotKeys: "marianOrchard,outletsLima,srLipa,smLipa",
        ),
        SavedTrip(
          id: "2",
          name: "Nasugbu Beach Weekend",
          date: "2026-11-15",
          meta: "Rated: ⭐⭐⭐⭐⭐",
          done: true,
          destination: "Nasugbu",
          budget: 3500,
          spotKeys: "peoplesPark,twinLakesTagaytay",
        ),
      ];
      await _persist(seeds);
      return seeds;
    }

    return raw
        .split("\n")
        .where((line) => line.trim().isNotEmpty)
        .map((line) {
          final p = line.split("|");
          if (p.length < 8) return null;
          return SavedTrip(
            id: p[0],
            name: p[1],
            date: p[2],
            meta: p[3],
            done: p[4] == "true",
            destination: p[5],
            budget: int.tryParse(p[6]) ?? 0,
            spotKeys: p[7],
          );
        })
        .whereType<SavedTrip>()
        .toList();
  }

  static Future<void> add(SavedTrip trip) async {
    final list = await all();
    await _persist([...list, trip]);
  }

  static Future<void> update(SavedTrip trip) async {
    final list = await all();
    await _persist(list.map((it) => it.id == trip.id ? trip : it).toList());
  }

  static Future<void> delete(String id) async {
    final list = await all();
    await _persist(list.where((it) => it.id != id).toList());
  }

  static Future<void> _persist(List<SavedTrip> trips) async {
    final prefs = await SharedPreferences.getInstance();
    final encoded = trips.map((t) => [
      t.id,
      t.name,
      t.date,
      t.meta,
      t.done.toString(),
      t.destination,
      t.budget.toString(),
      t.spotKeys,
    ].join("|")).join("\n");
    await prefs.setString(_key, encoded);
  }
}
