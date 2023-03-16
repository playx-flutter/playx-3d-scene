import 'package:playx_3d_scene/models/scene/material/texture/enums/texture_type.dart';
import 'package:playx_3d_scene/models/scene/material/texture/texture_sampler.dart';

class PlayxTexture {
  String? assetPath;
  String? url;
  TextureType? type;
  PlayxTextureSampler? sampler;

  PlayxTexture.asset(this.assetPath, {this.type, this.sampler});

  PlayxTexture.url(this.url, {this.type, this.sampler});

  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'type': type?.toName(),
        'sampler': sampler?.toJson(),
      };
}
