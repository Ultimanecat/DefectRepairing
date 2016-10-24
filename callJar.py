import os
import sys
#path="/home/akarin/Documents/DefectRepairing/math2f"
LIB_DIR="./lib/"
LIBS=LIB_DIR+"commons-collections-3.2.1.jar:"+LIB_DIR+"commons-cli-1.3.1.jar:"+LIB_DIR+"commons-cli-1.3.1-javadoc.jar:"+LIB_DIR+"commons-configuration-1.6.jar:"+LIB_DIR+"commons-lang-2.5.jar:"+LIB_DIR+"commons-logging-1.1.1.jar:"+LIB_DIR+"org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:"+LIB_DIR+"org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:"+LIB_DIR+"org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:"+LIB_DIR+"org.eclipse.core.runtime_3.5.0.v20090525.jar:"+LIB_DIR+"org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:"+LIB_DIR+"org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:"+LIB_DIR+"org.eclipse.jdt.core_3.5.2.v_981_R35x.jar:"+LIB_DIR+"org.eclipse.osgi_3.5.2.R35x_v20100126.jar"
ENTRY_POINT = "demo.test"
path=sys.argv[1]
Tracefile=sys.argv[2]
os.system("java -cp bin/:"+LIBS+" "+ENTRY_POINT +" -D "+path+" -T "+Tracefile)
