package victorqrt.bfi

import scala.collection.mutable.ArrayBuffer

class BFMemory(
  memory: ArrayBuffer[Byte] = new ArrayBuffer,
  var pointer: Int = 0
) {

  private def mutate(increase: Boolean) {
    val b = if (increase) 1 else -1

    if (pointer > memory.size) {
      memory ++= ArrayBuffer.fill(pointer - memory.size)(0.toByte)
    }

    memory(pointer) = (memory(pointer) + b).toByte
  }

  def increase { mutate(true) }
  def decrease { mutate(false) }

  def shift(right: Boolean): Unit =
    pointer += {
      if (right) 1
      else if (pointer == 0) 0 else -1
    }
}
