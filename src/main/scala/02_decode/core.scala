package sw
import chisel3._
import chisel3.util._
import common.Consts._
import common.Instructions._

class Core extends Module {
  val io = IO(new Bundle {
    val imem = Flipped(new ImemPortIo()) //指令接口
    val dmem = Flipped(new DmemPortIo()) //DmemPortIo数据接口
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
   //ID内部offset符号拓展
   val imm_i = inst(31,20) //offset[11:0]
   val imm_i_sext = Cat(Fill(20,imm_i(11)),imm_i) //offset符号拓展
   // sw 指令的s格式指令的立即数imm_S的译码处理
   val imm_s = Cat(inst(31,25),inst(11,7))
   val imm_s_sext = Cat(Fill(20,imm_s(11)),imm_s) //用imm_S高位补全高20位
//**********************************
  // Execute (EX) Stage
  val alu_out = MuxCase(0.U(WORD_LEN.W),Seq(
    (inst === LW) ->(rs1_data + imm_i_sext), //存储器地址的计算
    (inst === SW) ->(rs1_data + imm_s_sext)
  ))
  //**********************************
  // Memory Access (MEM) Stage
  io.dmem.addr := alu_out //将EX阶段计算出的存储器地址链接到MEM阶段的存储器端口
  io.dmem.wen := (inst === SW)
  io.dmem.wdata := rs2_data

  // when(inst === LW){  //存储器的地址可以始终输出给存储器
  //   io.dmem.addr := alu_out
  // }
//**********************************
// Write Back (WB) Stage
val wb_data = io.dmem.rdata //将存储器中的数据输出给WB阶段
when(inst === LW){
  regfile(wb_addr) := wb_data
}
//**********************************
//debug
io.exit := (inst === 0x00602823.U(WORD_LEN.W))
printf(p"pc_reg   : 0x${Hexadecimal(pc_reg)}\n")
printf(p"rs1_addr : 0x${Hexadecimal(rs1_addr)}\n")
printf(p"rs2_addr : 0x${Hexadecimal(rs2_addr)}\n")
printf(p"wb_addr  : 0x${Hexadecimal(wb_addr)}\n")
printf(p"rs1_data : 0x${Hexadecimal(rs1_data)}\n")
printf(p"rs2_data : 0x${Hexadecimal(rs2_data)}\n")
printf("---------\n")

printf(p"wb_data  : 0x${Hexadecimal(wb_data)}\n")
printf(p"dmem.addr: ${io.dmem.addr}\n")
printf(p"dmem.wen  : ${io.dmem.wen}\n")
printf(p"dmem.wdata: 0x${Hexadecimal(io.dmem.wdata)}\n")

  // lw 加载数据到寄存器
  
}




