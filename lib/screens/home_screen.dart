import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/scheduler.dart';
import 'package:provider/provider.dart';
import 'package:skype_clone/enum/user_state.dart';
import 'package:skype_clone/models/user.dart' as model;
import 'package:skype_clone/provider/user_provider.dart';
import 'package:skype_clone/resources/auth_methods.dart';
import 'package:skype_clone/resources/local_db/repository/log_repository.dart';
import 'package:skype_clone/screens/call_screens/pickup/pickup_layout.dart';
import 'package:skype_clone/screens/chat_screens/chat_screen.dart';
import 'package:skype_clone/screens/page_views/chats/chat_list_screen.dart';
import 'package:skype_clone/screens/page_views/logs/log_screen.dart';
import 'package:skype_clone/utils/universal_variables.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> with WidgetsBindingObserver {
  PageController pageController;
  int _page = 0;
  final AuthMethods _authMethods = AuthMethods();
  model.User _user;

  UserProvider userProvider;

  @override
  void initState() {
    super.initState();
    SchedulerBinding.instance.addPostFrameCallback((_) async {
      userProvider = Provider.of<UserProvider>(context, listen: false);
      await userProvider.refreshUser();

      _authMethods.setUserState(
        userId: userProvider.getUser.uid,
        userState: UserState.Online,
      );
      LogRepository.init(isHive: true, dbName: userProvider.getUser.uid);
    });
    WidgetsBinding.instance.addObserver(this);

    pageController = PageController();

    FirebaseMessaging.instance
        .getInitialMessage()
        .then((RemoteMessage message) async {
      if (message != null) {
        _user = await _authMethods.getUserDetailsById(message.data['user_uid']);

        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ChatScreen(
              receiver: _user,
            ),
          ),
        );
      }
    });

    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) async {
      _user = await _authMethods.getUserDetailsById(message.data['user_uid']);

      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => ChatScreen(
            receiver: _user,
          ),
        ),
      );
      print('A new onMessageOpenedApp event was published!');
    });
  }

  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    String currentUserId =
        (userProvider != null && userProvider.getUser != null)
            ? userProvider.getUser.uid
            : "";

    super.didChangeAppLifecycleState(state);

    switch (state) {
      case AppLifecycleState.resumed:
        currentUserId != null
            ? _authMethods.setUserState(
                userId: currentUserId, userState: UserState.Online)
            : print("resume state");
        break;
      case AppLifecycleState.inactive:
        currentUserId != null
            ? _authMethods.setUserState(
                userId: currentUserId, userState: UserState.Offline)
            : print("inactive state");
        break;
      case AppLifecycleState.paused:
        currentUserId != null
            ? _authMethods.setUserState(
                userId: currentUserId, userState: UserState.Waiting)
            : print("paused state");
        break;
      case AppLifecycleState.detached:
        currentUserId != null
            ? _authMethods.setUserState(
                userId: currentUserId, userState: UserState.Offline)
            : print("detached state");
        break;
    }
  }

  void onPageChanged(int page) {
    setState(() {
      _page = page;
    });
  }

  void navigationTapped(int page) {
    pageController.jumpToPage(page);
  }

  @override
  Widget build(BuildContext context) {
    double _labelFontSize = 10.0;

    return PickupLayout(
      scaffold: Scaffold(
        backgroundColor: UniversalVariables.blackColor,
        body: PageView(
          children: [
            ChatListScreen(),
            LogScreen(),
            Center(
                child: Text(
              'Contact Screen',
              style: TextStyle(color: Colors.white),
            ))
          ],
          controller: pageController,
          onPageChanged: onPageChanged,
          physics: NeverScrollableScrollPhysics(),
        ),
        bottomNavigationBar: Container(
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 10),
            child: CupertinoTabBar(
              backgroundColor: UniversalVariables.blackColor,
              items: [
                BottomNavigationBarItem(
                  icon: Icon(Icons.chat,
                      color: _page == 0
                          ? UniversalVariables.lightBlueColor
                          : UniversalVariables.greyColor),
                  label: Text(
                    'Chats',
                    style: TextStyle(
                      fontSize: _labelFontSize,
                      color: _page == 0
                          ? UniversalVariables.blackColor
                          : Colors.grey,
                    ),
                  ).toStringShort(),
                ),
                BottomNavigationBarItem(
                  icon: Icon(Icons.call,
                      color: _page == 1
                          ? UniversalVariables.lightBlueColor
                          : UniversalVariables.greyColor),
                  label: Text(
                    'Chats',
                    style: TextStyle(
                      fontSize: _labelFontSize,
                      color: _page == 1
                          ? UniversalVariables.blackColor
                          : Colors.grey,
                    ),
                  ).toStringShort(),
                ),
                BottomNavigationBarItem(
                  icon: Icon(Icons.contact_phone,
                      color: _page == 2
                          ? UniversalVariables.lightBlueColor
                          : UniversalVariables.greyColor),
                  label: Text(
                    'Chats',
                    style: TextStyle(
                      fontSize: _labelFontSize,
                      color: _page == 2
                          ? UniversalVariables.blackColor
                          : Colors.grey,
                    ),
                  ).toStringShort(),
                ),
              ],
              onTap: navigationTapped,
              currentIndex: _page,
            ),
          ),
        ),
      ),
    );
  }
}
