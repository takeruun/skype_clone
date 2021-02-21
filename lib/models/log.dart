class Log {
  int logId;
  String callerName;
  String callerPic;
  String receiverName;
  String receiverPic;
  String callStatus;
  String timestamp;

  Log({
    this.logId,
    this.callerName,
    this.callerPic,
    this.receiverName,
    this.receiverPic,
    this.callStatus,
    this.timestamp,
  });

  Map<String, dynamic> toMap() => {
        'log_id': logId,
        'caller_name': callerName,
        'caller_pic': callerPic,
        'receiver_name': receiverName,
        'receiver_pic': receiverPic,
        'call_status': callStatus,
        'timestamp': timestamp,
      };

  factory Log.fromMap(Map<dynamic, dynamic> logMap) => Log(
        logId: logMap['log_id'],
        callerName: logMap['caller_name'],
        callerPic: logMap['caller_pic'],
        receiverName: logMap['receiver_name'],
        receiverPic: logMap['receiver_pic'],
        callStatus: logMap['call_status'],
        timestamp: logMap['timestamp'],
      );
}
