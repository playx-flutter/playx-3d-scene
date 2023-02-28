import 'package:flutter/services.dart';

class PlayXModelViewerController {
  int id;
  late MethodChannel _channel;

  static const String channelName = "io.sourcya.playx.model_viewer.channel";

  PlayXModelViewerController({required this.id}) {
    _channel = MethodChannel('${channelName}_$id');
  }

  Future<void> setText(String text) async {
    return _channel.invokeMethod('setText', text);
  }
}
