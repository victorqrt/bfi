scalaVersion := "2.12.8"
scalacOptions += "-Ypartial-unification"

assemblyJarName in assembly := "bfi.jar"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.0",
  "org.typelevel" %% "cats-effect" % "2.1.1",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
)
