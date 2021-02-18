import 'package:cloud_firestore/cloud_firestore.dart';

class Message {
  String senderId;
  String receiverId;
  String type;
  String message;
  Timestamp timestamp;
  String photoUrl;

  Message({
    this.senderId,
    this.receiverId,
    this.type,
    this.message,
    this.timestamp,
  });

  Message.imageMessage({
    this.senderId,
    this.receiverId,
    this.type,
    this.message,
    this.timestamp,
    this.photoUrl,
  });

  Map<String, dynamic> toMap() => {
        'senderId': senderId,
        'receiverId': receiverId,
        'type': type,
        'message': message,
        'timestamp': timestamp,
      };

  Map<String, dynamic> toImageMap() => {
        'senderId': senderId,
        'receiverId': receiverId,
        'type': type,
        'message': message,
        'photoUrl': photoUrl,
        'timestamp': timestamp,
      };

  Message.fromMap(Map<String, dynamic> map) {
    this.senderId = map['senderId'];
    this.receiverId = map['receiverId'];
    this.type = map['type'];
    this.message = map['message'];
    this.timestamp = map['timestamp'];
    this.photoUrl = map['photoUrl'];
  }
}
