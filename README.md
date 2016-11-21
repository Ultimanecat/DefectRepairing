# DefectRepairing
Spectrum_based

make run RUNFLAG="-D directory -T tracefile"

make parser PARSERFLAG="tracefile1.txt tracefile2.txt"

改了一些名字，简单改了改makefile，make build应该可以用，其他的没有试

现在的插装工具需要先预处理行号

测试用例变异改成在原来文件的地方变异，输入是文件名和方法名，没处理同名方法（感觉似乎没必要？）

正在给defects4j写API（根目录下config文件里改一下defects4j的路径，这个文件在gitignore里）刚写好一个获取failing tests的函数

改了之前一直没改的一个小bug，不知道会不会引起新的bug。。。

TODO:

defects4j的API需要哪些？目前来看需要checkout、compile、test（这几个都是执行个命令行语句的事情），另外应该就是checkout之后的src dir和test dir。

完成framework

parser类名字起的不好（要改）



automatic patch apply&test

complete parser:

	consider filename

	consider function call&return

	ForStatement\Break

    LCS(need debugging) O(n2)

    LCS_bestfit(need dubugging) O(n3) using BFS instead of DP may get better performance
