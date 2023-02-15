package bfi


import cats.effect._
import cats.implicits._
import cats.mtl._

import BFParser._
import BFParser.Op._


object BFInterpreter:

  type MemoryState[F[_]]                = Stateful[F, BFMemory]
  inline def mstate[F[_] : MemoryState] = summon[MemoryState[F]]
  inline def sync  [F[_] : Sync]        = summon[Sync[F]]


  def execute[F[_] : MemoryState : Sync]
    (op: Op): F[Unit] =
    for
      mem <- mstate.get
      _   <- dispatch[F](op, mem)
      _   <- mstate modify (m => update(op, m))
    yield ()


  def dispatch[F[_] : MemoryState : Sync]
    (op: Op, mem: BFMemory): F[Unit] =
    op match
      case Jump(ops) =>
        if mem.zero then ().pure[F]
        else for
          _   <- ops.map(execute[F]).sequence
          m   <- mstate.get
          res <- dispatch[F](op, m)
        yield res

      case Read  =>
        for
          c <- sync blocking Console.in.read.toChar
          _ <- mstate modify (_ updated c.toByte)
        yield ()

      case Print => sync delay print(mem.get.toChar)

      case _     => ().pure[F]


  def update(e: Op, mem: BFMemory): BFMemory =
    e match
      case Add(b)   => mem add b
      case Shift(n) => mem shift n
      case Zero     => mem updated 0
      case _        => mem
