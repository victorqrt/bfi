package victorqrt.bfi


import cats.Monad
import cats.effect._
import cats.implicits._
import cats.mtl._


import BFParser._
import BFParser.Op._


object BFInterpreter:

  type MemoryState[F[_]] = Stateful[F, BFMemory]

  inline def FState[F[_] : MemoryState] = summon
  inline def FSyn[F[_]   : Sync]        = summon

  def execute[F[_] : MemoryState : Monad : Sync]
    (op: Op): F[Unit] =
    for
      mem <- FState.get
      _   <- dispatch[F](op, mem)
      _   <- FState modify (m => update(op, m))
    yield ()

  def dispatch[F[_] : MemoryState : Monad : Sync]
    (op: Op, mem: BFMemory): F[Unit] =
    op match
      case Jump(ops) =>
        if mem.zero then ().pure[F]
        else for
          _   <- ops.map(execute[F]).sequence
          m   <- FState.get
          res <- dispatch[F](op, m)
        yield res

      case Read  =>
        for
          c <- FSyn blocking Console.in.read.toChar
          _ <- FState modify (_ updated c.toByte)
        yield ()

      case Print => FSyn delay print(mem.get.toChar)

      case _     => ().pure[F]

  def update(e: Op, mem: BFMemory): BFMemory =
    e match
      case Add(b)   => mem add b
      case Shift(n) => mem shift n
      case Zero     => mem updated 0
      case _        => mem
