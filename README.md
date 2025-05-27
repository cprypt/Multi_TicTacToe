# Multi_TicTacToe

## 프로젝트 구조
- Client
    - TTTClient.java (클라이언트 메인 클래스, 서버와 상호작용)
    - SocketClient.java (서버 소켓 연결 및 메시지 송수신 구현)
    - UserInterface.java (보드 렌더링 및 메시지 파싱)
    - InputHandler.java (사용자 입력 처리 및 검증)
- Server
    - TTTServer.java (서버 메인 클래스, 게임 흐름 제어)
    - SocketManager.java (클라이언트 연결 및 메시지 송수신 관리)
    - GameLogic.java (틱택토 게임 로직(판 검사, 승패 판단))
    - MessageHandler.java (메시지 포맷 정의 및 파싱)

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
    - java -cp out; server.TTTServer [포트번호]
    - java -cp out; client.TTTClient [서버호스트] [포트번호]

## 업데이트 내역
1. 서버/클라이언트 -> CLI 구현 (완)
2. 서버 -> 분할 세션 구현
3. 클라이언트 -> GUI 구현
4. 서버/클라이언트 -> 싱글 플레이 구현
