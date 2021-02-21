import 'dart:async';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'package:agora_rtc_engine/rtc_engine.dart';
import 'package:agora_rtc_engine/rtc_local_view.dart' as RtcLocalView;
import 'package:agora_rtc_engine/rtc_remote_view.dart' as RtcRemoteView;
import 'package:flutter/scheduler.dart';
import 'package:provider/provider.dart';
import 'package:skype_clone/configs/agora_configs.dart';
import 'package:skype_clone/models/call.dart';
import 'package:skype_clone/provider/user_provider.dart';
import 'package:skype_clone/resources/call_methods.dart';

class PhoneCallScreen extends StatefulWidget {
  final Call call;
  final ClientRole role = ClientRole.Broadcaster;

  PhoneCallScreen({this.call});
  @override
  _PhoneCallScreenState createState() => _PhoneCallScreenState();
}

class _PhoneCallScreenState extends State<PhoneCallScreen> {
  bool _joined = false;
  int _remoteUid = null;
  bool _switch = false;
  bool muted = false;
  RtcEngine _engine;
  CallMethods callMethods = CallMethods();

  UserProvider userProvider;
  StreamSubscription callStreamSubscription;

  @override
  void initState() {
    super.initState();
    addPostFrameCallback();
    itializeAgora();
  }

  // Initialize the app
  Future<void> itializeAgora() async {
    // Create RTC client instance
    _engine = await RtcEngine.create(APP_ID);
    // Define event handler
    _engine.setEventHandler(RtcEngineEventHandler(
        joinChannelSuccess: (String channel, int uid, int elapsed) {
      print('joinChannelSuccess ${channel} ${uid}');
      setState(() {
        _joined = true;
      });
    }, userJoined: (int uid, int elapsed) {
      print('userJoined ${uid}');
      setState(() {
        _remoteUid = uid;
      });
    }, userOffline: (int uid, UserOfflineReason reason) {
      print('userOffline ${uid}');
      setState(() {
        _remoteUid = null;
      });
    }));
    // Join channel 123
    await _engine.joinChannel(null, widget.call.channelId, null, 0);
  }

  @override
  void dispose() {
    super.dispose();
    _engine.leaveChannel();
    _engine.destroy();
    callStreamSubscription.cancel();
  }

  void _onToggleMute() {
    setState(() {
      muted = !muted;
    });
    _engine.muteLocalAudioStream(muted);
  }

  addPostFrameCallback() {
    SchedulerBinding.instance.addPostFrameCallback((timeStamp) {
      userProvider = Provider.of<UserProvider>(context, listen: false);

      callStreamSubscription = callMethods
          .callStream(uid: userProvider.getUser.uid)
          .listen((DocumentSnapshot ds) {
        switch (ds.data) {
          case null:
            // snapshot is null which means that call is hanged and documents are deleted
            Navigator.pop(context);
            break;
          default:
            break;
        }

        if (ds.data() == null) {
          Navigator.pop(context);
        }
      });
    });
  }

  // Create a simple chat UI
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Agora Audio quickstart',
      home: Scaffold(
        backgroundColor: Colors.black,
        body: Center(
            child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              widget.call.receiverName,
              style: TextStyle(fontSize: 20, color: Colors.white),
            ),
            SizedBox(height: 25),
            _toolbar()
          ],
        )),
      ),
    );
  }

  /// Toolbar layout
  Widget _toolbar() {
    if (widget.role == ClientRole.Audience) return Container();
    return Container(
      alignment: Alignment.bottomCenter,
      padding: const EdgeInsets.symmetric(vertical: 48),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          RawMaterialButton(
            onPressed: _onToggleMute,
            child: Icon(
              muted ? Icons.mic_off : Icons.mic,
              color: muted ? Colors.white : Colors.blueAccent,
              size: 20.0,
            ),
            shape: CircleBorder(),
            elevation: 2.0,
            fillColor: muted ? Colors.blueAccent : Colors.white,
            padding: const EdgeInsets.all(12.0),
          ),
          RawMaterialButton(
            onPressed: () => callMethods.endCall(call: widget.call),
            child: Icon(
              Icons.call_end,
              color: Colors.white,
              size: 35.0,
            ),
            shape: CircleBorder(),
            elevation: 2.0,
            fillColor: Colors.redAccent,
            padding: const EdgeInsets.all(15.0),
          ),
        ],
      ),
    );
  }
}
