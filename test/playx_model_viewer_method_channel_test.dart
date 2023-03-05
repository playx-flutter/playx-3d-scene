import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:playx_model_viewer/channel/playx_model_viewer_method_channel.dart';

void main() {
  MethodChannelPlayxModelViewer platform = MethodChannelPlayxModelViewer();
  const MethodChannel channel = MethodChannel('playx_model_viewer');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
