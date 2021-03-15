assemblyJarName in assembly := "bfi.jar"

scalaVersion := "3.0.0-RC1"

scalacOptions ++= Seq(
  //"-Ypartial-unification",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"   % "2.4.2",
  "org.typelevel" %% "cats-effect" % "2.3.3",
  "org.typelevel" %% "cats-mtl"    % "1.1.2",

  ("org.scala-lang.modules" % "scala-parser-combinators_2.13" % "1.1.2").withDottyCompat("2.13.3")
)
