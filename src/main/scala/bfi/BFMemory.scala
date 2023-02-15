package bfi


case class BFMemory
  ( ptr: Int         = 0
  , mem: Array[Byte] = Array.fill(64)(0.toByte)):

  private def checkRealloc: Array[Byte] =
    if ptr < mem.size then mem
    else mem ++ Array.fill(1 + ptr)(0.toByte)

  def updated(b: Byte) =
    copy(mem = checkRealloc.updated(ptr, b))

  def add(b: Byte)       = updated((get + b).toByte)
  def get                = if ptr < mem.size then mem(ptr) else 0
  def shift(offset: Int) = copy(ptr = 0 max (ptr + offset))
  def zero               = get == 0
