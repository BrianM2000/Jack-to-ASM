//add.vm
//add
@SP
A=M
A=A-1
D=M
A=A-1
M=M+D
@SP
M=M-1
//sub.vm
//sub
@SP
A=M
A=A-1
D=M
A=A-1
M=M-D
@SP
M=M-1
//function name 1
(name)

0
D=A
@SP
A=M
M=D
@SP
M=M+1
//call name 2
@name
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
D=M
@7
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@name
0;JMP
(name)
