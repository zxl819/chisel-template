file://<WORKSPACE>/src/main/scala/02_decode/core.scala
### java.lang.OutOfMemoryError: Java heap space

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.3
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.3/scala3-library_3-3.3.3.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.12/scala-library-2.13.12.jar [exists ]
Options:



action parameters:
offset: 1207
uri: file://<WORKSPACE>/src/main/scala/02_decode/core.scala
text:
```scala
package decode
import chisel3._
import common.Consts._
import common.Instructions._

class Core extends Module {
  val io = IO(new Bundle {
    val imem = Flipped(new ImemPortIo()) //指令接口
    val dmem = Flipped(new DmemPortIo()) //数据接口
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
//**********************************
  // Execute (EX) Stage
  val alu_out = MuxCase(0.U（@@)
//**********************************
//debug
io.exit := (inst === 0x34333231.U(WORD_LEN.W))
printf(p"pc_reg   : 0x${Hexadecimal(pc_reg)}\n")
printf(p"rs1_addr : 0x${Hexadecimal(rs1_addr)}\n")
printf(p"rs2_addr : 0x${Hexadecimal(rs2_addr)}\n")
printf(p"wb_addr  : 0x${Hexadecimal(wb_addr)}\n")
printf(p"rs1_data : 0x${Hexadecimal(rs1_data)}\n")
printf(p"rs2_data : 0x${Hexadecimal(rs2_data)}\n")
printf("---------\n")

  // lw 加载数据到寄存器
  
}





```



#### Error stacktrace:

```

```
#### Short summary: 

java.lang.OutOfMemoryError: Java heap space