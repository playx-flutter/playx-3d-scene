///Wrapper class to hold data on success or message on failure
class Result<T> {
  T? data;
  String? message;

  Result._({this.data, this.message});

  factory Result.success(T? data) {
    return Result._(data: data);
  }

  factory Result.error(String? message) {
    return Result._(message: message);
  }

  bool isSuccess() => data != null;
}
