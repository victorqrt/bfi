package victorqrt.bfi

import cats.~>
import cats.free.Free
import cats.data.State

import victorqrt.bfi.BFParser._

object BFAlgebra {

  sealed trait BFAlgebra[A]
  final case class Jump(es: List[Expression], s: BFState) extends BFAlgebra[Unit]
  final case class Shift(o: Op, s: BFState)               extends BFAlgebra[Unit]
  final case class Mutate(o: Op, s: BFState)              extends BFAlgebra[Unit]
  final case class Input(s: BFState)                      extends BFAlgebra[Unit]
  final case class Output(s: BFState)                     extends BFAlgebra[Unit]

  type FreeBF[A]  = Free[BFAlgebra, A]
  type BFState = State[BFMemory, Unit]

  def jump(es: List[Expression], s: BFState): FreeBF[Unit] =
    Free liftF Jump(es, s)

  def shift(o: Op, s: BFState): FreeBF[Unit] =
    Free liftF Shift(o, s)

  def mutate(o: Op, s: BFState): FreeBF[Unit] =
    Free liftF Mutate(o, s)

  def input(s: BFState): FreeBF[Unit] =
    Free liftF Input(s)

  def output(s: BFState): FreeBF[Unit] =
    Free liftF Output(s)

  def represent(exprs: List[Expression], s: BFState): List[FreeBF[_]] =
    exprs map {

      case Jmp(es)   => jump(es, s)

      case o @ Op(c) =>
        c match {
          case '<' | '>' => shift(o, s)
          case '+' | '-' => mutate(o, s)
          case '.'       => output(s)
          case ','       => input(s)
        }
    }

  //val BFInterpreterIO
}
