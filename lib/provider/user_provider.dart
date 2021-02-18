import 'package:flutter/material.dart';
import 'package:skype_clone/resources/auth_methods.dart';
import 'package:skype_clone/models/user.dart' as model;

class UserProvider extends ChangeNotifier {
  model.User _user;
  AuthMethods _authMethods = AuthMethods();

  model.User get getUser => _user;

  void refreshUser() async {
    model.User user = await _authMethods.getUserDetails();
    _user = user;
    notifyListeners();
  }
}
