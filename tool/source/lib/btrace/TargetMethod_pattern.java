import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

@BTrace
public class TargetMethod {
    @OnMethod(
        clazz="__CLASS__NAME__",
        method="__METHOD__NAME__",
        type="__SIGNATURE__",
        location=@Location(value=Kind.LINE, line=-1)
    )
    public static void online(@ProbeClassName String pcn, @ProbeMethodName String pmn, int line) {
        print("---" + pcn + "." + pmn +  ":" + line + "\n");
    }
    
}
