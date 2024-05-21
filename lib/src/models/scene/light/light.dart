import 'dart:ui';

import 'package:playx_3d_scene/src/models/scene/geometry/direction.dart';
import 'package:playx_3d_scene/src/models/scene/geometry/position.dart';
import 'package:playx_3d_scene/src/models/scene/light/light_type.dart';
import 'package:playx_3d_scene/src/utils/utils.dart';

/// An object that allows you to create a light source in the scene, such as a sun or street lights.
/// Light types - Lights come in three flavors:
///
/// directional lights
/// point lights
/// spot lights
/// -Directional lights
/// Directional lights have a direction, but don't have a position.
/// All light rays are parallel and come from infinitely far away and from everywhere. Typically a directional light is used to simulate the sun.
/// Directional lights and spot lights are able to cast shadows.
/// To create a directional light use LightType.DIRECTIONAL or LightType.SUN, both are similar,
/// but the later also draws a sun's disk in the sky and its reflection on glossy objects.
/// -Point lights
/// Unlike directional lights, point lights have a position but emit light in all directions.
///The intensity of the light diminishes with the inverse square of the distance to the light.
/// -Spot lights
/// Spot lights are similar to point lights but the light they emit is limited to a cone defined by spotLightCone and the light's direction.
/// A spot light is therefore defined by a position, a direction and inner and outer cones.
/// The spot light's influence is limited to inside the outer cone. The inner cone defines the light's falloff attenuation.
/// A physically correct spot light is a little difficult to use because changing the outer angle of the cone changes the illumination levels,
/// as the same amount of light is spread over a changing volume.
/// The coupling of illumination and the outer cone means that an artist cannot tweak the influence cone of a spot light without also changing the perceived illumination.
/// It therefore makes sense to provide artists with a parameter to disable this coupling.
///
///Defaults to Directional light with  colorTemperature = 6_500.0, intensity = 100_000.0f,
// And direction = Direction(x:0.0, y:-1.0,z: 0.0), castShadows = true, cast light=false
class Light {
  ///Denotes the type of the light being created.
  LightType type;

  ///Sets the initial color of a light.
  /// The light color is specified in the linear sRGB color-space.
  /// The default is white.
  Color? color;

  /// instead of passing color directly, you can pass the temperature, in Kelvin.
  /// Converts a correlated color temperature to a  RGB color in sRGB space.
  /// The temperature must be expressed in Kelvin and must be in the range 1,000K to 15,000K.
  /// only one of temperature or color should be specified.
  double? colorTemperature;

  ///Sets the initial intensity of a light.
  /// This parameter depends on the LightType.
  /// For directional lights,it specifies the illuminance in lux (or lumen/m^2).
  /// For point lights and spot lights, it specifies the luminous power in lumen.
// For example, the sun's illuminance is about 100,000 lux.
  double? intensity;

  ///Sets the initial position of the light in world space.
  /// note: The Light's position is ignored for directional lights (LightManager.Type.DIRECTIONAL or LightManager.Type.SUN)
  /// Must be a list of 3 numbers:
  /// x – Light's position x coordinate in world space. The default is 0.
  /// y – Light's position y coordinate in world space. The default is 0.
  /// z – Light's position z coordinate in world space. The default is 0.
  PlayxPosition? position;

  ///Sets the initial direction of a light in world space.
  /// The light direction is specified in world space and should be a unit vector.
  /// note: The Light's direction is ignored for LightManager.Type.POINT lights.
  /// Must be a list of 3 numbers:
  // x – light's direction x coordinate (default is 0)
  // y – light's direction y coordinate (default is -1)
  // z – light's direction z coordinate (default is 0)
  PlayxDirection? direction;

  ///Whether this light casts light (enabled by default)
  // In some situations it can be useful to have a light in the scene
  // that doesn't actually emit light, but does cast shadows.
  bool? castLight;

  ///Enables or disables casting shadows from this Light.
  /// (disabled by default)
  bool? castShadows;

  ///Set the falloff distance for point lights and spot lights.
  /// At the falloff distance, the light has no more effect on objects.
  /// The falloff distance essentially defines a sphere of influence around the light,
  /// and therefore has an impact on performance.
  /// Larger falloffs might reduce performance significantly,especially when many lights are used.
  /// Try to avoid having a large number of light's spheres of influence overlap.
  /// The Light's falloff is ignored for directional lights (LightType.DIRECTIONAL or LightType.SUN)
  /// falloffRadius – Falloff distance in world units. Default is 1 meter.
  double? falloffRadius;

