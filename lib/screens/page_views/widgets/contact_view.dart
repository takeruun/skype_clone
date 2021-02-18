import 'package:flutter/material.dart';
import 'package:skype_clone/models/contact.dart';
import 'package:skype_clone/models/user.dart';
import 'package:skype_clone/resources/auth_methods.dart';
import 'package:skype_clone/resources/call_methods.dart';
import 'package:skype_clone/utils/universal_variables.dart';
import 'package:skype_clone/widgets/custom_tile.dart';

class ContactView extends StatelessWidget {
  final Contact contact;
  final AuthMethods _authMethods = AuthMethods();

  ContactView(this.contact);

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<User>(builder: null);
  }
}

class ViewLayout extends StatelessWidget {
  final User contact;
  final CallMethods _callMethods = CallMethods();

  ViewLayout({@required this.contact});

  @override
  Widget build(BuildContext context) {
    return CustomTile(
      mini: false,
      onTap: () {},
      title: Text(
        "TK",
        style:
            TextStyle(color: Colors.white, fontFamily: "Arial", fontSize: 19),
      ),
      subtitle: Text(
        "Hello",
        style: TextStyle(
          color: UniversalVariables.greyColor,
          fontSize: 14,
        ),
      ),
      leading: Container(
        constraints: BoxConstraints(maxHeight: 60, maxWidth: 60),
        child: Stack(
          children: <Widget>[
            CircleAvatar(
              maxRadius: 30,
              backgroundColor: Colors.grey,
              backgroundImage: NetworkImage(
                  "https://yt3.ggpht.com/a/AGF-l7_zT8BuWwHTymaQaBptCy7WrsOD72gYGp-puw=s900-c-k-c0xffffffff-no-rj-mo"),
            ),
            Align(
              alignment: Alignment.bottomRight,
              child: Container(
                height: 13,
                width: 13,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: UniversalVariables.onlineDotColor,
                  border: Border.all(
                      color: UniversalVariables.blackColor, width: 2),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
