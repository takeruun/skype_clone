import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:image/image.dart' as Im;
import 'package:path_provider/path_provider.dart';
import 'package:skype_clone/enum/user_state.dart';

class Utils {
  static String getUsername(String email) {
    return "live:${email.split('@')[0]}";
  }

  static String getInitials(String name) {
    List<String> nameSplite = name.split(' ');
    String firstNameInitial = nameSplite[0][0];
    String lastNameInitial = nameSplite[1][0];

    return firstNameInitial + lastNameInitial;
  }

  static Future<File> pickImage({@required ImageSource source}) async {
    final picker = ImagePicker();
    PickedFile selectedImage = await picker.getImage(source: source);
    File file = File(selectedImage.path);
    return compressImage(file);
  }

  static Future<File> compressImage(File imageToCompress) async {
    final tempDir = await getTemporaryDirectory();
    final path = tempDir.path;
    int random = Random().nextInt(1000);

    Im.Image image = Im.decodeImage(imageToCompress.readAsBytesSync());
    Im.copyResize(image, width: 500, height: 500);

    return new File('$path/img_$random.jpg')
      ..writeAsBytesSync(Im.encodeJpg(image, quality: 85));
  }

  static int stateToNum(UserState userState) {
    switch (userState) {
      case UserState.Offline:
        return 0;

      case UserState.Online:
        return 1;

      default:
        return 2;
    }
  }

  static UserState numToState(int number) {
    switch (number) {
      case 0:
        return UserState.Offline;

      case 1:
        return UserState.Online;

      default:
        return UserState.Waiting;
    }
  }
}
