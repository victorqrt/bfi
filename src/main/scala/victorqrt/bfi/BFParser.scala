package victorqrt.bfi

import cats.effect._
import scala.util.parsing.combinator._

object BFParser extends RegexParsers {

  sealed trait Expression
  final case class Jmp(block: List[Expression]) extends Expression
  final case class Op(c: Char)                  extends Expression
  final case class Read(c: Char)                extends Expression

  override protected val whiteSpace = """[^<>+-\.,\[\]]+""".r

  def token: Parser[String] =
    "<" | ">" | "+" | "-" | "." | ","

  def lsb: Parser[String] = "["

  def rsb: Parser[String] = "]"

  def op: Parser[Op] =
    token ^^ { s => Op(s charAt 0) }

  def jmp: Parser[Jmp] =
    lsb ~> expr <~ rsb ^^ { e => Jmp(e) }

  def expr: Parser[List[Expression]] =
    rep(op | jmp)

  def apply(source: String): IO[ParseResult[List[Expression]]] =
    IO { parseAll(expr, source) }
}
