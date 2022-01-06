package victorqrt.bfi


import cats.effect._
import scala.util.parsing.combinator._


object BFParser extends RegexParsers:

  enum Op:
    case Add(b: Byte)
    case Jump(block: List[Op])
    case Print
    case Read
    case Shift(offset: Int)
    case Zero

  import Op._

  override protected val whiteSpace = """[^<>+-\.,\[\]]+""".r

  def add   = "+" ^^^ Add(1)
  def sub   = "-" ^^^ Add(-1)
  def left  = "<" ^^^ Shift(-1)
  def right = ">" ^^^ Shift(1)
  def out   = "." ^^^ Print
  def read  = "," ^^^ Read
  def op    = add | sub | left | right | out | read
  def jmp   = "[" ~> expr <~ "]" ^^ { Jump(_) }
  
  def expr: Parser[List[Op]] = rep(op | jmp)

  def optimize(is: List[Op]): List[Op] =
    is.foldLeft(Nil: List[Op]) {
      case (ops :+ Add(b), Add(b2))     => ops :+ Add((b + b2).toByte)
      case (ops :+ Shift(n), Shift(n2)) => ops :+ Shift(n + n2)
      case ops -> Jump(List(Add(-1)))   => ops :+ Zero
      case ops -> Jump(block)           => ops :+ Jump(optimize(block))
      case ops -> op                    => ops :+ op
    }

  def apply(source: String) = optimize(parseAll(expr, source).get)
