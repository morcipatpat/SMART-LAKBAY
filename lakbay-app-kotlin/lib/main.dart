import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'ui/intro_screen.dart';
import 'ui/login_screen.dart';
import 'ui/main_screen.dart';
import 'ui/chat_screen.dart';
import 'ui/customizer_screen.dart';
import 'ui/itinerary_screen.dart';
import 'ui/route_screen.dart';
import 'ui/map_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const SmartLakbayApp());
}

final GoRouter router = GoRouter(
  initialLocation: '/intro',
  routes: [
    GoRoute(path: '/intro', builder: (context, state) => const IntroScreen()),
    GoRoute(path: '/login', builder: (context, state) => const LoginScreen()),
    GoRoute(path: '/main', builder: (context, state) => const MainScreen()),
    GoRoute(
      path: '/customizer/:destination/:budget',
      builder: (context, state) {
        final dest = state.pathParameters['destination'] ?? 'Taal Heritage Town';
        final bud = int.tryParse(state.pathParameters['budget'] ?? '') ?? 3500;
        return TripCustomizerScreen(destination: dest, budget: bud);
      },
    ),
    GoRoute(
      path: '/itinerary/:destination/:budget/:spotKeys',
      builder: (context, state) {
        final dest = state.pathParameters['destination'] ?? 'Taal Heritage Town';
        final bud = int.tryParse(state.pathParameters['budget'] ?? '') ?? 3500;
        final keys = state.pathParameters['spotKeys'] ?? '';
        return ItineraryScreen(destination: dest, budget: bud, spotKeys: keys);
      },
    ),
    GoRoute(
      path: '/route/:spotKeys/:tripLabel',
      builder: (context, state) {
        final keys = state.pathParameters['spotKeys'] ?? '';
        final label = Uri.decodeComponent(state.pathParameters['tripLabel'] ?? 'Trip Route');
        return RouteScreen(spotKeys: keys, tripLabel: label);
      },
    ),
    GoRoute(
      path: '/map',
      builder: (context, state) =>
          const MapScreen(initialLat: 13.9550, initialLng: 121.1600),
    ),
  ],
);

class SmartLakbayApp extends StatelessWidget {
  const SmartLakbayApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'SmartLakbay',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF0A4FD6)),
      ),
      routerConfig: router,
    );
  }
}
