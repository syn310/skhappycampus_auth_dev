FROM adoptopenjdk/openjdk12:latest

VOLUME /tmp
ADD target/auth-0.0.1-SNAPSHOT.war auth.war
ENV JAVE_OPTS=""
RUN bash -c 'touch /auth.war'
ENTRYPOINT ["java","-Djasecurity.egd=file:/dev/./urandom","-jar","/auth.war"]