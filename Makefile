SCALA_CLI := scala-cli
SCALA_VER := 3.3.0-RC6


define SCALA_DEPS =
//> using dep org.typelevel::cats-core:2.9.0
//> using dep org.typelevel::cats-effect:3.5.0
//> using dep org.typelevel::cats-mtl:1.3.1
//> using dep org.scala-lang.modules::scala-parser-combinators:2.3.0
endef

export SCALA_DEPS


bfi.jar: dependencies.scala
	${SCALA_CLI} --power package . -S ${SCALA_VER} --assembly -o $@


bfi: dependencies.scala
	${SCALA_CLI} --power package . -S ${SCALA_VER} -o $@ --native-image \
	-- --no-fallback


dependencies.scala: Makefile
	echo "$$SCALA_DEPS" > $@


clean:
	rm -f dependencies.scala bfi bfi.jar bfi.build_artifacts.txt


clean-cache: clean
	rm -rf .bsp .scala-build
	${SCALA_CLI} clean .


native: bfi
	strip -s bfi
	[ -x "$(shell command -v upx)" ] && upx bfi


repl:
	${SCALA_CLI} repl . -S ${SCALA_VER}


.PHONY: clean clean-cache native repl
