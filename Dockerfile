# 기본 이미지로 OpenJDK를 포함한 공식 Java 이미지를 사용
FROM openjdk:23

# 컨테이너 내부에서 애플리케이션 파일을 저장할 디렉토리 생성
VOLUME /tmp

# 애플리케이션의 jar 파일을 컨테이너 내부로 복사
ADD build/libs/gyooltalk-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]