
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 拷贝应用程序jar包到镜像中
COPY target/userservice-0.0.1-SNAPSHOT.jar /app/myapp.jar

# 暴露应用程序的端口号
EXPOSE 8080

# 启动应用程序
CMD ["java", "-jar", "myapp.jar"]
