# DefectRepairing
Spectrum_based

make run RUNFLAG="-D directory -T tracefile"
make parser PARSERFLAG="tracefile1.txt tracefile2.txt"

Object的赋值输出为字符串"Object"

未追踪constructor

未追踪final method

TODO:

解决body部分为一条语句而不是一个block的if/for/while/do语句可能追踪不全的bug

追踪constructor的传参

automatic patch apply&test
testcase mutation

complete parser:
	consider filename
	consider function call&return
	ForStatement\Break
