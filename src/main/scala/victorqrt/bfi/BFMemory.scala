package victorqrt.bfi

import scala.collection.mutable.ArrayBuffer

import BFParser._

class BFMemory(
  val memory: ArrayBuffer[Byte],
  val pointer: Int
) {

  private def checkRealloc {
    if (pointer >= memory.size) {
      memory ++= ArrayBuffer.fill(pointer - memory.size + 1)(0.toByte)
    }
  }

  private def mutate(increment: Boolean): BFMemory = {
    checkRealloc
    memory(pointer) = (memory(pointer) + (if (increment) 1 else -1)).toByte
    new BFMemory(memory, pointer)
  }

  def get: Byte = {
    checkRealloc
    memory(pointer)
  }

  def increment = mutate(true)
  def decrement = mutate(false)
  def getAsStr  = new String(Array(get), "utf-8")
  def zero      = get == 0

  def shift(right: Boolean): BFMemory =
    if (pointer == 0 && !right) this
    else new BFMemory(
      memory,
      pointer + (if (right) 1 else -1)
    )
}

object BFMemory {
  def apply =
    new BFMemory(ArrayBuffer.fill(1)(0.toByte), 0)
}
