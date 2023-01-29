lazy val root = project
  .in(file("."))
  .settings(
    name                       := "bfi",
    Compile / mainClass        := Some("victorqrt.bfi.BFI"),
    assembly / assemblyJarName := "bfi.jar",
    scalaVersion               := "3.2.1",

    libraryDependencies ++= Seq
      ( "org.typelevel" %% "cats-core"   % "2.9.0"
      , "org.typelevel" %% "cats-effect" % "3.4.5"
      , "org.typelevel" %% "cats-mtl"    % "1.3.0"
      , "org.scala-lang.modules" %% "scala-parser-combinators" % "2.2.0"
      )
  )
