class Call {
  String callerId;
  String callerName;
  String callerPic;
  String receiverId;
  String receiverName;
  String receiverPic;
  String channelId;
  bool hasDialled;

  Call({
    this.callerId,
    this.callerName,
    this.callerPic,
    this.receiverId,
    this.receiverName,
    this.receiverPic,
    this.channelId,
    this.hasDialled,
  });

  Map<String, dynamic> toMap() => {
        'callerId': callerId,
        'callerName': callerName,
        'callerPic': callerPic,
        'receiverId': receiverId,
        'receiverName': receiverName,
        'receiverPic': receiverPic,
        'channelId': channelId,
        'hasDialled': hasDialled
      };

  Call.fromMap(Map<String, dynamic> callMap) {
    this.callerId = callMap['callerId'];
    this.callerName = callMap['callerName'];
    this.callerPic = callMap['callerPic'];
    this.receiverId = callMap['receiverId'];
    this.receiverName = callMap['receiverName'];
    this.receiverPic = callMap['receiverPic'];
    this.channelId = callMap['channelId'];
    this.hasDialled = callMap['hasDialled'];
  }
}
