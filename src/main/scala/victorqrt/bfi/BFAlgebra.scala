package victorqrt.bfi

import cats.~>
import cats.free.Free
import cats.data.State

import victorqrt.bfi.BFParser._

object BFAlgebra {

  sealed trait BFAlgebra[A]
  final case class Shift(c: Char) extends BFAlgebra[State[BFMemory, Unit]]
  final case class Mutate(c: Char) extends BFAlgebra[State[BFMemory, Unit]]
  final case class Jump() extends BFAlgebra[State[BFMemory, Unit]]
  final case class Input() extends BFAlgebra[Unit]
  final case class Output() extends BFAlgebra[Unit]

  type FreeBF[A] = Free[BFAlgebra, A]

  def input: FreeBF[Unit] =
    Free liftF Input()

  def output: FreeBF[Unit] =
    Free liftF Output()

  def represent(exprs: List[Expression]) =
    exprs map {

      case Op(c)   => c match {
        case '<' | '>' => Shift(c)
        case '+' | '-' => Mutate(c)
        case '.'       => Output()
        case ','       => Input()
      }

      //case Jmp(es) =>
    }
}
