import 'package:flutter/services.dart';



class EventChannelBackgroundIssue {

  static const EventChannel _enteredBeaconsEvent =
  const EventChannel("com.unit1.beacon.entered.beacons");

  static Stream<dynamic> get getData {
    return _enteredBeaconsEvent.receiveBroadcastStream();
  }
}