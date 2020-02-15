package victorqrt.bfi

import cats.effect._
import cats.implicits._
import scala.io.Source

import BFInterpreter._

object BFI extends IOApp {

  val usage = IO {
    println("Usage: ./bfi <script>")
    System.exit(1)
  }

  def run(args: List[String]): IO[ExitCode] =
    for {
      file <- IO { args(0) } handleErrorWith { _ => usage }
      src  <- Resource
                .fromAutoCloseable(IO { Source.fromFile(file.toString) } )
                .use(s => IO { s.mkString })
      prog <- BFParser(src)
      mem  <- IO { BFMemory.apply }
      _    <- prog.get
                  .map(execute)
                  .sequence
                  .runA(mem)
    } yield {
      ExitCode.Success
    }
}
