import 'package:flutter_test/flutter_test.dart';
import 'package:playx_model_viewer/channel/playx_model_viewer_method_channel.dart';
import 'package:playx_model_viewer/channel/playx_model_viewer_platform_interface.dart';
import 'package:playx_model_viewer/view/playx_model_viewer.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPlayxModelViewerPlatform
    with MockPlatformInterfaceMixin
    implements PlayxModelViewerPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PlayxModelViewerPlatform initialPlatform =
      PlayxModelViewerPlatform.instance;

  test('$MethodChannelPlayxModelViewer is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPlayxModelViewer>());
  });

  test('getPlatformVersion', () async {
    PlayxModelViewer playxModelViewerPlugin = PlayxModelViewer();
    MockPlayxModelViewerPlatform fakePlatform = MockPlayxModelViewerPlatform();
    PlayxModelViewerPlatform.instance = fakePlatform;

    expect(await playxModelViewerPlugin.getPlatformVersion(), '42');
  });
}
