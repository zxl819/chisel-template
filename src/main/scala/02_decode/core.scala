package decode
import chisel3._
import common.Consts._

class Core extends Module {
  val io = IO(new Bundle {
    val imem = Flipped(new ImemPortIo())
    val exit = Output(Bool())
  })

  val regfile = Mem(32, UInt(WORD_LEN.W))


  //**********************************
  // Instruction Fetch (IF) Stage

  val pc_reg = RegInit(START_ADDR)
  pc_reg := pc_reg + 4.U(WORD_LEN.W)
  io.imem.addr := pc_reg
  val inst = io.imem.inst

  //**********************************
  // Instruction Decode (ID) Stage
val rs1_addr = inst(19, 15) //r1寄存器的编号为指令列15～19位
val rs2_addr = inst(24, 20) //r2寄存器的编号为指令列20～24位
val wb_addr  = inst(11, 7) // rd寄存器的编号为指令列7～11位

val rs1_data = Mux((rs1_addr =/= 0.U), regfile(rs1_addr), 0.U(WORD_LEN.W)) //如果rs1_addr不为0，则将rs1_data赋值为regfile(rs1_addr)，否则赋值为0
val rs2_data = Mux((rs2_addr =/= 0.U), regfile(rs2_addr), 0.U(WORD_LEN.W)) //如果rs2_addr不为0，则将rs2_data赋值为regfile(rs2_addr)，否则赋值为0

//debug
io.exit := (inst === 0x34333231.U(WORD_LEN.W))
 printf(p"pc_reg   : 0x${Hexadecimal(pc_reg)}\n")
printf(p"rs1_addr : 0x${Hexadecimal(rs1_addr)}\n")
printf(p"rs2_addr : 0x${Hexadecimal(rs2_addr)}\n")
printf(p"wb_addr  : 0x${Hexadecimal(wb_addr)}\n")
printf(p"rs1_data : 0x${Hexadecimal(rs1_data)}\n")
printf(p"rs2_data : 0x${Hexadecimal(rs2_data)}\n")
  printf("---------\n")
}




