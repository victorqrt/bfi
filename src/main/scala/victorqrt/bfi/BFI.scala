package victorqrt.bfi


import cats.data._
import cats.effect._
import cats.implicits._
import cats.mtl._
import scala.io.Source


import BFInterpreter._


object BFI extends IOApp:

  val usage = IO {
    println("Usage: ./bfi <script>")
    sys.exit(0) }

  def stateRef[S](s: S) =
    for
      r <- Ref[IO] of s
    yield new Stateful[IO, S] {
      def monad     = summon
      def get       = r.get
      def set(s: S) = r set s
    }

  def run(args: List[String]): IO[ExitCode] =
    for
      file <- IO(args(0)).handleErrorWith(_ => usage)
      src  <- Resource
                .fromAutoCloseable(IO(Source.fromFile(file.toString)))
                .use(_.mkString.pure[IO])
      prog <- IO(BFParser(src))
      mem  <- stateRef(BFMemory.apply)
      _    <- prog.map(execute[IO](_)(using mem))
                  .sequence
    yield
      ExitCode.Success
