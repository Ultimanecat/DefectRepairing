# DefectRepairing
Spectrum_based

make run RUNFLAG="-D directory -T tracefile"

make parser PARSERFLAG="tracefile1.txt tracefile2.txt"

Mutator：make mutate MUTATEFLAG=""

输入是一个只包含一个方法的文件。

随机变异生成若干方法，输出到STDOUT。


TODO:

解决body部分为一条语句而不是一个block的if/for/while/do语句可能追踪不全的bug


automatic patch apply&test

complete parser:
	consider filename
	consider function call&return
	ForStatement\Break
