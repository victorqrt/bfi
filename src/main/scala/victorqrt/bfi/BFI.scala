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
      mem  <- IO { BFMemory.apply }
      _    <- prog.get
                   .map(execute)
                   .sequence
                   .runA(mem)
    } yield {
      ExitCode.Success
    }

  def execute(e: Expression): StateT[IO, BFMemory, Unit] =
    for {
      mem <- StateT.get[IO, BFMemory]
      _   <- dispatch(e, mem)
      _   <- StateT modify[IO, BFMemory] (m => update(e, m))
    } yield ()

  def dispatch(e: Expression, mem: BFMemory): StateT[IO, BFMemory, Unit] =
    e match {
      case Op('.') => StateT liftF IO { print(mem.getAsStr) }
      case Jmp(es) =>
        if (mem.zero) StateT liftF IO.unit
        else for {
               _   <- es.map(execute).sequence
               m   <- StateT.get[IO, BFMemory]
               res <- if (!m.zero) dispatch(e, m)
                      else StateT
                             .liftF(IO.unit)
                             .asInstanceOf[StateT[IO, BFMemory, Unit]]
             } yield res
      case _       => StateT liftF IO.unit
    }

  def update(e: Expression, mem: BFMemory): BFMemory =
    e match {
      case Op('+') => mem.increment
      case Op('-') => mem.decrement
      case Op('<') => mem shift false
      case Op('>') => mem shift true
      case _       => mem
    }
}
