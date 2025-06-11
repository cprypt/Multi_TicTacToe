@echo off
echo ==============================================
echo [Start] Windows 빌드 스크립트
echo ==============================================

REM 1) 출력 디렉토리 생성
echo [1] 출력 디렉토리 생성
mkdir out

REM 2) Java 소스 컴파일
echo [2] Java 소스 컴파일
javac -d out server\*.java client\*.java

REM 3) 서버용 JAR 생성
echo [3] TTTServer.jar 생성
jar cvfm TTTServer.jar server-manifest.txt -C out server

REM 4) 클라이언트용 JAR 생성
echo [4] TTTClient.jar 생성
jar cvfm TTTClient.jar client-manifest.txt -C out client

echo [OK] 빌드 성공