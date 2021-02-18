class User {
  String uid;
  String name;
  String email;
  String username;
  String status;
  int state;
  String profilePhoto;

  User({
    this.uid,
    this.name,
    this.email,
    this.username,
    this.status,
    this.state,
    this.profilePhoto,
  });

  Map<String, dynamic> toMap() => {
        'uid': uid,
        'name': name,
        'email': email,
        'username': username,
        'status': status,
        'state': state,
        'profile_photo': profilePhoto,
      };

  factory User.fromMap(Map<String, dynamic> mapData) => User(
        uid: mapData['uid'],
        name: mapData['name'],
        email: mapData['email'],
        username: mapData['username'],
        status: mapData['status'],
        state: mapData['state'],
        profilePhoto: mapData['profile_photo'],
      );
}
