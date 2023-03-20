class PlayxAnimation {
  /// Animation Index of the Animation to be used.
  int? index;

  /// Animation Name of the Animation to be used.
  String? name;

  /// auto play : decides whether to play the animation automatically or not
  /// default is true.
  bool autoPlay;

  PlayxAnimation.byIndex(this.index, {this.autoPlay = true});

  PlayxAnimation.byName(this.name, {this.autoPlay = true});

  Map<String, dynamic> toJson() => {
        'index': index,
        'name': name,
        'autoPlay': autoPlay,
      };
}
