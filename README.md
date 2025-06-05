# Multi_TicTacToe

## 프로젝트 구조
- Client
    - TTTClientGUI.java (클라이언트, GUI 기반)
    - SocketClient.java (클라이언트-서버 소켓 통신)
- Server
    - TTTServer.java (서버, 멀티 세션 기반)
    - GameSession.java (세션 관리)
    - GameLogic.java (틱택토 게임 로직(판 검사, 승패 판단))
    - MessageHandler.java (프로토콜 정의 및 메시지 파싱)

## 빌드 방법
0. 필요 종속성
    - Java
    - Git
2. 프로젝트 클론
    - git clone https://github.com/cprypt/Multi_TicTacToe.git
3. 자바 소스 코드 컴파일
    - mkdir -p out
    - javac -d out server/\*.java client/\*.java
4. 서버/클라이언트 실행
    - java -cp out server.TTTServer [포트번호]
    - java -cp out client.TTTClientGUI [서버호스트] [포트번호]

## 업데이트 내역
1. 서버/클라이언트 -> CLI 구현 (완)
2. 클라이언트 -> GUI 구현 (완)
3. 서버 -> 분할 세션 구현 (완)
4. 서버/클라이언트 -> 싱글 플레이 구현
