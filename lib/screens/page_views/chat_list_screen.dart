import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:skype_clone/models/contact.dart';
import 'package:skype_clone/provider/user_provider.dart';
import 'package:skype_clone/resources/auth_methods.dart';
import 'package:skype_clone/resources/chat_methods.dart';
import 'package:skype_clone/screens/page_views/widgets/contact_view.dart';
import 'package:skype_clone/screens/page_views/widgets/new_chat_button.dart';
import 'package:skype_clone/screens/page_views/widgets/quiet_box.dart';
import 'package:skype_clone/screens/page_views/widgets/user_circle.dart';
import 'package:skype_clone/widgets/custom_tile.dart';
import 'package:skype_clone/utils/universal_variables.dart';
import 'package:skype_clone/widgets/appbar.dart';

class ChatListScreen extends StatelessWidget {
  CustomAppBar customAppBar(BuildContext context) {
    return CustomAppBar(
      leading: IconButton(
        icon: Icon(
          Icons.notifications,
          color: Colors.white,
        ),
        onPressed: () {},
      ),
      title: UserCircle(),
      centerTitle: true,
      actions: <Widget>[
        IconButton(
          icon: Icon(
            Icons.search,
            color: Colors.white,
          ),
          onPressed: () {
            Navigator.pushNamed(context, '/search_screen');
          },
        ),
        IconButton(
          icon: Icon(
            Icons.more_vert,
            color: Colors.white,
          ),
          onPressed: () {},
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: UniversalVariables.blackColor,
      appBar: customAppBar(context),
      floatingActionButton: NewChatButton(),
      body: ChatListContainer(),
    );
  }
}

class ChatListContainer extends StatelessWidget {
  final ChatMethods _chatMethods = ChatMethods();

  @override
  Widget build(BuildContext context) {
    final UserProvider userProvider = Provider.of<UserProvider>(context);

    return Container(
      child: StreamBuilder<QuerySnapshot>(
        stream: _chatMethods.fetchContacts(userId: userProvider.getUser.uid),
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            var docList = snapshot.data.docs;

            if (docList.isEmpty) {
              return QuietBox();
            }
            return ListView.builder(
              padding: EdgeInsets.all(10),
              itemCount: docList.length,
              itemBuilder: (context, index) {
                Contact contact = Contact.fromMap(docList[index].data());

                return ContactView(contact);
              },
            );
          }
          return Center(child: CircularProgressIndicator());
        },
      ),
    );
  }
}
