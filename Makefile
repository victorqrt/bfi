all: bfi.jar

bfi: bfi.jar
	${GRAALVM_HOME}/bin/native-image \
	--allow-incomplete-classpath --no-fallback \
	--initialize-at-build-time -jar bfi.jar

bfi.jar: target
	cp $(shell find target/ | grep bfi.jar) .

target:
	sbt assembly

clean:
	rm -fr target/ project/project/ project/target/ bfi bfi.jar bfi.build_artifacts.txt

native: bfi
	strip -s bfi

.PHONY: all clean native
