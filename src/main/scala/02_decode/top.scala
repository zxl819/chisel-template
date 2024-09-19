// package lw

// import chisel3._
// import chisel3.util._

// class Top extends Module {
//     val io = IO(new Bundle{
//         val exit = Output(Bool())
//     })
//     // Core 类和Memory类的例化
//     val core = Module(new Core())
//     val memory  = Module(new Memory())

//     // 连接Core和Memory的接口
//     core.io.imem <> memory.io.imem
//     core.io.dmem <> memory.io.dmem
//     io.exit := core.io.exit
// }


package lw

import chisel3._

class Top extends Module {
  val io = IO(new Bundle {
    val exit = Output(Bool())
  })
  val core = Module(new Core())
  val memory = Module(new Memory())
  core.io.imem <> memory.io.imem
  core.io.dmem <> memory.io.dmem
  io.exit := core.io.exit
}
