package victorqrt.bfi


import cats.Monad
import cats.effect._
import cats.implicits._
import cats.mtl._


import BFParser._
import BFParser.Expression._


object BFInterpreter:

  type MemoryState[F[_]] = Stateful[F, BFMemory]

  inline def FState[F[_] : MemoryState] = summon

  def execute[F[_] : Sync : MemoryState : Monad]
    (e: Expression): F[Unit] =
    for
      mem <- FState.get
      _   <- dispatch[F](e, mem)
      _   <- FState modify (m => update(e, m))
    yield ()

  def dispatch[F[_] : Sync : MemoryState : Monad]
    (e: Expression, mem: BFMemory): F[Unit] =

    e match

      case Jmp(es) =>
        if mem.zero then Sync[F] delay ()
        else for
          _   <- es.map(execute[F]).sequence
          m   <- FState.get
          res <- dispatch[F](e, m)
        yield res

      case Op('.') => Sync[F] delay print(mem.getAsStr)

      case Op(',') =>
        for
          c <- Sync[F] delay io.StdIn.readChar
          _ <- execute[F](Read(c))
        yield ()

      case _       => Sync[F] delay ()

  def update(e: Expression, mem: BFMemory): BFMemory =
    e match
      case Op('+') => mem.increment
      case Op('-') => mem.decrement
      case Op('<') => mem shift false
      case Op('>') => mem shift true
      case Read(c) => mem updated c.toByte
      case _       => mem
