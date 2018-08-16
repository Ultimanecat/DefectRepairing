# Set the file name of your jar package:
JAR_PKG = DefectRepairing.Instrumenter.jar
# Set your entry point of your java app:
ENTRY_POINT = DefectRepairing.Instrumenter # should change to Framework 
TEST = DefectRepairing.Instrumenter
PARSER = DefectRepairing.parser
TESTCASEINSTR = TestCase.Instrumenter

LIB_DIR = ./lib/
LIBS = $(LIB_DIR)commons-collections-3.2.1.jar:$(LIB_DIR)commons-cli-1.3.1.jar:$(LIB_DIR)commons-cli-1.3.1-javadoc.jar:$(LIB_DIR)commons-configuration-1.6.jar:$(LIB_DIR)commons-lang-2.5.jar:$(LIB_DIR)commons-logging-1.1.1.jar:$(LIB_DIR)org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:$(LIB_DIR)org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:$(LIB_DIR)org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:$(LIB_DIR)org.eclipse.core.runtime_3.5.0.v20090525.jar:$(LIB_DIR)org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:$(LIB_DIR)org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:$(LIB_DIR)org.eclipse.jdt.core_3.5.2.v_981_R35x.jar:$(LIB_DIR)org.eclipse.osgi_3.5.2.R35x_v20100126.jar:$(LIB_DIR)jdom-2.0.5.jar:$(LIB_DIR)diffparser.jar:$(LIB_DIR)slf4j-api-1.7.5.jar

TESTFLAG=
RUNFLAG = 
PARSERFLAG=
MUTATEFLAG=
RES_DIR = no
SOURCE_FILES = \
DefectRepairing/jPickle.java \
TestCase/MethodInstr.java \
DefectRepairing/LineNumber.java \
TestCase/patchparser.java \
DefectRepairing/parser.java \
DefectRepairing/Instrumenter.java TestCase/Instrumenter.java \
DefectRepairing/LineNumberPreProcessor.java \
TestCase/GetSingleTest_Chart.java \
TestCase/listTestCase.java \
TestCase/classifier.java \
TestCase/MethodName.java \
TestCase/TraceParser.java

# 设置你的java编译器
# Set your java compiler here:
JAVAC = javac
# 设置你的编译选项
JFLAGS = -encoding UTF-8 -cp bin/:$(LIBS) 


# 用法：
# make new: 在你的工程目录下生成src, bin, res子目录。
# 如果你定义的类包含在某个包里：请自己在src下建立相应的目录层次。
# 最终的目录结构如下：
# ├── a.jar
# ├── bin
# │     └── test
# │             ├── A.class
# │             └── B.class
# ├── makefile
# ├── res
# │     └── doc
# │            └── readme.txt
# └── src
#        └── test
#                ├── A.java
#                └── B.java

# make build: 编译，在bin目录下生成 java classes。
# make clean: 清理编译结果，以便重新编译
# make rebuild: 清理编译结果，重新编译。
# make run: make 之后，可以通过make run查看运行结果。
# make jar: 生成可执行的jar包。

#############下面的内容建议不要修改####################

vpath %.class bin
vpath %.java src

# show help message by default
Default:
	@echo "make new: new project, create src, bin, res dirs."
	@echo "make build: build project."
	@echo "make clean: clear classes generated."
	@echo "make rebuild: rebuild project."
	@echo "make run: run your app."
	@echo "make jar: package your project into a executable jar."

build: $(SOURCE_FILES:.java=.class)

# pattern rule
# 不能处理两个类互相引用的情况，尽量避免
%.class: %.java
	$(JAVAC) -d bin $(JFLAGS) $<

rebuild: clean build

.PHONY: new clean run jar parser mutate test

new:
ifeq ($(RES_DIR),yes)
	mkdir -pv src bin res
else
	mkdir -pv src bin
endif

clean:
	rm -frv bin/*

run:
	java -cp bin/:$(LIBS) $(ENTRY_POINT) $(RUNFLAG)

jar:
ifeq ($(RES_DIR),yes)
	jar cvfe $(JAR_PKG) $(ENTRY_POINT)  -C bin . res
else
	jar cvfe $(JAR_PKG) $(ENTRY_POINT) -C bin .
endif

test:
	java -cp bin/:$(LIBS) $(TEST) $(TESTFLAG)
parser:
	java -cp bin/:$(LIBS) $(PARSER) $(PARSERFLAG)
TestCaseInstr:
	java -cp bin/:$(LIBS) $(TESTCASEINSTR) $(ARGS)
GetSingleTest_Chart:
	java -cp bin/:$(LIBS) TestCase.GetSingleTest_Chart $(ARGS)
instru_class:
	java -cp bin/:$(LIBS) DefectRepairing.Instrumenter $(ARGS)
listTestCase:
	java -cp bin/:$(LIBS) TestCase.listTestCase $(ARGS)
parse:
	java -Xms32m -Xmx8192m -cp bin/:$(LIBS) TestCase.TraceParser $(ARGS)
MthdInstr:
	java -cp bin/:$(LIBS) TestCase.MethodInstr $(ARGS)
PatchInfo:
	java -cp bin/:$(LIBS) TestCase.MethodName $(ARGS)
