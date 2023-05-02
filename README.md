# Playx 3d Scene

Plugin for rendering interactive 3D models based on [Google filament](https://github.com/google/filament) natively on Android.  
With the ability of controlling environment skybox, light, camera, ground and more.



https://user-images.githubusercontent.com/19964129/227077898-ac46c4b9-3668-4c2d-bbea-933b1ce272fb.mp4



## Features


- Render 3d model from GLB of GLTF file from url or assets.
- change model animation by index or by name.
- Control model scale and position.
- Render environment skybox from HDR image file or from filament ktx file or from color  
  or it can be transparent.
- Render indirect light  which is used to simulate environment lighting. it can be loaded from from HDR image file or from filament ktx file.
- Creates  a light source in the scene, such as a sun or street lights
- Have control over the camera with different modes like orbit and free flight mode with a lot of options.
- Create ground plane below the model with custom materials and textures.
- Create different shapes like cube, plane and sphere with custom materials and textures.


## Installation

In the `dependencies:` section of your `pubspec.yaml`, add the following line:

```yaml  
dependencies:  
  playx_3d_scene: ^0.0.1  
```  

## Usage
Basic usage to create 3d model from glb file with skybox and indirect lighting from hdr image file.
```dart  
 Playx3dScene(
    model: GlbModel.asset("assets/models/Fox.glb",
      animation: PlayxAnimation.byIndex(0, autoPlay: true),
    ),
    scene: Scene(
      skybox: HdrSkybox.asset("assets/envs/courtyard.hdr"),
      indirectLight: HdrIndirectLight.asset("assets/envs/courtyard.hdr"),
    ),
    onCreated: (Playx3dSceneController controller) async {
      await controller.changeAnimationByIndex(1);
    },
    onModelStateChanged: (state) {
      setState(() {
        isModelLoading = state == ModelState.loading;
      });
    },
  );
```  

See the example app for more complex example.

## How To use

The widget conisists of 3 main components :

### Model :
There is 2 types of models that can be rendered :
#### Glb :
GLB is a binary container format of glTF. It bundles all the textures and mesh data into a single file.

To load GLB model file from assets we can use :
```dart  
Playx3dScene(  
   model: GlbModel.asset(    
      "assets/models/Fox.glb",  // load glb from assets path  
       animation: PlayxAnimation.byIndex(0, autoPlay: true),  //controls animation   
       fallback: GlbModel.asset("assets/models/Fox.glb"),  // fallback model if error happened  
       centerPosition: PlayxPosition(x: 0, y: 0, z: -4),  //center position of model   
       scale: 1.0,  // scale of model defaults to 1  
  ),  
),  
```  
To load GLB model file from url we can use :
```dart  
Playx3dScene(  
   model: GlbModel.url(    
    // load glb from direct url of glb file.  
    "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Fox/glTF-Binary/Fox.glb",   
     animation: PlayxAnimation.byName("walk", autoPlay: true),   //controls animation   
     fallback: GlbModel.asset("assets/models/Fox.glb"), // fallback model if error happened   
     centerPosition: PlayxPosition(x: 0, y: 0, z: -4), //center position of model    
     scale: 1.0,   // scale of model defaults to 1  
   ),  
),  
```  
<br>  

#### GLTF:
[glTF](https://en.wikipedia.org/wiki/GlTF) is a 3D file format maintained by the Khronos Group. It is an all-purpose transmission format.

- To load Gltf model file from assets we can use :
  ```dart  
   Playx3dScene(    
        model: GltfModel.asset(    
            "assets/models/BusterDrone.gltf",  /// path of the gltf file in assets  
            prefix: "assets/models/",  ///prefix for gltf images  
            animation: PlayxAnimation.byIndex(0, autoPlay: true),  //controls animation   
            fallback: GlbModel.asset("assets/models/Fox.glb"),  // fallback model if error happened   
            centerPosition: PlayxPosition(x: 0, y: 0, z: -4),  //center position of model    
            scale: 1.0,   // scale of model defaults to 1  
        ),  
    ),  
  ```  

_NOTE_ :

If the images path that in the gltf file different from the flutter asset path,    
consider adding [prefix] to the images path to be before the image.    
Or [postfix] to the images path to be after the image.    
For example if in the gltf file, the image path is textures/texture    
and in assets the image path is assets/models/textures/texture.png    
[prefix] must be 'assets/models/' and [postfix] should be '.png'.  
<br>

- To load Gltf model file from url we can use :  
  Currently we are supporting loading Gltf file as zip from url only.
  ```dart  
   Playx3dScene(  //same like loading from asssets  
       model: GltfModel.url(    
            " gltf url",  /// url of the gltf .zip file   
           prefix: "assets/models/",  ///prefix for gltf images   
       ),  
    ),  
 ```  

Now we can show our model with a transparent scene with default light.  
Next we will know how we can customize our scene.


### Scene:
Our scene consists of some components that allow us to customize how our 3d scene looks.

### Image-Based Light and Skybox

Filament supports rendering with  _image-based lighting_, or IBL. This uses an environment map to approximate the lighting all directions.

At run time, we need to create an  `IndirectLight` object  that contains a set of floating-point images. Together these images comprise all the mipmap levels and cubemap faces that make up the environment. In a sense these are not visible images; it is more accurate to them of them as data that can be used to approximate the indirect lighting in the scene.

On the other hand, the  `Skybox` that  _does_ contain visible images.  
Filament provides an offline tool called  `cmgen` that can consume an equirectangular image and produce indirect light and skybox files in one fell swoop,  as depicted below.

We support loading skybox and indirect light from both HDR Image or KTX file.  
![](https://miro.medium.com/v2/resize:fit:700/1*3A4eB8hGKhABSi7_k50Nug.png)


**1. Skybox:**  
We can load skybox from ktx file or hdr file or from color and defaults to transparent color.

- Loading skybox from Ktx file:
  - From assets:
  ```dart  
   scene: Scene(    
        skybox:KtxSkybox.asset(  
            // asset pasth for skybox ktx file  
            "assets/envs/venetian_crossroads_2k/venetian_crossroads_2k_skybox.ktx"
    ),   
    ```

  - From url:
  ```dart  
  scene: Scene(    
        skybox:KtxSkybox.url(  
            "url for skybox ktx file" // url for skybox ktx file  
    ),    
    ```  

 <br>  

- Loading skybox Form HDR file:
  - From assets:
  ```dart  
  scene: Scene(    
       skybox:HdrSkybox.asset(  
          "assets/envs/courtyard.hdr" // asset pasth for skybox hdr file  
   ),  
   ```

  - From url:
  ```dart  
   scene: Scene(    
       skybox:HdrSkybox.url(  
          "url for skybox hdr file" // url for skybox hdr file  
   ),    
  ```
  - Loading Skybox Form Color:
  ```dart  
  scene: Scene(    
        skybox: ColoredSkybox(color: Colors.green), // color of skybox   
        ), 
  ```

<br>  

**2. Indirect Light:**  
Indirect Light is used to simulate environment lighting, a form of global illumination.  
Filament supports rendering with image-based lighting, or IBL.    
This uses an environment map to approximate the lighting all directions.

We can load indirect light from KTX file or HDR file or from certain parameters.
- Loading indirect light from KTX file:
  - From assets:
    ```dart  
    scene: Scene(    
                 indirectLight:KtxIndirectLight.asset(  
                 // asset pasth for  indirect light ktx file  
                  "assets/envs/venetian_crossroads_2k/venetian_crossroads_2k_ibl.ktx" 
        ),  
    ```

  - From url:
     ```dart  
     scene: Scene(    
        indirectLight:KtxIndirectLight.url(  
            "url for indirect light ktx file" // url for indirect light ktx file  
      ),    
    ```  
- Loading indirect light form HDR file:
  - From assets:
     ```dart  
      scene: Scene(    
         indirectLight:HdrIndirectLight.asset(  
            "assets/envs/courtyard.hdr" // asset pasth for indirect light hdr file  
      ),  
    ```
  - From url:
     ```dart  
       scene: Scene(    
       indirectLight:HdrIndirectLight.url(  
           "url for skybox hdr file" // url for indirect light hdr file  
        ),    
    ```

  - Load indirect light from default parameters like intensity, irradiance, etc:

    ```dart  
     scene: Scene(    
          indirectLight: DefaultIndirectLight(  
               intensity : 30000, // indirect light intensity.  
               radianceBands: 1, // Number of spherical harmonics bands.  
               radianceSh: [1,1,1], // Array containing the spherical harmonics coefficients.  
               irradianceBands: 1, // Number of spherical harmonics bands.  
               irradianceSh: [1,1,1] // Array containing the spherical harmonics coefficients.  
           ),  
    ),  
    ```  
<br>  

**3. Light:**  
Light allows us to create a light source in the scene, such as a sun or street lights.  
Lights come in three flavors:    
directional lights   -  point lights   - spot lights
- Directional lights :  
  Directional lights have a direction, but don't have a position.    
  All light rays are parallel and come from infinitely far away and from everywhere.  
  Typically a directional light is used to simulate the sun.    
  Directional lights and spot lights are able to cast shadows.    
  To create a directional light use LightType.DIRECTIONAL or LightType.SUN, both are similar,    
  but the later also draws a sun's disk in the sky and its reflection on glossy objects.

- Point lights    
  Unlike directional lights, point lights have a position but emit light in all directions.    
  The intensity of the light diminishes with the inverse square of the distance to the light.

- Spot lights    
  Spot lights are similar to point lights but the light they emit is limited to a cone defined by spotLightCone and the light's direction.    
  A spot light is therefore defined by a position, a direction and inner and outer cones.    
  The spot light's influence is limited to inside the outer cone. The inner cone defines the light's falloff attenuation.    
  A physically correct spot light is a little difficult to use because changing the outer angle of the cone changes the illumination levels,    
  as the same amount of light is spread over a changing volume.    
  The coupling of illumination and the outer cone means that an artist cannot tweak the influence cone of a spot light without also changing the perceived illumination.    
  It therefore makes sense to provide artists with a parameter to disable this coupling.

Here is an exmaple how to add light to the scene:
```dart  
scene: Scene(    
   light: Light(    
      type: LightType.directional,    
      colorTemperature: 6500.0,    
      intensity: 100000.0,    
      direction: PlayxDirection(x: 0, y: -1, z: 0),    
      castShadows: true,    
     ),    
  ),  
```  
**4. Camera:**  
It help us have better control over the camera like what mode it operates on, target position, exposure and more.

There is 3 modes for the camera :  
Orbit mode - Free flight mode  - Map mode

Here is an example of how we can customize our camera:
```dart  
scene:  Scene(  
     camera: Camera.orbit(  //make camera operates on orbit mode   
        exposure: Exposure.formAperture(   
         //Sets this camera's exposure (default is f/16, 1/125s, 100 ISO)  
           aperture: 16.0,    
           shutterSpeed: 1 / 125,    
           sensitivity: 150,    
           ),    
        //Sets the world-space position of interest, which defaults to (x:0,y:0,z:-4).  
        targetPosition: PlayxPosition(x: 0.0, y: 0.0, z: -4.0),   
        // Sets The initial eye position in world space for ORBIT mode.    
        //This defaults to (x:0,y:0,z:1).  
        orbitHomePosition: PlayxPosition(x: 0.0, y: 1.0, z: 1.0),   
        //The orientation for the home position, which defaults to (x:0,y:1,z:0).  
        upVector: PlayxPosition(x: 0.0, y: 1.0, z: 0.0),    
     ),    
),  
```  
<br>  

**5. Ground:**  
The widget also support creating  ground plane to be drawn on the scene.  
With support for customizing ground plane material and textures.

We will talk in detail more about materials below.

Here is an example of how to create a ground :
```dart  
scene:Scene(  
   ground: Ground(    
        width: 4.0,  //width of the ground  
        height: 4.0,  //length of the ground  
         // determine whether the ground plane should be drawn below the model or not.   
         //if not provided we must provide center position of the ground.  
        isBelowModel: true,   
        normal: PlayxDirection.y(1.0),  //direction of the shape rotation in the world space  
        material: PlayxMaterial.asset(  // material of the ground  
           "assets/materials/textured_pbr.filamat",    
           parameters: [    
              MaterialParameter.texture(    
                 value: PlayxTexture.asset(    
                    "assets/materials/texture/floor_basecolor.png",    
                      type: TextureType.color,    
                       sampler: PlayxTextureSampler(anisotropy: 8),    
                    ),    
                 name: "baseColor",    
             ),    
             MaterialParameter.texture(    
              value: PlayxTexture.asset(    
                 "assets/materials/texture/floor_normal.png",    
                    type: TextureType.normal,    
                    sampler: PlayxTextureSampler(anisotropy: 8),    
                    ),    
                 name: "normal",    
              ),    
              MaterialParameter.texture(    
                 value: PlayxTexture.asset(    
                    "assets/materials/texture/floor_ao_roughness_metallic.png",    
                    type: TextureType.data,    
                    sampler: PlayxTextureSampler(anisotropy: 8),    
                    ),    
                 name: "aoRoughnessMetallic",    
            ),    
           ],    
        ),    
      ),  
),  
```  


### Shapes:
The widget also supports rendering different shapes like cube, plane and sphere.  
with support for customizing it's material and textures.  
![enter image description here](https://dl.dropbox.com/s/il6ia7901clzom8/2.jpg)  
Here is an example of how to create sphere and cube:

```dart  
       shapes: [  
              Cube(  
                  id: 1, // id of the shape used to update the shapes  
                  length: .5, // length of the cube  
                  centerPosition: PlayxPosition(  
                      x: -1, y: 0, z: -4), // center position of the cube  
                  material: PlayxMaterial.asset("assets/materials/lit.filamat",  //material of the cube  
                      parameters: [  
                        MaterialParameter.color(color: Colors.green, name: "baseColor"),  
                        MaterialParameter.float(value: .8, name: "roughness"),  
                        MaterialParameter.float(value: .8, name: "metallic"),  
                      ],  
                  ),  
                ),  
                Sphere(      //creates a sphere  
                  id: 2,   // id of the shape used to update the shapes  
                  centerPosition: PlayxPosition(x: 1, y: 0, z: -4),   //center position of the sphere  
                  radius: .5,      //radius of the sphere   
                  material: PlayxMaterial.asset(  // material of the sphere  
                    "assets/materials/textured_pbr.filamat",  
                    parameters: [  
                      MaterialParameter.texture(  
                        value: PlayxTexture.asset(  
                          "assets/materials/texture/floor_basecolor.png",  
                          type: TextureType.color,  
                          sampler: PlayxTextureSampler(anisotropy: 8),  
                        ),  
                        name: "baseColor",  
                      ),  
                      MaterialParameter.texture(  
                        value: PlayxTexture.asset(  
                          "assets/materials/texture/floor_normal.png",  
                          type: TextureType.normal,  
                          sampler: PlayxTextureSampler(anisotropy: 8),  
                        ),  
                        name: "normal",  
                      ),  
                      MaterialParameter.texture(  
                        value: PlayxTexture.asset(  
                          "assets/materials/texture/floor_ao_roughness_metallic.png",  
                          type: TextureType.data,  
                          sampler: PlayxTextureSampler(anisotropy: 8),  
                        ),  
                        name: "aoRoughnessMetallic",  
                      ),  
                    ],  
                  ),  
                )  
              ],                 
```  
<br>  


## Materials:
Filament is a physically based rendering (PBR) engine for Android. Filament offers a customizable material system that you can use to create both simple and complex materials.

A material defines the visual appearance of a surface. To completely describe and render a surface, a material provides the following information:
- Material model
- Set of use-controllable named parameters
- Raster state (blending mode, backface culling, etc.)
- Vertex shader code
- Fragment shader code  
  <br>

### Material model

Also called  _shading model_ or  _lighting model_, the material model defines the intrinsic properties of a surface. These properties have a direct influence on the way lighting is computed and therefore on the appearance of a surface.

Filament materials can use one of the following material models:

- Lit (or standard)
- Subsurface
- Cloth
- Unlit
- Specular glossiness (legacy)

For example for Lit (or standard) model :

This material model can be used to describe many non-metallic surfaces (_dielectrics_) or metallic surfaces (_conductors_).  
The appearance of a material using the standard model is controlled using properties for example:

**baseColor** : Diffuse albedo for non-metallic surfaces, and specular color for metallic surfaces

**metallic** :Whether a surface appears to be dielectric (0.0) or conductor (1.0). Often used as a binary value (0 or 1)

**roughness** Perceived smoothness (1.0) or roughness (0.0) of a surface. Smooth surfaces exhibit sharp reflections

**reflectance** Fresnel reflectance at normal incidence for dielectric surfaces. This directly controls the strength of the reflections

**sheenColor** Strength of the sheen layer

Each property can be different  from each other.

For more information about material and how to create the material file look at the material [documentation](https://google.github.io/filament/Materials.html) and [samples](https://github.com/google/filament/tree/main/samples/materials).



### Compiling materials
To use material in our widget to customize the appearance of shapes and ground.    
You will be required to create a material file (.mat file) then we will need to convert it to (.filamt) file to be able to use it with the widget using the material compiler  `matc` .

Material packages can be compiled from material definitions using the command line tool called  `matc`.  
You can get it by downloading the binary package for your host platform of choice from Filament’s  [releases page](https://github.com/google/filament/releases) on GitHub. Be sure to choose a version that matches the The filament version we are using which is V1.32.1.

The simplest way to use  `matc` is to specify an input material definition (`lit.mat` in the example below) and an output material package (`lit.filamat` in the example below):


``` matc -p mobile -a opengl -o app/src/main/assets/lit.filamat app/src/materials/lit.mat    
```   

### How to use materials:
After we have created our material files and compiled it using  material compiler  `matc`.  
We can use it to customize the visual appearance of shapes and ground like giving ground wood texture.

Here is an example of how to give cube lit material and customize it's color, metallic and roughnesss.

```dart  
  Cube(
    //materials can be created from assets or url file
    //by providing path of the compiled material file
    material: PlayxMaterial.asset(
      "assets/materials/lit.filamat",
     //usually the material file contains values for these properties, 
	 //but if we want to customize it we can like that.
      parameters: [
        //update base color property with color
        MaterialParameter.color(color: Colors.green, name: "baseColor"),
        //update roughness property with it's value
        MaterialParameter.float(value: .8, name: "roughness"),
        //update metallicproperty with it's value
        MaterialParameter.float(value: .8, name: "metallic"),
      ],
    ),
  );
```  
And just like that we have given our cube shape a material with color and customizing it's metallic and roughness properties.

The widget also supports customizing our shapes material by textures.  
It can load textures from assets or url and apply it to any shape.

Here is an example of giving a sphere a wood texture :
```dart               
                Sphere(         
              //Asset path of the compiled material file   
                  material: PlayxMaterial.asset(  
                    "assets/materials/textured_pbr.filamat",  
                    parameters: [  
                      MaterialParameter.texture(  
                      //creates texture from asset for texture the material type must be sampler2d   
                        value: PlayxTexture.asset(  
                          "assets/materials/texture/floor_basecolor.png",  
                          //type of texture  
                          type: TextureType.color,  
                          //sampler defines how a texture is accessed.  
                          //can be left to default  
                          sampler: PlayxTextureSampler(anisotropy: 8),  
                        ),  
                        //baseColor property name in the material file  
                        name: "baseColor",  
                      ),  
                      //creates texture from asset and apply it to normal property   
                      MaterialParameter.texture(  
                        value: PlayxTexture.asset(  
                          "assets/materials/texture/floor_normal.png",  
                          type: TextureType.normal,  
                          sampler: PlayxTextureSampler(anisotropy: 8),  
                        ),  
                        name: "normal",  
                      ),  
                      //creates texture from asset and apply it to aoRoughnessMetallic property   
                      MaterialParameter.texture(  
                        value: PlayxTexture.asset(  
                          "assets/materials/texture/floor_ao_roughness_metallic.png",  
                          type: TextureType.data,  
                          sampler: PlayxTextureSampler(anisotropy: 8),  
                        ),  
                        name: "aoRoughnessMetallic",  
                      ),  
                    ],  
                  ),  
                )  
              ],  
             
```  

<br>  


## Addendum: Creating the KTX Files

To make your own KTX file , you can obtain  `cmgen` by downloading the binary package for your host platform of choice from Filament’s  [releases page](https://github.com/google/filament/releases) on GitHub. Be sure to choose a version that matches the The filament version we are using which is V1.32.1.

To generate both a Skybox and an IBL, invoke  `cmgen` using a command line like this:
```  
cmgen                   \    
    --deploy ./myOutDir \    
    --format=ktx        \    
    --size=256          \    
    --extract-blur=0.1  \    
    mySrcEnv.hdr  
 ```  

The  `extract-blur` option tells  `cmgen` to make a skybox in addition to the IBL. To see the complete list of options, try  `cmgen -h`.

<br>  


## Documentation && References

- [Filament](https://google.github.io/filament/Filament.html), an in-depth explanation of real-time physically based rendering, the graphics capabilities and implementation of Filament. This document explains the math and reasoning behind most of our decisions. This document is a good introduction to PBR for graphics programmers.
- [Materials](https://google.github.io/filament/Materials.html), the full reference documentation for our material system. This document explains our different material models, how to use the material compiler  `matc` and how to write custom materials.
- [Material Properties](https://google.github.io/filament/Material%20Properties.pdf), a reference sheet for the standard material model..
- [Filament sample Android apps](https://github.com/google/filament/tree/main/android/samples),This directory contains several sample Android applications that demonstrate how to use the Filament APIs:
- [Getting Started with Filament on Android](https://medium.com/@philiprideout/getting-started-with-filament-on-android-d10b16f0ec67) : by   Philip Rideout