  ///Defines a spot light's angular falloff attenuation.
  /// A spot light is defined by a position, a direction and two cones, inner and outer.
  /// These two cones are used to define the angular falloff attenuation of the spot light
  /// and are defined by the angle from the center axis to where the falloff begins
  /// (i.e. cones are defined by their half-angle).
  /// note: The spot light cone is ignored for directional and point lights.
  ///inner cone angle in radian between 0 and pi/2 outer
  double? spotLightConeInner;

  ///Defines a spot light's angular falloff attenuation.
  /// A spot light is defined by a position, a direction and two cones, inner and outer.
  /// These two cones are used to define the angular falloff attenuation of the spot light
  /// and are defined by the angle from the center axis to where the falloff begins
  /// (i.e. cones are defined by their half-angle).
  /// note: The spot light cone is ignored for directional and point lights.
  ///outer cone angle in radian between inner and pi/2
  double? spotLightConeOuter;

  ///Defines the angular radius of the sun, in degrees, between 0.25° and 20.0°
  /// The Sun as seen from Earth has an angular size of 0.526° to 0.545°
  /// sunAngularRadius – sun's radius in degree. Default is 0.545°.
  double? sunAngularRadius;

  ///Defines the halo radius of the sun. The radius of the halo is defined as a multiplier of the sun angular radius.
  /// sunHaloSize – radius multiplier. Default is 10.0.
  double? sunHaloSize;

  ///Defines the halo falloff of the sun. The falloff is a dimensionless number used as an exponent.
  /// haloFalloff – halo falloff. Default is 80.0.
  double? sunHaloFalloff;

  Light(
      {this.type = LightType.directional,
      this.color,
      this.colorTemperature,
      this.intensity,
      this.position,
      this.direction,
      this.castLight,
      this.castShadows,
      this.falloffRadius,
      this.spotLightConeInner,
      this.spotLightConeOuter,
      this.sunAngularRadius,
      this.sunHaloSize,
      this.sunHaloFalloff});

  Map<String, dynamic> toJson() => {
        'type': LightType.toName(type),
        'color': color?.toHex(),
        'colorTemperature': colorTemperature,
        'intensity': intensity,
        'position': position?.toJson(),
        'direction': direction?.toJson(),
        'castLight': castLight,
        'castShadows': castShadows,
        'falloffRadius': falloffRadius,
        'spotLightConeInner': spotLightConeInner,
        'spotLightConeOuter': spotLightConeOuter,
        'sunAngularRadius': sunAngularRadius,
        'sunHaloSize': sunHaloSize,
        'sunHaloFalloff': sunHaloFalloff
      };

  @override
  String toString() {
    return 'Light(type: $type, color: $color, colorTemperature: $colorTemperature, intensity: $intensity, position: $position, direction: $direction, castLight: $castLight, castShadows: $castShadows, falloffRadius: $falloffRadius, spotLightConeInner: $spotLightConeInner, spotLightConeOuter: $spotLightConeOuter, sunAngularRadius: $sunAngularRadius, sunHaloSize: $sunHaloSize, sunHaloFalloff: $sunHaloFalloff)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is Light &&
        other.type == type &&
        other.color == color &&
        other.colorTemperature == colorTemperature &&
        other.intensity == intensity &&
        other.position == position &&
        other.direction == direction &&
        other.castLight == castLight &&
        other.castShadows == castShadows &&
        other.falloffRadius == falloffRadius &&
        other.spotLightConeInner == spotLightConeInner &&
        other.spotLightConeOuter == spotLightConeOuter &&
        other.sunAngularRadius == sunAngularRadius &&
        other.sunHaloSize == sunHaloSize &&
        other.sunHaloFalloff == sunHaloFalloff;
  }

  @override
  int get hashCode {
    return type.hashCode ^
        color.hashCode ^
        colorTemperature.hashCode ^
        intensity.hashCode ^
        position.hashCode ^
        direction.hashCode ^
        castLight.hashCode ^
        castShadows.hashCode ^
        falloffRadius.hashCode ^
        spotLightConeInner.hashCode ^
        spotLightConeOuter.hashCode ^
        sunAngularRadius.hashCode ^
        sunHaloSize.hashCode ^
        sunHaloFalloff.hashCode;
  }
}
