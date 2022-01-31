import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'notification_listener.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
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
    connectToAndroidChannel();
    super.initState();
  }

  final List<String> _data = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('EventChannel Background Issue Sample'),
      ),
      body: Center(
        child: SingleChildScrollView(
          child: StreamBuilder(
            stream: EventChannelBackgroundIssue.getData,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                print('Data Received: ${snapshot.data}'
                    'Total Data in List: ${_data.length}');
                _data.add(snapshot.data.toString());
                return Text('Latest Data: ${snapshot.data}');
              }
              return Text('No Data');
            },
          ),
        ),
      ),
    );
  }

  Future<void> connectToAndroidChannel() async {
    String response = "";
    try {
      final String result = await platform
          .invokeMethod('signalR', {"userId": "123123", "token": "token"});
      response = result;
      print(result);
    } on PlatformException catch (e) {
      response = "Failed to Invoke: '${e.message}'.";
    }
  }
}
