# Multi_TicTacToe

## 프로젝트 구조
- Client
    - TTTClient.java (GUI 기반 클라이언트, 메시지 처리)
    - SocketClient.java (클라이언트-서버 소켓 통신, 메시지 송수신)
- Server
    - TTTServer.java (쓰레드 세션 기반 서버, 대기 큐 처리)
    - GameSession.java (클라이언트-서버 소켓 통신, 메시지 송수신)
    - ServerConsole.java (서버 관리 콘솔, 클라이언트 연결 및 세션 관리)
    - GameLogic.java (틱택토 게임 로직(보드 처리, 승패 판단))
    - MessageHandler.java (프로토콜 정의, 메시지 파싱)

## 자동 빌드 방법
1. UNIX/Linux: build_unix_linux.sh 실행 TTTServer.jar, TTTClient.jar 실행
2. Windows: build_windows.bat 실행 후 TTTServer.jar, TTTClient.jar 실행

## 수동 빌드 방법
0. 필요 종속성
    - Git
    - Java
1. 프로젝트 클론
    - git clone https://github.com/cprypt/Multi_TicTacToe.git
    - cd Multi_TicTacToe
2. 자바 소스 코드 컴파일
    - mkdir -p out
    - javac -d out server/\*.java client/\*.java
3. Jar 파일 생성
    - jar cvfm TTTServer.jar server-manifest.txt -C out server
    - jar cvfm TTTClient.jar client-manifest.txt -C out client
4. 서버/클라이언트 터미널 환경 실행
    - java -jar TTTServer.jar
    - java -jar TTTClient.jar

## 업데이트 내역
1. 서버/클라이언트 -> CLI 구현
2. 클라이언트 -> GUI 구현
3. 서버 -> 세션 구현
4. 서버 -> 서버 관리 GUI 구현
5. 서버 Port, 클라이언트 IP/Port 입력 GUI 구현
