assemblyJarName in assembly := "bfi.jar"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-Ypartial-unification",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.typelevel" %% "cats-effect" % "2.0.0",
  "org.typelevel" %% "cats-mtl-core" % "0.7.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
)
