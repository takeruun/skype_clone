import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'package:image/image.dart';
import 'package:skype_clone/enum/user_state.dart';
import 'package:skype_clone/models/user.dart' as model;
import 'package:skype_clone/resources/auth_methods.dart';
import 'package:skype_clone/utils/utilities.dart';

class OnlineDotIndicator extends StatelessWidget {
  final String uid;
  final AuthMethods _authMethods = AuthMethods();

  OnlineDotIndicator({@required this.uid});

  @override
  Widget build(BuildContext context) {
    getColor(int state) {
      switch (Utils.numToState(state)) {
        case UserState.Offline:
          return Colors.red;

        case UserState.Online:
          return Colors.green;

        default:
          return Colors.orange;
      }
    }

    return StreamBuilder<DocumentSnapshot>(
      stream: _authMethods.getUserStream(uid: uid),
      builder: (context, snapshot) {
        model.User user;
        if (snapshot.hasData && snapshot.data.data() != null) {
          user = model.User.fromMap(snapshot.data.data());
        }

        return Container(
          height: 10,
          width: 10,
          margin: EdgeInsets.only(right: 5, top: 5),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: getColor(user?.state),
          ),
        );
      },
    );
  }
}