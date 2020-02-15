package victorqrt.bfi

import BFParser._

class BFMemory(
  val memory: List[Byte],
  val pointer: Int
) {

  private def checkRealloc: List[Byte] = {
    if (pointer < memory.size) memory
    else memory ++ List.fill(pointer - memory.size + 1)(0.toByte)
  }

  private def mutate(increment: Boolean): BFMemory = {
    val mem = checkRealloc
    val inc = if (increment) 1 else -1
    new BFMemory(
      mem.updated(pointer, (mem(pointer) + inc).toByte),
      pointer
    )
  }

  def shift(right: Boolean): BFMemory =
    if (pointer == 0 && !right) this
    else new BFMemory(
      memory,
      pointer + (if (right) 1 else -1)
    )

  def updated(b: Byte): BFMemory = {
    val mem = checkRealloc
    new BFMemory(mem.updated(pointer, b), pointer)
  }

  def get       = checkRealloc(pointer)
  def getAsStr  = new String(Array(get), "utf-8")
  def increment = mutate(true)
  def decrement = mutate(false)
  def zero      = get == 0
}

object BFMemory {
  def apply =
    new BFMemory(List.fill(1)(0.toByte), 0)
}
