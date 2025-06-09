# Multi_TicTacToe

## 프로젝트 구조
- Client
    - TTTClientGUI.java (GUI 기반 클라이언트, 메시지 처리)
    - SocketClient.java (클라이언트-서버 소켓 통신, 메시지 송수신)
- Server
    - TTTServer.java (쓰레드 세션 기반 서버, 대기 큐 처리)
    - GameSession.java (클라이언트-서버 소켓 통신, 메시지 송수신)
    - ServerConsole.java (서버 관리 콘솔, 클라이언트 연결 및 세션 관리)
    - GameLogic.java (틱택토 게임 로직(보드 처리, 승패 판단))
    - MessageHandler.java (프로토콜 정의, 메시지 파싱)

## 빌드 방법
0. 필요 종속성
    - Git
    - Java
1. 프로젝트 클론
    - git clone https://github.com/cprypt/Multi_TicTacToe.git
    - cd Multi_TicTacToe
2. 자바 소스 코드 컴파일
    - mkdir -p out
    - javac -d out server/\*.java client/\*.java
3. 서버/클라이언트 실행
    - java -cp out server.TTTServer [포트번호]
    - java -cp out client.TTTClient [서버호스트] [포트번호]

## 업데이트 내역
1. 서버/클라이언트 -> CLI 구현
2. 클라이언트 -> GUI 구현
3. 서버 -> 분할 세션 구현
4. 서버 -> 서버 관리 콘솔 구현 (예정)
