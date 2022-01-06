package victorqrt.bfi


class BFMemory(val memory: Array[Byte], val pointer: Int):

  private def checkRealloc: Array[Byte] =
    if pointer < memory.size then memory
    else memory ++ Array.fill(1 + pointer)(0.toByte)

  def add(b: Byte): BFMemory = this updated (get + b).toByte

  def shift(offset: Int): BFMemory =
    new BFMemory(memory, 0 max (pointer + offset))

  def updated(b: Byte): BFMemory =
    new BFMemory(checkRealloc.updated(pointer, b), pointer)

  def get  = if pointer < memory.size then memory(pointer) else 0
  def zero = get == 0


object BFMemory:

  def apply = new BFMemory(Array.fill(64)(0.toByte), 0)
