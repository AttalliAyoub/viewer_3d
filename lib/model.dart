part of viewer3d;

class Model {
  final String path;
  final String? texture;

  Model({
    required this.path,
    this.texture,
  });

  Map<String, dynamic> get json => {
        'path': path,
        'texture': texture,
      };
}
