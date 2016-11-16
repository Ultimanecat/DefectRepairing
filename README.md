# DefectRepairing
Spectrum_based

make run RUNFLAG="-D directory -T tracefile"

make parser PARSERFLAG="tracefile1.txt tracefile2.txt"

Mutator：make mutate MUTATEFLAG="-F testfilename"


TODO:

完成framework
处理一下make入口仍然是test的问题（应该是framework）

test和parser两个类名字起的不好（要改）

test里面一个list泛型类型检查的warning调不过（也不是那么关键）


automatic patch apply&test

complete parser:
	consider filename
	consider function call&return
	ForStatement\Break
