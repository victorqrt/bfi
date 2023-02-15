package bfi


import cats.data._
import cats.effect._
import cats.implicits._
import cats.mtl._
import scala.io.Source

import BFInterpreter._


object BFI extends IOApp:

  lazy val usage = IO:
    println("Usage: ./bfi <script>")
    sys.exit(0)

  def run(args: List[String]): IO[ExitCode] =
    for
      file <- IO(args(0)).handleErrorWith(_ => usage)
      src  <- Resource
                .fromAutoCloseable(IO(Source.fromFile(file.toString)))
                .use(_.mkString.pure[IO])

      prog <- IO(BFParser(src))

      mem  <- Ref[IO] of BFMemory()
      memS  = new Stateful[IO, BFMemory]:
                 def monad            = summon
                 def get              = mem.get
                 def set(s: BFMemory) = mem set s

      _    <- prog.map(execute[IO](_)(using memS, Sync[IO])).sequence
    yield
      ExitCode.Success
