import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

void main() {
  runApp(MyApp());
}

/// MyApp은 MaterialApp을 사용하여 전체 애플리케이션을 감쌉니다.
class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter 확장 위젯 예제',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: HomePage(),
    );
  }
}

/// HomePage는 다양한 위젯을 보여주는 메인 페이지입니다.
class HomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // MediaQuery를 사용하여 화면 크기 정보를 가져옵니다.
    final mediaQuery = MediaQuery.of(context);
    final isLandscape = mediaQuery.orientation == Orientation.landscape;

    return Scaffold(
      appBar: AppBar(
        title: Text('홈 페이지'),
      ),
      body: SingleChildScrollView(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              // Material 위젯
              Text(
                'Material Design 위젯',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 10),
              ElevatedButton(
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Material 버튼이 눌렸습니다!')),
                  );
                },
                child: Text('Material 버튼'),
              ),
              Divider(height: 40),

              // Cupertino 위젯
              Text(
                'Cupertino (iOS) 위젯',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 10),
              CupertinoButton(
                color: CupertinoColors.activeBlue,
                onPressed: () {
                  // Cupertino 스타일의 알림을 표시
                  showCupertinoDialog(
                    context: context,
                    builder: (context) => CupertinoAlertDialog(
                      title: Text('알림'),
                      content: Text('Cupertino 버튼이 눌렸습니다!'),
                      actions: [
                        CupertinoDialogAction(
                          child: Text('확인'),
                          onPressed: () => Navigator.of(context).pop(),
                        ),
                      ],
                    ),
                  );
                },
                child: Text('Cupertino 버튼'),
              ),
              Divider(height: 40),

              // 커스텀 위젯
              Text(
                '커스텀 위젯',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 10),
              CustomCard(
                title: '커스텀 카드',
                description: '이것은 커스텀 위젯입니다. 클릭하면 팝업이 나타납니다.',
                icon: Icons.star,
              ),
              Divider(height: 40),

              // 반응형 디자인 예제
              Text(
                '반응형 디자인 (화면 크기: ${mediaQuery.size.width.toStringAsFixed(0)} x ${mediaQuery.size.height.toStringAsFixed(0)})',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                textAlign: TextAlign.center,
              ),
              SizedBox(height: 10),
              isLandscape
                  ? Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        ContainerBox(color: Colors.red),
                        SizedBox(width: 20),
                        ContainerBox(color: Colors.green),
                        SizedBox(width: 20),
                        ContainerBox(color: Colors.blue),
                      ],
                    )
                  : Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        ContainerBox(color: Colors.red),
                        SizedBox(height: 20),
                        ContainerBox(color: Colors.green),
                        SizedBox(height: 20),
                        ContainerBox(color: Colors.blue),
                      ],
                    ),
              Divider(height: 40),

              // Stateful 위젯 예제: 카운터
              Text(
                'Stateful 위젯: 카운터 예제',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 10),
              CounterWidget(),
            ],
          ),
        ),
      ),
    );
  }
}

/// CustomCard는 클릭 가능하며, 클릭 시 커스텀 팝업을 표시하는 커스텀 디자인의 카드 위젯입니다.
class CustomCard extends StatelessWidget {
  final String title;
  final String description;
  final IconData icon;

  CustomCard({
    required this.title,
    required this.description,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        // 카드가 클릭되었을 때 커스텀 팝업을 표시
        showDialog(
          context: context,
          builder: (context) => CustomPopupDialog(
            title: title,
            description: description,
          ),
        );
      },
      child: Card(
        elevation: 4,
        shadowColor: Colors.grey.withOpacity(0.5),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Row(
            children: [
              Icon(icon, size: 40, color: Colors.blue),
              SizedBox(width: 20),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title,
                        style:
                            TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                    SizedBox(height: 5),
                    Text(description, style: TextStyle(fontSize: 16)),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// CustomPopupDialog는 커스텀 팝업 대화상자를 구현한 위젯입니다.
class CustomPopupDialog extends StatelessWidget {
  final String title;
  final String description;

  CustomPopupDialog({required this.title, required this.description});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(title),
      content: Text(description),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: Text('닫기'),
        ),
      ],
    );
  }
}

/// ContainerBox는 반응형 디자인을 위한 예제용 컨테이너입니다.
class ContainerBox extends StatelessWidget {
  final Color color;

  ContainerBox({required this.color});

  @override
  Widget build(BuildContext context) {
    // MediaQuery를 사용하여 화면 너비에 따라 크기를 조절
    final screenWidth = MediaQuery.of(context).size.width;
    return Container(
      width: screenWidth * 0.2, // 화면 너비의 20%
      height: screenWidth * 0.2, // 화면 너비의 20%
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(10),
      ),
    );
  }
}

/// CounterWidget은 StatefulWidget을 사용하여 상태 변화를 관리하는 예제입니다.
class CounterWidget extends StatefulWidget {
  @override
  _CounterWidgetState createState() => _CounterWidgetState();
}

class _CounterWidgetState extends State<CounterWidget> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++; // 카운트 증가
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          '카운트: $_counter',
          style: TextStyle(fontSize: 24),
        ),
        SizedBox(height: 10),
        ElevatedButton(
          onPressed: _incrementCounter,
          child: Text('카운트 증가'),
        ),
      ],
    );
  }
}
