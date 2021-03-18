import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';


void main() => runApp(chooseWidget(window.defaultRouteName));

Widget chooseWidget(String route) {
  switch (route) {
  // name of the route defined in the host app
    case 'support':
      return SupportActivity();
    default:
     return SupportActivity();
  }
}

class SupportActivity extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SupportScreen(),
    );
  }
}

class SupportScreen extends StatefulWidget {
  
  @override
  _SupportScreenState createState() => _SupportScreenState();
}

class _SupportScreenState extends State<SupportScreen> {
  
  static const platform = const MethodChannel('FLUTTER_TESTE');
  String _dataFromFlutter = "Android can ping you";
  

  Future<void> _getDataFromAndroid() async {
    String data;
    try {
      final String result = await platform.invokeMethod('CHAMANDO_METODO_ANDROID', {"data": "DIEGO"}); //sending data from flutter here
      data = result;
    } on PlatformException catch (_) {
      data = "Android is not responding please check the code";
    }
    setState(() {
      _dataFromFlutter = data;
    });
  }
  Future<dynamic> nativeMethodCallHandler(MethodCall methodCall) async {
    switch (methodCall.method) {
      case "CHAMANDO_METODO_FLUTTER" :
        setState(() {
          _dataFromFlutter = "NOME ENVIADO DO ANDROID: "+methodCall.arguments;
        });
        break;
      default:
        setState(() {
          _dataFromFlutter = "NENHUM VALOR PASSADO";
        });
        break;
    }
  }

  @override
  void initState() {
    platform.setMethodCallHandler(nativeMethodCallHandler);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: <Widget>[
              Container(
                padding: EdgeInsets.only(top:60, left: 30, right: 30, bottom: 30),
                width: MediaQuery. of(context). size. width,
                child: Image.asset("assets/logo_conecta_fahz.png"),
              ),
              Text(
                "$_dataFromFlutter", 
                style: 
                  TextStyle(
                    fontSize: 30, 
                    fontFamily: 'RobotoMono', 
                    fontWeight: FontWeight.bold, 
                    color: Colors.blue.shade800
                  )
              ),
              Container(
                padding: EdgeInsets.only(top:60, left: 30, right: 30, bottom: 0),
                child: Row(
                  children: <Widget>[
                    new Flexible(
                        child: TextField(
                          decoration: InputDecoration(
                              border: OutlineInputBorder(),
                              labelText: "CPF"),
                        )
                    ),
                    new SizedBox(width: 10,),
                    new Flexible(
                        child: TextField(
                          decoration: InputDecoration(
                              border: OutlineInputBorder(),
                              labelText: "Data Nascimento"),
                        )
                    )
                  ],
                ),
            ),
            Container(
              padding: EdgeInsets.only(top:30, left: 30, right: 30, bottom: 0),
              child: Row(
                children: <Widget>[
                  new Flexible(
                      child: TextField(
                        decoration: InputDecoration(
                            border: OutlineInputBorder(),
                            labelText: "E-mail"),
                      )
                  ),
                ],
              ),
            ),
            Container(
              padding: EdgeInsets.only(top:30, left: 30, right: 30, bottom: 0),
              child: Row(
                children: <Widget>[
                  new Flexible(
                      child: new DropdownButtonFormField<String>(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                            labelText: "Motivo"),
                        items: <String>['Motivo 1', 'Motivo 2', 'Motivo 3', 'Motivo 4'].map((String value) {
                          return new DropdownMenuItem<String>(
                            value: value,
                            child: new Text(value),
                          );
                        }).toList(),
                        onChanged: (_) {},
                      )
                  ),
                ],
              ),
            ),
              Container(
                padding: EdgeInsets.only(top:30, left: 30, right: 30, bottom: 30),
                child: Row(
                  children: <Widget>[
                    new Flexible(
                        child: TextField(
                          maxLines: 8,
                          decoration: InputDecoration(
                              border: OutlineInputBorder(),
                              labelText: "Informe o seu problema"),
                        )
                    ),
                  ],
                ),
              ),

              Container(
                padding: EdgeInsets.only(top:0, left: 30, right: 30, bottom: 30),
                child: InkWell(
                      // When the user taps the button, show a snackbar.
                      onTap: () {
                      },
                      child: Container(
                        width: 300,
                        child: RaisedButton(
                          color: Colors.blue,
                          onPressed: (){
                            _getDataFromAndroid();
                          },
                          child: Text('ENVIAR', textAlign: TextAlign.center, style: TextStyle(fontFamily: 'RobotoMono', fontWeight: FontWeight.bold, color: Colors.white)),
                        )
                      ),
                    )
                ),



            ],
          ),
        ),
      )
    );
  }
}