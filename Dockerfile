# Stage 1: Build file WAR bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và source code
COPY pom.xml .
COPY src ./src

# Build package war (bỏ qua tests)
RUN mvn clean package -DskipTests

# Stage 2: Chạy trên Tomcat 10.1 (Jakarta EE 10)
FROM tomcat:10.1-jdk17-temurin

# Xóa các webapp mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy file war vừa build thành ROOT.war để chạy ngay tại domain gốc
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Tự động đồng bộ cổng PORT của Render (nếu có) và nạp biến môi trường CSDL vào JVM
CMD ["sh", "-c", "if [ -n \"$PORT\" ]; then sed -i \"s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/\" /usr/local/tomcat/conf/server.xml; fi && export CATALINA_OPTS=\"$CATALINA_OPTS -DDB_DRIVER=${DB_DRIVER:-com.mysql.cj.jdbc.Driver} -DDB_URL=${DB_URL:-jdbc:mysql://localhost:3306/murach} -DDB_USER=${DB_USER:-root} -DDB_PASSWORD=${DB_PASSWORD:-123456}\" && catalina.sh run"]
