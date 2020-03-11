package victorqrt.bfi

import cats.Monad
import cats.effect._
import cats.implicits._
import cats.mtl._
import cats.mtl.instances.all._

import BFParser._

object BFInterpreter {

  type MemoryState[F[_]] = MonadState[F, BFMemory]

  def execute[F[_] : LiftIO : MemoryState : Monad]
    (e: Expression): F[Unit] =
    for {
      mem <- implicitly[MemoryState[F]].get
      _   <- dispatch[F](e, mem)
      _   <- implicitly[MemoryState[F]] modify (m => update(e, m))
    } yield ()

  def dispatch[F[_] : LiftIO : MemoryState : Monad]
    (e: Expression, mem: BFMemory): F[Unit] =
    e match {
      case Jmp(es) =>
        if (mem.zero) IO.unit.to[F]
        else for {
               _   <- es.map(execute[F]).sequence
               m   <- implicitly[MemoryState[F]].get
               res <- dispatch[F](e, m)
             } yield res

      case Op('.') => IO { print(mem.getAsStr) }.to[F]

      case Op(',') =>
        for {
          c <- IO { io.StdIn.readChar }.to[F]
          _ <- execute[F](Read(c))
        } yield ()

      case _       => IO.unit.to[F]
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
