val rs1_addr = inst(19, 15) //r1寄存器的编号为指令列15～19位
val rs2_addr = inst(24, 20) //r2寄存器的编号为指令列20～24位
val wb_addr  = inst(11, 7) // rd寄存器的编号为指令列7～11位

val rs1_data = Mux((rs1_addr =/= 0.U), regfile(rs1_addr), 0.U(WORD_LEN.W)) //如果rs1_addr不为0，则将rs1_data赋值为regfile(rs1_addr)，否则赋值为0
val rs2_data = Mux((rs2_addr =/= 0.U), regfile(rs2_addr), 0.U(WORD_LEN.W)) //如果rs2_addr不为0，则将rs2_data赋值为regfile(rs2_addr)，否则赋值为0

//debug
printf(p"rs1_addr : 0x${Hexadecimal(rs1_addr)}\n")
printf(p"rs2_addr : 0x${Hexadecimal(rs2_addr)}\n")
printf(p"wb_addr  : 0x${Hexadecimal(wb_addr)}\n")
printf(p"rs1_data : 0x${Hexadecimal(rs1_data)}\n")
printf(p"rs2_data : 0x${Hexadecimal(rs2_data)}\n")