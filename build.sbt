assemblyJarName in assembly := "bfi.jar"

scalaVersion := "2.13.3"

scalacOptions ++= Seq(
  //"-Ypartial-unification",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.2.0",
  "org.typelevel" %% "cats-effect" % "2.2.0",
  "org.typelevel" %% "cats-mtl" % "1.0.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
)
