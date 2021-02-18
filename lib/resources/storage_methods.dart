import 'dart:io';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/widgets.dart';
import 'package:skype_clone/models/user.dart' as model;
import 'package:skype_clone/provider/image_upload_provider.dart';
import 'package:skype_clone/resources/chat_methods.dart';

class StorageMethods {
  static final FirebaseFirestore firestore = FirebaseFirestore.instance;

  Reference _reference;

  model.User user = model.User();

  Future<String> uploadImageToStorage(File imageFile) async {
    try {
      _reference = FirebaseStorage.instance
          .ref()
          .child('${DateTime.now().millisecondsSinceEpoch}');

      UploadTask _uploadTask = _reference.putFile(imageFile);

      var url = await (await _uploadTask).ref.getDownloadURL();

      return url;
    } catch (e) {
      print(e);
      return null;
    }
  }

  void uploadImage({
    @required File image,
    @required String receiverId,
    @required String senderId,
    @required ImageUploadProvider imageUploadProvider,
  }) async {
    final ChatMethods chatMethods = ChatMethods();

    // Set some loading value to db and show it to user
    imageUploadProvider.setToLoading();

    // GET url from the image bucket
    String url = await uploadImageToStorage(image);

    // Hide loading
    imageUploadProvider.setToIdle();

    // Uploading image message to db
    chatMethods.setImageMsg(url, receiverId, senderId);
  }
}
