lazy val root = project
  .in(file("."))
  .settings(
    name                       := "bfi",
    Compile / mainClass        := Some("victorqrt.bfi.BFI"),
    assembly / assemblyJarName := "bfi.jar",
    scalaVersion               := "3.1.3",

    libraryDependencies ++= Seq
      ( "org.typelevel" %% "cats-core"   % "2.8.0"
      , "org.typelevel" %% "cats-effect" % "3.3.14"
      , "org.typelevel" %% "cats-mtl"    % "1.3.0"
      , "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
      )
  )
