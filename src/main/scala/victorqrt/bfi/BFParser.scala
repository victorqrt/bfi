package victorqrt.bfi


import cats.effect._
import scala.util.parsing.combinator._


object BFParser extends RegexParsers:

  enum Instruction:
    case Add(b: Byte)
    case Jump(block: List[Instruction])
    case Print
    case Read
    case ShiftPtr(offset: Int)
    case Zero

  import Instruction._

  override protected val whiteSpace = """[^<>+-\.,\[\]]+""".r

  def add:   Parser[Instruction] = "+" ^^ { _ => Add(1) }
  def sub:   Parser[Instruction] = "-" ^^ { _ => Add(-1) }
  def left:  Parser[Instruction] = "<" ^^ { _ => ShiftPtr(-1) }
  def right: Parser[Instruction] = ">" ^^ { _ => ShiftPtr(1) }
  def out:   Parser[Instruction] = "." ^^ { _ => Print }
  def read:  Parser[Instruction] = "," ^^ { _ => Read }

  def op: Parser[Instruction] = add | sub | left | right | out | read

  def jmp: Parser[Jump] = "[" ~> expr <~ "]" ^^ { Jump(_) }

  def expr: Parser[List[Instruction]] = rep(op | jmp)

  def optimize(is: List[Instruction]): List[Instruction] =
    is.foldLeft(Nil: List[Instruction]) {
      (is: List[Instruction], i: Instruction) => is -> i match
        case (ops :+ Add(b), Add(b2))           => ops :+ Add((b + b2).toByte)
        case (ops :+ ShiftPtr(n), ShiftPtr(n2)) => ops :+ ShiftPtr(n + n2)
        case ops -> Jump(List(Add(-1)))         => ops :+ Zero
        case ops -> Jump(block)                 => ops :+ Jump(optimize(block))
        case ops -> op                          => ops :+ op
    }

  def apply(source: String) = optimize(parseAll(expr, source).get)
