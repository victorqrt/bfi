lazy val root = project
  .in(file("."))
  .settings(
    name                       := "bfi",
    Compile / mainClass        := Some("victorqrt.bfi.BFI"),
    assembly / assemblyJarName := "bfi.jar",
    scalaVersion               := "3.0.2",

    libraryDependencies ++= Seq
      ( "org.typelevel" %% "cats-core"   % "2.6.1"
      , "org.typelevel" %% "cats-effect" % "3.2.9"
      , "org.typelevel" %% "cats-mtl"    % "1.2.1"
      , "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.0"
      )
  )
