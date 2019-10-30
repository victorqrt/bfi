package victorqrt.bfi

import cats._
import cats.effect._
import cats.implicits._
import scala.collection.mutable.ArrayBuffer

object BFI extends IOApp {

  val hw = "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>."

  def run(args: List[String]): IO[ExitCode] =
    for {
      test   <- BFParser(hw)
      memory <- IO { new BFMemory(new ArrayBuffer[Byte], 0) }
      code   <- IO { println(test) } as ExitCode.Success
    } yield code
}
