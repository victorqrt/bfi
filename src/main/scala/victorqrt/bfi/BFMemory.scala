package victorqrt.bfi

import scala.collection.mutable.ArrayBuffer

import BFParser._

class BFMemory(
  val memory: ArrayBuffer[Byte],
  val pointer: Int,
  val program: List[Expression]
) {

  private def checkRealloc {
    if (pointer >= memory.size) {
      memory ++= ArrayBuffer.fill(pointer - memory.size + 1)(0.toByte)
    }
  }

  private def mutate(increment: Boolean): BFMemory = {
    checkRealloc
    memory(pointer) = (memory(pointer) + (if (increment) 1 else -1)).toByte
    new BFMemory(memory, pointer, program)
  }

  def get: Byte = {
    checkRealloc
    memory(pointer)
  }

  def getAsString = new String(Array(get), "utf-8")

  def increment = mutate(true)
  def decrement = mutate(false)
  def zero      = get == 0

  def shift(right: Boolean): BFMemory =
    new BFMemory(
      memory,
      pointer + {
        if (right) 1
        else if (pointer == 0) 0 else -1
      },
      program
    )
}

object BFMemory {
  def apply(es: List[Expression]) =
    new BFMemory(ArrayBuffer.fill(1)(0.toByte), 0, es)
}
