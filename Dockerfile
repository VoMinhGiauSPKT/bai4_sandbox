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

# Copy thêm driver JDBC vào thư viện Tomcat để DataSource JNDI hoạt động tuyệt đối ổn định
COPY --from=build /app/target/bai4-1.0-SNAPSHOT/WEB-INF/lib/postgresql-*.jar /usr/local/tomcat/lib/
COPY --from=build /app/target/bai4-1.0-SNAPSHOT/WEB-INF/lib/mysql-*.jar /usr/local/tomcat/lib/

EXPOSE 8080

# Tự động đồng bộ cổng PORT của Render và nhận diện cấu hình PostgreSQL / MySQL
CMD ["sh", "-c", "if [ -n \"$PORT\" ]; then sed -i \"s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/\" /usr/local/tomcat/conf/server.xml; fi && URL=\"${DB_URL:-${INTERNAL_DATABASE_URL:-$DATABASE_URL}}\" && if [ -z \"$URL\" ]; then URL=\"jdbc:mysql://localhost:3306/murach\"; fi && case \"$URL\" in postgres://*) URL=\"jdbc:postgresql://${URL#postgres://}\" ;; postgresql://*) URL=\"jdbc:postgresql://${URL#postgresql://}\" ;; esac && DRIVER=\"$DB_DRIVER\" && if [ -z \"$DRIVER\" ]; then case \"$URL\" in *postgresql*) DRIVER=\"org.postgresql.Driver\" ;; *) DRIVER=\"com.mysql.cj.jdbc.Driver\" ;; esac; fi && export CATALINA_OPTS=\"$CATALINA_OPTS -DDB_DRIVER=$DRIVER -DDB_URL=$URL -DDB_USER=${DB_USER:-root} -DDB_PASSWORD=${DB_PASSWORD:-123456}\" && catalina.sh run"]

