package victorqrt.bfi


import cats.Monad
import cats.effect._
import cats.implicits._
import cats.mtl._


import BFParser._
import BFParser.Instruction._


object BFInterpreter:

  type MemoryState[F[_]] = Stateful[F, BFMemory]

  inline def FState[F[_] : MemoryState] = summon

  def execute[F[_] : Sync : MemoryState : Monad]
    (op: Instruction): F[Unit] =
    for
      mem <- FState.get
      _   <- dispatch[F](op, mem)
      _   <- FState modify (m => update(op, m))
    yield ()

  def dispatch[F[_] : Sync : MemoryState : Monad]
    (op: Instruction, mem: BFMemory): F[Unit] =

    op match

      case Jump(ops) =>
        if mem.zero then Sync[F] delay ()
        else for
          _   <- ops.map(execute[F]).sequence
          m   <- FState.get
          res <- dispatch[F](op, m)
        yield res

      case Print => Sync[F] delay print(mem.getAsStr)

      case Read =>
        for
          c <- Sync[F] delay io.StdIn.readChar
          _ <- FState modify (_ updated c.toByte)
        yield ()

      case _       => Sync[F] delay ()

  def update(e: Instruction, mem: BFMemory): BFMemory =
    e match
      case Add(b)      => mem add b
      case ShiftPtr(n) => mem shift n
      case Zero        => mem updated 0
      case _           => mem
