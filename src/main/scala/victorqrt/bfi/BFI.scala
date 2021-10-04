package victorqrt.bfi


import cats.data._
import cats.effect._
import cats.implicits._
import scala.io.Source


import BFInterpreter._


object BFI extends IOApp:

  val usage = IO {
    println("Usage: ./bfi <script>")
    sys.exit(1)
  }

  type Stack[R] = StateT[IO, BFMemory, R]

  def run(args: List[String]): IO[ExitCode] =
    for
      file <- IO(args(0)) handleErrorWith (_ => usage)
      src  <- Resource
                .fromAutoCloseable(Source.fromFile(file.toString).pure[IO])
                .use(s => s.mkString.pure[IO])
      prog <- BFParser.apply(src).pure[IO]
      mem  <- BFMemory.apply.pure[IO]
      _    <- prog.map(execute[Stack])
                  .sequence
                  .runA(mem)
    yield
      ExitCode.Success
