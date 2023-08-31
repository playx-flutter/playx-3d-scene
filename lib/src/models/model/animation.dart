/// An object representing what animation to be played for current model.
class PlayxAnimation {
  /// The Index of the Animation to be used.
  int? index;

  /// The Name of the Animation to be used.
  String? name;

  /// Decides whether to play the animation automatically or not.
  /// Default is true.
  bool autoPlay;

  /// creates animation object by index to be played.
  PlayxAnimation.byIndex(this.index, {this.autoPlay = true});

  /// creates animation object by name to be played.
  PlayxAnimation.byName(this.name, {this.autoPlay = true});

  Map<String, dynamic> toJson() => {
        'index': index,
        'name': name,
        'autoPlay': autoPlay,
      };
}
