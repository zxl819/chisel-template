file://<WORKSPACE>/src/main/scala/02_decode/core.scala
### java.lang.OutOfMemoryError: Java heap space

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.3
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.3/scala3-library_3-3.3.3.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.12/scala-library-2.13.12.jar [exists ]
Options:



action parameters:
offset: 4636
uri: file://<WORKSPACE>/src/main/scala/02_decode/core.scala
text:
```scala

package decodemore

import chisel3._
import chisel3.util._
import common.Instructions._
import common.Consts._

class Core extends Module {
  val io = IO(new Bundle {
    val imem = Flipped(new ImemPortIo())
    val dmem = Flipped(new DmemPortIo())
    val exit = Output(Bool())
  })

  val regfile = Mem(32, UInt(WORD_LEN.W))


  //**********************************
  // Instruction Fetch (IF) Stage
  
  val pc_reg = RegInit(START_ADDR)
  io.imem.addr := pc_reg
  val inst = io.imem.inst
  pc_reg := pc_reg + 4.U(WORD_LEN.W)


  //**********************************
  // Instruction Decode (ID) Stage

  val rs1_addr = inst(19, 15)
  val rs2_addr = inst(24, 20)
  val wb_addr  = inst(11, 7)
  val rs1_data = Mux((rs1_addr =/= 0.U(WORD_LEN.U)), regfile(rs1_addr), 0.U(WORD_LEN.W))
  val rs2_data = Mux((rs2_addr =/= 0.U(WORD_LEN.U)), regfile(rs2_addr), 0.U(WORD_LEN.W))
  
  val imm_i = inst(31, 20)
  val imm_i_sext = Cat(Fill(20, imm_i(11)), imm_i)
  val imm_s = Cat(inst(31, 25), inst(11, 7))
  val imm_s_sext = Cat(Fill(20, imm_s(11)), imm_s)
  val imm_b = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8))
  val imm_b_sext = Cat(Fill(19, imm_b(11)), imm_b, 0.U(1.U))
  val imm_j = Cat(inst(31), inst(19, 12), inst(20), inst(30, 21))
  val imm_j_sext = Cat(Fill(11, imm_j(19)), imm_j, 0.U(1.U))


  val csignals = ListLookup(inst,
              List(ALU_X,   OP1_RS1, OP2_RS2, MEN_X,  REN_X, WB_X  ),
    Array(
      LW   -> List(ALU_ADD, OP1_RS1, OP2_IMI, MEN_X , REN_S, WB_MEM),
      SW   -> List(ALU_ADD, OP1_RS1, OP2_IMS, MEN_S,  REN_X, WB_X  ),
      ADD  -> List(ALU_ADD, OP1_RS1, OP2_RS2, MEN_X , REN_S, WB_ALU),
      ADDI -> List(ALU_ADD, OP1_RS1, OP2_IMI, MEN_X , REN_S, WB_ALU),
      SUB  -> List(ALU_SUB, OP1_RS1, OP2_RS2, MEN_X , REN_S, WB_ALU),
      AND  -> List(ALU_AND, OP1_RS1, OP2_RS2, MEN_X , REN_S, WB_ALU),
      OR   -> List(ALU_OR , OP1_RS1, OP2_RS2, MEN_X , REN_S, WB_ALU),
      XOR  -> List(ALU_XOR, OP1_RS1, OP2_RS2, MEN_X , REN_S, WB_ALU),
      ANDI -> List(ALU_AND, OP1_RS1, OP2_IMI, MEN_X , REN_S, WB_ALU),
      ORI  -> List(ALU_OR , OP1_RS1, OP2_IMI, MEN_X , REN_S, WB_ALU),
      XORI -> List(ALU_XOR, OP1_RS1, OP2_IMI, MEN_X , REN_S, WB_ALU),
      SLL  -> List(ALU_SLL, OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU),
      SRL  -> List(ALU_SRL, OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU),
      SRA  -> List(ALU_SRA, OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU),
      SLLI -> List(ALU_SLL, OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU),
      SRLI -> List(ALU_SRL, OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU),
      SRAI -> List(ALU_SRA, OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU),
      // 以下追加
      SLT   -> List(ALU_SLT , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU),
      SLTU  -> List(ALU_SLTU, OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU),
      SLTI  -> List(ALU_SLT , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU),
      SLTIU -> List(ALU_SLTU, OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU),
      BEQ   -> List(BR_BEQ  , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  ),
      BNE   -> List(BR_BNE  , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  ),
      BGE   -> List(BR_BGE  , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  ),
      BGEU  -> List(BR_BGEU , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  ),
      BLT   -> List(BR_BLT  , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  ),
      BLTU  -> List(BR_BLTU , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  ),
            // 以下追加
      JAL   -> List(ALU_ADD  , OP1_PC , OP2_IMJ, MEN_X, REN_S, WB_PC ),
      JALR  -> List(ALU_JALR , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_PC ),

    )
  )
  val exe_fun :: op1_sel :: op2_sel :: mem_wen :: rf_wen :: wb_sel :: Nil = csignals

  val op1_data = MuxCase(0.U(WORD_LEN.W), Seq(
    (op1_sel === OP1_RS1) -> rs1_data,
  ))

  val op2_data = MuxCase(0.U(WORD_LEN.W), Seq(
    (op2_sel === OP2_RS2) -> rs2_data,
    (op2_sel === OP2_IMI) -> imm_i_sext,
    (op2_sel === OP2_IMS) -> imm_s_sext,
    (op2_sel === OP2_IMJ) -> imm_j_sext
  ))


  //**********************************
  // Execute (EX) Stage

  val alu_out = MuxCase(0.U(WORD_LEN.W), Seq(
    (exe_fun === ALU_ADD) -> (op1_data + op2_data),
    (exe_fun === ALU_SUB) -> (op1_data - op2_data),
    (exe_fun === ALU_AND) -> (op1_data & op2_data),
    (exe_fun === ALU_OR)  -> (op1_data | op2_data),
    (exe_fun === ALU_XOR) -> (op1_data ^ op2_data),
        // shift
    (exe_fun === ALU_SLL) -> (op1_data << op2_data(4, 0))(31, 0),
    (exe_fun === ALU_SRL) -> (op1_data >> op2_data(4, 0)).asUInt(),
    (exe_fun === ALU_SRA) -> (op1_data.asSInt() >> op2_data(4, 0)).asUInt(),
      (exe_fun === ALU_SLT)  -> (op1_data.asSInt() < op2_data.asSInt()).asUInt(),
    (exe_fun === ALU_SLTU) -> (op1_data < op2_data).asUInt()，@@
  ))
// branch
  br_flg := MuxCase(false.B, Seq(
    (exe_fun === BR_BEQ)  ->  (op1_data === op2_data),
    (exe_fun === BR_BNE)  -> !(op1_data === op2_data),
    (exe_fun === BR_BLT)  ->  (op1_data.asSInt() < op2_data.asSInt()),
    (exe_fun === BR_BGE)  -> !(op1_data.asSInt() < op2_data.asSInt()),
    (exe_fun === BR_BLTU) ->  (op1_data < op2_data),
    (exe_fun === BR_BGEU) -> !(op1_data < op2_data)
  ))
  br_target := pc_reg + imm_b_sext


  //**********************************
  // Memory Access Stage

  io.dmem.addr := alu_out
  io.dmem.wen := Mux(mem_wen === MEN_S, 1.U(MEN_LEN.W), 0.U(MEN_LEN.W))
  io.dmem.wdata := rs2_data


  //**********************************
  // Writeback (WB) Stage
  
  val wb_data = MuxCase(alu_out, Seq(
    (wb_sel === WB_MEM) -> io.dmem.rdata,
  ))
  when(rf_wen === REN_S) {
    regfile(wb_addr) := wb_data
  }


  //**********************************
  // Debug
  io.exit := (inst === 0xc0001073L.U(WORD_LEN.W))
  printf(p"pc_reg     : 0x${Hexadecimal(pc_reg)}\n")
  printf(p"inst       : 0x${Hexadecimal(inst)}\n")
  printf(p"wb_addr    : wb_addr\n")
  printf(p"rs1_addr   : rs1_addr\n")
  printf(p"rs2_addr   : rs2_addr\n")
  printf(p"rs1_data   : 0x${Hexadecimal(rs1_data)}=${rs1_data}\n")
  printf(p"rs2_data   : 0x${Hexadecimal(rs2_data)}=${rs2_data}\n")
  printf(p"wb_data    : 0x${Hexadecimal(wb_data)}\n")
  printf(p"dmem.addr  : io.dmem.addr\n")
  printf(p"dmem.rdata : ${io.dmem.rdata}\n")
  printf("---------\n")
}
// package riscvtests
// import chisel3._
// import chisel3.util._
// import common.Consts._
// import common.Instructions._

// class Core extends Module {
//   val io = IO(new Bundle {
//     val imem = Flipped(new ImemPortIo()) //指令接口
//     val dmem = Flipped(new DmemPortIo()) //DmemPortIo数据接口
//     val exit = Output(Bool())
//     val gp   = Output(UInt(WORD_LEN.W))
//   })

//   val regfile = Mem(32, UInt(WORD_LEN.W))


//   //**********************************
//   // Instruction Fetch (IF) Stage

//   val pc_reg = RegInit(START_ADDR)
//   pc_reg := pc_reg + 4.U(WORD_LEN.W)
//   io.imem.addr := pc_reg
//   val inst = io.imem.inst

//   //**********************************
//   // Instruction Decode (ID) Stage
// val rs1_addr = inst(19, 15) //r1寄存器的编号为指令列15～19位
// val rs2_addr = inst(24, 20) //r2寄存器的编号为指令列20～24位
// val wb_addr  = inst(11, 7) // rd寄存器的编号为指令列7～11位

// val rs1_data = Mux((rs1_addr =/= 0.U(WORD_LEN.W)), regfile(rs1_addr), 0.U(WORD_LEN.W)) //如果rs1_addr不为0，则将rs1_data赋值为regfile(rs1_addr)，否则赋值为0
// val rs2_data = Mux((rs2_addr =/= 0.U(WORD_LEN.W)), regfile(rs2_addr), 0.U(WORD_LEN.W)) //如果rs2_addr不为0，则将rs2_data赋值为regfile(rs2_addr)，否则赋值为0
//    //ID内部offset符号拓展
//    val imm_i = inst(31,20) //offset[11:0]
//    val imm_i_sext = Cat(Fill(20,imm_i(11)),imm_i) //offset符号拓展
//    // sw 指令的s格式指令的立即数imm_S的译码处理
//    val imm_s = Cat(inst(31,25),inst(11,7))
//    val imm_s_sext = Cat(Fill(20,imm_s(11)),imm_s) //用imm_S高位补全高20位
// //**********************************
//   // Execute (EX) Stage
//   val alu_out = MuxCase(0.U(WORD_LEN.W),Seq(
//     (inst === LW || inst === ADDI) ->(rs1_data + imm_i_sext), //存储器地址的计算
//     (inst === SW) ->(rs1_data + imm_s_sext),
//     (inst === ADD) ->(rs1_data + rs2_data),
//     (inst === SUB) ->(rs1_data - rs2_data)
//     (inst === AND) ->(rs1_data & rs2_data),
//     (inst === OR) ->(rs1_data | rs2_data),
//     (inst === XOR) ->(rs1_data ^ rs2_data),
//     (inst === ANDI) ->(rs1_data & imm_i_sext),
//     (inst === ORI) ->(rs1_data | imm_i_sext),
//     (inst === XORI) ->(rs1_data ^ imm_i_sext)

//   ))
//   //**********************************
//   // Memory Access (MEM) Stage
//   io.dmem.addr := alu_out //将EX阶段计算出的存储器地址链接到MEM阶段的存储器端口
//   io.dmem.wen := (inst === SW)
//   io.dmem.wdata := rs2_data

//   // when(inst === LW){  //存储器的地址可以始终输出给存储器
//   //   io.dmem.addr := alu_out
//   // }
// //**********************************
// // Write Back (WB) Stage
// val wb_data = MuxCase(alu_out,Seq(
//   (inst === LW) -> Io.dmem.rdata
// )) //将存储器中的数据输出给WB阶段
// when(inst === LW || inst === ADD || inst === ADDI || inst === SUB || inst === AND || inst === OR || inst === XOR || inst === ANDI || inst === ORI || inst === XORI){
//   regfile(wb_addr) := wb_data
// }
// //**********************************
// //debug
// io.exit := (inst === 0x00602823.U(WORD_LEN.W))
// printf(p"pc_reg   : 0x${Hexadecimal(pc_reg)}\n")
// printf(p"rs1_addr : 0x${Hexadecimal(rs1_addr)}\n")
// printf(p"rs2_addr : 0x${Hexadecimal(rs2_addr)}\n")
// printf(p"wb_addr  : 0x${Hexadecimal(wb_addr)}\n")
// printf(p"rs1_data : 0x${Hexadecimal(rs1_data)}\n")
// printf(p"rs2_data : 0x${Hexadecimal(rs2_data)}\n")
// printf("---------\n")

// printf(p"wb_data  : 0x${Hexadecimal(wb_data)}\n")
// printf(p"dmem.addr: ${io.dmem.addr}\n")
// printf(p"dmem.wen  : ${io.dmem.wen}\n")
// printf(p"dmem.wdata: 0x${Hexadecimal(io.dmem.wdata)}\n")

//   // lw 加载数据到寄存器
  
// }





```



#### Error stacktrace:

```

```
#### Short summary: 

java.lang.OutOfMemoryError: Java heap space