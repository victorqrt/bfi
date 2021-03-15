package victorqrt.bfi


class BFMemory(val memory: Array[Byte], val pointer: Int):

  private def checkRealloc: Array[Byte] =
    if pointer < memory.size then memory
    else memory ++ Array.fill(1 + pointer)(0.toByte)

  private def mutate(increment: Boolean): BFMemory =
    this updated (get + (if increment then 1 else -1)).toByte

  def shift(right: Boolean): BFMemory =
    if pointer == 0 && !right then this
    else new BFMemory(
      memory,
      pointer + (if right then 1 else -1),
    )

  def updated(b: Byte): BFMemory =
    new BFMemory(checkRealloc.updated(pointer, b), pointer)

  def get       = if pointer < memory.size then memory(pointer) else 0
  def getAsStr  = new String(Array(get), "utf-8")
  def increment = mutate(true)
  def decrement = mutate(false)
  def zero      = get == 0


object BFMemory:
  def apply = new BFMemory(Array.fill(64)(0.toByte), 0)
