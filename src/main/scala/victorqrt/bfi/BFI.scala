package victorqrt.bfi

import cats._
import cats.effect._
import cats.implicits._

object BFI extends IOApp {

  val hw = "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>."

  def run(args: List[String]): IO[ExitCode] =
    for {
      test   <- IO { BFParser.parseAll(BFParser.expr, hw) }
      memory <- IO { new BFMemory }
      code   <- IO { println(test) } as ExitCode.Success
    } yield code
}
