package victorqrt.bfi

import cats.data.StateT
import cats.effect.IO
import cats.implicits._

import BFParser._

object BFInterpreter {

 def execute(e: Expression): StateT[IO, BFMemory, Unit] =
    for {
      mem <- StateT.get[IO, BFMemory]
      _   <- dispatch(e, mem)
      _   <- StateT modify[IO, BFMemory] (m => update(e, m))
    } yield ()

  def dispatch(e: Expression, mem: BFMemory): StateT[IO, BFMemory, Unit] =
    e match {

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

      case Op('.') => StateT liftF IO { print(mem.getAsStr) }

      case Op(',') =>
        for {
          c <- StateT liftF IO { readChar}
          _ <- execute(Read(c))
        } yield ()

      case _       => StateT liftF IO.unit
    }

  def update(e: Expression, mem: BFMemory): BFMemory =
    e match {
      case Op('+') => mem.increment
      case Op('-') => mem.decrement
      case Op('<') => mem shift false
      case Op('>') => mem shift true
      case Read(c) => mem updated c.toByte
      case _       => mem
    }
}