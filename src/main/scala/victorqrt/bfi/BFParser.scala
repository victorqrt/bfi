package victorqrt.bfi


import cats.effect._
import scala.util.parsing.combinator._


object BFParser extends RegexParsers:

  enum Expression:
    case Jmp(block: List[Expression])
    case Op(c: Char)
    case Read(c: Char)

  import Expression._

  override protected val whiteSpace = """[^<>+-\.,\[\]]+""".r

  def token: Parser[String] = "<" | ">" | "+" | "-" | "." | ","

  def lsb: Parser[String] = "["

  def rsb: Parser[String] = "]"

  def op: Parser[Op] = token ^^ { s => Op(s charAt 0) }

  def jmp: Parser[Jmp] = lsb ~> expr <~ rsb ^^ { e => Jmp(e) }

  def expr: Parser[List[Expression]] = rep(op | jmp)

  def apply(source: String) = IO(parseAll(expr, source))
