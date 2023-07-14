FROM ubuntu:22.04
RUN apt-get update -y
RUN apt-get install -y openjdk-17-jdk
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64/
RUN export JAVA_HOME
RUN java -version
RUN apt-get install -y git
RUN apt-get install maven -y
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
RUN git clone https://github.com/mateuscardoso99/trabalho-so-concorrencia.git
WORKDIR /usr/src/app/trabalho-so-concorrencia
RUN mvn package

#sem maven
#RUN javac -d -Xdiags:verbose -encoding utf8 -sourcepath ./src/ -d ./target/classes ./src/main/java/org/example/webserver/Main.java ./src/main/java/org/example/model/*.java ./src/main/java/org/example/threads/*.java ./src/main/java/org/example/utils/DateFormatter.java 
#RUN jar cvfm Main.jar ./src/main/resources/META-INF/MANIFEST.MF ./target/classes/org/example/model/*.class ./target/classes/org/example/threads/*.class ./target/classes/org/example/utils/*.class ./target/classes/org/example/webserver/*.class
#ENTRYPOINT ["java","-cp","target/classes/","org.example.webserver.Main"]

#outra forma. javac cria pasta com os bytecodes (.class) com o mesmo nome do atributo package declarado nas classes (org.example..) fazendo com que quando executar o .jar não de erro de ClassNotFoundException
#arquivo jar: empacota os bytecodes (.class) em um arquivo executável
#RUN javac -d -Xdiags:verbose -encoding utf8 -classpath ./src/ -d . ./src/main/java/org/example/webserver/Main.java ./src/main/java/org/example/model/*.java ./src/main/java/org/example/threads/*.java ./src/main/java/org/example/utils/DateFormatter.java
#RUN jar cfmv Main.jar ./src/main/resources/META-INF/MANIFEST.MF ./org/example/model/*.class ./org/example/threads/*.class ./org/example/utils/*.class ./org/example/webserver/*.class
#ENTRYPOINT ["java"."-cp","Main.jar","org.example.webserver.Main"]

ENTRYPOINT ["java","-jar","./target/trabalho-1.0-SNAPSHOT.jar"]
EXPOSE 8080
