import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:restart_app/restart_app.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  static StreamController<NotificationDataArg> streamController =
      StreamController<NotificationDataArg>.broadcast();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('flutterNative');
  int _counter = 0;
  late StreamSubscription _streamSubscription;

  String result = "";

  @override
  void initState() {
    super.initState();
  }

  final List<String> _data = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text('EventChannel Background Issue Sample'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(_data.length == 0 ? 'No Data' : _data[_data.length - 1]),
            SizedBox(
              height: 20,
            ),
            InkWell(
              onTap: () async {
                var result = await platform.invokeMethod(
                  'stopSignalR',
                );
                print(result);
                Restart.restartApp();
              },
              child: const Material(
                color: Colors.deepPurple,
                child: Text("stop Service"),
              ),
            ),
            SizedBox(
              height: 20,
            ),
            InkWell(
              onTap: () {
                connectToAndroidChannel(
                    "com.unit1.beacon.entered.beacons", "token");
              },
              child: const Material(
                color: Colors.deepPurple,
                child: Text("start Service"),
              ),
            )
          ],
        ));
  }

  Future<void> connectToAndroidChannel(String userId, String token) async {
    _streamSubscription = EventChannelBackground.getData.listen((event) {
      if (event != "onNotificationPressed") {
        NotificationDataArg notificationDataArg =
            NotificationDataArg.fromJson(jsonDecode(event)[0]);
        MyApp.streamController.add(notificationDataArg);
        setState(() {
          _data.add(notificationDataArg.toString());
        });
      } else {
        print("NotificationPressed");
      }
    });

    try {
      final String result = await platform
          .invokeMethod('signalR', {"userId": userId, "token": token});
      print("FlutterSignalR:" + result.toString());
      if (result != "UNDEFINED") {
        print("NotificationPressed");
      }
    } on PlatformException catch (e) {
      throw "Failed to Invoke: '${e.message}'.";
    }
  }
}

class EventChannelBackground {
  static const EventChannel _enteredBeaconsEvent =
      const EventChannel("com.unit1.beacon.entered.beacons");

  static Stream<dynamic> get getData {
    return _enteredBeaconsEvent.receiveBroadcastStream();
  }
}

class NotificationDataArg {
  String? body;
  String? title;
  int? notificationId;
  String? orderNumber;

  NotificationDataArg(
      {this.body, this.title, this.notificationId, this.orderNumber});

  NotificationDataArg.fromJson(Map<String, dynamic> json) {
    body = json['body'];
    title = json['title'];
    notificationId = json['notificationId'];
    orderNumber = json['orderNumber'];
  }
}
