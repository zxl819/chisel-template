// package fetch
// import chisel3._
// import chisel3.util._
// import common.Consts._
// import chisel3.util.experimental.loadMemoryFromFile

// /* ImemPortIO类继承Bundle类，包含addr和inst两个信号 */
// class ImemPortIo extends Bundle {
//   val addr = Input(UInt(WORD_LEN.W))
//   val inst = Output(UInt(WORD_LEN.W))
// }

// class Memory extends Module {
//   val io = IO(new Bundle {
//     val imem = new ImemPortIo()
//   })

//   val mem = Mem(16384,UInt(8.W))

//   loadMemoryFromFile(mem, "src/hex/fetch.hex")
//   //连续4个地址存储8位数据，形成32位指令
//   io.imem.inst := Cat(
//     mem(io.imem.addr + 3.U(WORD_LEN)),
//     mem(io.imem.addr + 2.U(WORD_LEN)),
//     mem(io.imem.addr + 1.U(WORD_LEN)),
//     mem(io.imem.addr)
//   )
// }

package fetch

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import common.Consts._

class ImemPortIo extends Bundle {
  val addr = Input(UInt(WORD_LEN.W))
  val inst = Output(UInt(WORD_LEN.W))
}

class Memory extends Module {
  val io = IO(new Bundle {
    val imem = new ImemPortIo()
  })

  val mem = Mem(16384, UInt(8.W))
  loadMemoryFromFile(mem, "src/hex/fetch.hex")
  io.imem.inst := Cat(
    mem(io.imem.addr + 3.U(WORD_LEN.W)), 
    mem(io.imem.addr + 2.U(WORD_LEN.W)),
    mem(io.imem.addr + 1.U(WORD_LEN.W)),
    mem(io.imem.addr)
  )
}