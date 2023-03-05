import 'package:flutter/material.dart';
import 'package:playx_model_viewer/view/playx_model_viewer.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Container(
            color: Colors.yellow,
            child: const PlayXModelViewer(
              glbAssetPath: "assets/models/Fox.glb",
              autoPlay: true,
              animationIndex: 0,
            ),
          ),
        ),
      ),
    );
  }
}
