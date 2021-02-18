import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:skype_clone/models/user.dart' as model;

class Contact {
  String uid;
  Timestamp addedOn;

  Contact({
    this.uid,
    this.addedOn,
  });

  Map<String, dynamic> toMap() => {
        'uid': uid,
        'added_on': addedOn,
      };

  factory Contact.fromMap(Map<String, dynamic> mapData) => Contact(
        uid: mapData['uid'],
        addedOn: mapData['added_on'],
      );
}
