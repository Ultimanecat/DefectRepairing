# A general java project makefile
# Author: Wu Jiqing (jiqingwu@gmail.com)
# create: 2012-06-12
# update: 2012-06-13
# version: 0.7

# 设置你要生成的jar包的文件名
# Set the file name of your jar package:
JAR_PKG = demo.test.jar
# 设置你的项目的入口点
# Set your entry point of your java app:
ENTRY_POINT = demo.test
PARSER = demo.parser
MUTATE = mutator.MutateTest

LIB_DIR = ./lib/
LIBS = $(LIB_DIR)commons-collections-3.2.1.jar:$(LIB_DIR)commons-cli-1.3.1.jar:$(LIB_DIR)commons-cli-1.3.1-javadoc.jar:$(LIB_DIR)commons-configuration-1.6.jar:$(LIB_DIR)commons-lang-2.5.jar:$(LIB_DIR)commons-logging-1.1.1.jar:$(LIB_DIR)org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:$(LIB_DIR)org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:$(LIB_DIR)org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:$(LIB_DIR)org.eclipse.core.runtime_3.5.0.v20090525.jar:$(LIB_DIR)org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:$(LIB_DIR)org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:$(LIB_DIR)org.eclipse.jdt.core_3.5.2.v_981_R35x.jar:$(LIB_DIR)org.eclipse.osgi_3.5.2.R35x_v20100126.jar

RUNFLAG = 
PARSERFLAG=
# 是否需要res目录，如果你的程序有图片、文档等，
# 最好放入res目录中。
# yes: 需要；no：不需要
RES_DIR = no
# 设置你项目包含的源文件
# 如果你使用了package，请自己在src下建立相应的目录层次，
# 并将源文件放在对应的目录中。
# 如你要生成的一个类是 com.game.A，
# 那么你的源文件应该是 com/game/A.java。
# 多个类之间用空格间隔，如果一行太长，用'\'换行，
# 建议一行一个。
# 另外注意顺序，如果class A 引用 class B，那么B.java应该放在A.java前。
SOURCE_FILES = \
demo/test.java demo/parser.java \
mutator/MutateOperator.java mutator/MutateTest.java \

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

.PHONY: new clean run jar parser mutate

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

parser:
	java -cp bin/:$(LIBS) $(PARSER) $(PARSERFLAG)
mutate:
	java -cp bin/:$(LIBS) $(MUTATE) $(MUTATEFLAG)
