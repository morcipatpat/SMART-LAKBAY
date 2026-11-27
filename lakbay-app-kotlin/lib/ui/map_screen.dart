import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:go_router/go_router.dart';
import '../models/models.dart';

class MapScreen extends StatefulWidget {
  final double initialLat;
  final double initialLng;

  const MapScreen({
    super.key,
    this.initialLat = 13.9550,
    this.initialLng = 121.1600,
  });

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  late GoogleMapController mapController;
  late LatLng currentLatLng;
  String searchQuery = "";
  Spot? selectedSpot;
  bool hasPermission = false;

  @override
  void initState() {
    super.initState();
    currentLatLng = LatLng(widget.initialLat, widget.initialLng);
    _checkLocationPermission();
  }

  Future<void> _checkLocationPermission() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) return;

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) return;
    }

    if (permission == LocationPermission.deniedForever) return;

    setState(() => hasPermission = true);

    Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10,
      ),
    ).listen((Position position) {
      setState(() {
        currentLatLng = LatLng(position.latitude, position.longitude);
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    final filteredSpots = searchQuery.isEmpty
        ? SPOTS
        : SPOTS.where((s) =>
            s.name.toLowerCase().contains(searchQuery.toLowerCase()) ||
            s.desc.toLowerCase().contains(searchQuery.toLowerCase()) ||
            s.category.toLowerCase().contains(searchQuery.toLowerCase())).toList();

    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC),
      body: SafeArea(
        child: Column(
          children: [
            // Top Bar
            SurfaceHeader(
              hasPermission: hasPermission,
              currentLatLng: currentLatLng,
              onBack: () => context.go('/main'),
              onMyLocation: () {
                mapController.animateCamera(
                  CameraUpdate.newLatLngZoom(currentLatLng, 15),
                );
              },
              onSearchChanged: (v) => setState(() => searchQuery = v),
            ),
            // Map + List
            Expanded(
              child: ListView(
                padding: const EdgeInsets.all(12),
                children: [
                  const Text("🗺️ Batangas Google Map", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                  const SizedBox(height: 8),
                  SizedBox(
                    height: 300,
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(16),
                      child: GoogleMap(
                        initialCameraPosition: CameraPosition(
                          target: currentLatLng,
                          zoom: 14,
                        ),
                        myLocationEnabled: hasPermission,
                        myLocationButtonEnabled: true,
                        zoomControlsEnabled: true,
                        onMapCreated: (controller) => mapController = controller,
                        gestureRecognizers: {
                          Factory<PanGestureRecognizer>(() => PanGestureRecognizer()),
                          Factory<ScaleGestureRecognizer>(() => ScaleGestureRecognizer()),
                          Factory<TapGestureRecognizer>(() => TapGestureRecognizer()),
                          Factory<VerticalDragGestureRecognizer>(() => VerticalDragGestureRecognizer()),
                        },
                        markers: {
                          ...filteredSpots.map((spot) => Marker(
                                markerId: MarkerId(spot.key),
                                position: LatLng(spot.lat, spot.lng),
                                infoWindow: InfoWindow(
                                  title: "${spot.icon} ${spot.name}",
                                  snippet: "${spot.category} • ₱${spot.price} • ★${spot.rating}",
                                ),
                                onTap: () => setState(() => selectedSpot = spot),
                              )),
                          Marker(
                            markerId: const MarkerId("current_location"),
                            position: currentLatLng,
                            infoWindow: const InfoWindow(title: "📍 You are here", snippet: "Live GPS Location"),
                          ),
                        },
                      ),
                    ),
                  ),
                  const SizedBox(height: 12),
                  const Text("Nearby Establishments", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                  const SizedBox(height: 8),
                  ...filteredSpots.map((spot) {
                    final dist = calculateDistance(
                      currentLatLng.latitude,
                      currentLatLng.longitude,
                      spot.lat,
                      spot.lng,
                    );
                    return Card(
                      color: Colors.white,
                      elevation: 1,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      child: InkWell(
                        onTap: () {
                          setState(() => selectedSpot = spot);
                          mapController.animateCamera(
                            CameraUpdate.newLatLngZoom(LatLng(spot.lat, spot.lng), 16),
                          );
                        },
                        borderRadius: BorderRadius.circular(16),
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  Text(spot.icon, style: const TextStyle(fontSize: 24)),
                                  const SizedBox(width: 10),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(spot.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                                        Text("★ ${spot.rating} • $dist km away • ${spot.price == 0 ? "Est. FREE" : "Est. ₱${spot.price}"}", style: const TextStyle(fontSize: 12, color: Colors.grey)),
                                      ],
                                    ),
                                  ),
                                  ElevatedButton.icon(
                                    onPressed: () {
                                      final enc = Uri.encodeComponent(spot.name);
                                      context.go('/route/${spot.key}/$enc');
                                    },
                                    icon: const Icon(Icons.directions, size: 14, color: Colors.white),
                                    label: const Text("Directions", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.white)),
                                    style: ElevatedButton.styleFrom(
                                      backgroundColor: const Color(0xFF21C55E),
                                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 6),
                              Text(spot.desc, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                            ],
                          ),
                        ),
                      ),
                    );
                  }),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class SurfaceHeader extends StatelessWidget {
  final bool hasPermission;
  final LatLng currentLatLng;
  final VoidCallback onBack;
  final VoidCallback onMyLocation;
  final ValueChanged<String> onSearchChanged;

  const SurfaceHeader({
    super.key,
    required this.hasPermission,
    required this.currentLatLng,
    required this.onBack,
    required this.onMyLocation,
    required this.onSearchChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          children: [
            Row(
              children: [
                IconButton(
                  icon: const Icon(Icons.arrow_back, color: Color(0xFF0A4FD6)),
                  onPressed: onBack,
                ),
                const Text("🌋 Smart ", style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF0A4FD6))),
                const Text("Lakbay Map", style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF21C55E))),
                const Spacer(),
                ElevatedButton.icon(
                  onPressed: onMyLocation,
                  icon: const Icon(Icons.my_location, size: 14, color: Colors.white),
                  label: const Text("My Location", style: TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold)),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0A4FD6),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(50)),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              decoration: BoxDecoration(
                color: const Color(0xFFEFF6FF),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  Container(
                    width: 8,
                    height: 8,
                    decoration: const BoxDecoration(color: Color(0xFF21C55E), shape: BoxShape.circle),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    "Using Live GPS • ${currentLatLng.latitude.toStringAsFixed(4)}, ${currentLatLng.longitude.toStringAsFixed(4)}",
                    style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF0A4FD6)),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              onChanged: onSearchChanged,
              decoration: InputDecoration(
                hintText: "Search nearby Batangas stores & establishments...",
                prefixIcon: const Icon(Icons.search),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                  borderSide: const BorderSide(color: Color(0xFFC7D0E0)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
