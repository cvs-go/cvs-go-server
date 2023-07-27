#!/bin/sh

PROJECT_ROOT="/home/ubuntu/cvs-go-server"
JAR_FILE="$PROJECT_ROOT/cvsgo-0.0.1-SNAPSHOT.jar"

# 현재 구동 중인 애플리케이션 pid 확인
CURRENT_PID=$(pgrep -f $JAR_NAME)

# 프로세스가 켜져 있을 경우 종료
if [ -z $CURRENT_PID ]; then
  echo "> NOT RUNNING"
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

# build 파일 복사
cp $PROJECT_ROOT/build/libs/cvsgo-0.0.1-SNAPSHOT.jar $JAR_FILE

# jar 파일 실행
echo "> deploy $JAR_FILE"
nohup java -jar $JAR_FILE &
