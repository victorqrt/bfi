all: jar
	cp $(shell find target/ | grep bfi.jar) .

jar:
	sbt assembly

native: all
	${GRAALVM_HOME}/bin/native-image -jar bfi.jar

clean:
	rm -fr target/ project/project/ project/target/ bfi bfi.jar
