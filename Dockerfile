FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl as builder

WORKDIR /home/app
ADD . /home/app/users
RUN cd users && ./mvnw -Dmaven.test.skip=true clean package


FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl as optimizer

WORKDIR /home/app
COPY --from=builder /home/app/users/target/*.jar users.jar
RUN java -Djarmode=tools -jar users.jar extract --layers --launcher


FROM bellsoft/liberica-runtime-container:jre-21-stream-musl

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
COPY --from=optimizer /home/app/users/dependencies/ ./
COPY --from=optimizer /home/app/users/spring-boot-loader/ ./
COPY --from=optimizer /home/app/users/snapshot-dependencies/ ./
COPY --from=optimizer /home/app/users/application/ ./