class PlayxAnimation {
  /// Animation Index of the Animation to be used.
  int? index;

  /// Animation Name of the Animation to be used.
  String? name;

  /// auto play : decides whether to play the animation automatically or not
  /// default is false.
  bool autoPlay;
  Duration? duration;

  PlayxAnimation._(
      {this.index, this.name, this.autoPlay = false, this.duration});

  factory PlayxAnimation.byIndex(int index,
      {bool autoPlay = false, Duration? duration}) {
    return PlayxAnimation._(
        index: index, autoPlay: autoPlay, duration: duration);
  }

  factory PlayxAnimation.byName(String? name,
      {bool autoPlay = false, Duration? duration}) {
    return PlayxAnimation._(name: name, autoPlay: autoPlay, duration: duration);
  }

  Map<String, dynamic> toJson() => {
        'index': index,
        'name': name,
        'autoPlay': autoPlay,
        'duration': duration,
      };
}
