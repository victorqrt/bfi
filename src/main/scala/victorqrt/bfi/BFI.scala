package victorqrt.bfi

import cats.data.StateT
import cats.effect._
import cats.implicits._

import BFParser._

object BFI extends IOApp {

  val hw = "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>."

  def run(args: List[String]): IO[ExitCode] =
    for {
      prog <- BFParser(hw)
      mem   <- IO { BFMemory(prog.get) }
      _     <- prog.get
                   .map(execute)
                   .sequence
                   .run(mem)
      _     <- IO { println(mem.memory) }
    } yield {
      ExitCode.Success
    }

  def dispatch(e: Expression, mem: BFMemory): IO[Unit] =
    e match {
      case Op('.') => IO { print(mem.getAsString) }
      case Jmp(es) => if (mem.zero) IO.unit
                      else es.map(execute)
                             .sequence
                             .runA(mem) >> IO.unit
      case _       => IO.unit
    }

  def update(e: Expression, mem: BFMemory): BFMemory =
    e match {
      case Op('+') => mem.increment
      case Op('-') => mem.decrement
      case Op('<') => mem shift false
      case Op('>') => mem shift true
      case _       => mem
    }

  def execute(e: Expression): StateT[IO, BFMemory, Unit] =
    for {
      mem <- StateT.get[IO, BFMemory]
      _   <- StateT liftF dispatch(e, mem)
      _   <- StateT modify[IO, BFMemory] (m => update(e, m))
    } yield ()
}
