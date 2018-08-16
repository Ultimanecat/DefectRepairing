import time
import os
from unidiff import PatchSet

btrace_home=os.path.abspath("./lib/btrace")

def run(project,bugid,patch_no,tests,randoop_tests=[],tmp_tracefile='tmp_c'):
    tmp_tracefile+=project+bugid+patch_no+'run_print_trace'
    tmp_tracefile=os.path.join(os.getcwd(),tmp_tracefile)
    w_buggy=project+str(bugid)+'b'
    w_patched=w_buggy+'_'+patch_no

    patchfile=os.path.join('../patches',patch_no)
    patch = PatchSet.from_filename(patchfile)

    source_file=patch[0].source_file
    target_file=patch[0].target_file
    line_no_list=[]
    for hunki in range(len(patch[0])):
        for i in range(len(patch[0][hunki])):
            if not patch[0][hunki][i].is_context:
                line_no_list.append(str(patch[0][hunki][i-1].source_line_no+1))
                break

    dir_path='../traces/'+w_patched
    if(os.path.exists(tmp_tracefile)):
        os.system('rm '+tmp_tracefile)
    os.system('mkdir '+dir_path)
    os.system('mkdir '+os.path.join(dir_path,'buggy'))
    os.system('mkdir '+os.path.join(dir_path,'patched'))

    patch_info_file="fdsa.txt"
    os.system("rm -rf "+patch_info_file)
    os.system('make PatchInfo ARGS="'+os.path.join('../source/',source_file)+' '+patch_info_file+' '+','.join(line_no_list)+'" >/dev/null')
    f=open(patch_info_file)
    patched_class=f.readlines()[-1].strip()
    f.close()

    f=open("%s/AllLines_pattern.java"%(btrace_home))
    s=f.read()
    f.close()
    s=s.replace('__CLASS__NAME__',patched_class)
    f=open("%s/AllLines.java"%(btrace_home),'w')
    f.write(s)
    f.close()
    os.system("cd %s && ./btracec AllLines.java"%(btrace_home))

    jvmargs=" -a -Djvmargs=\-javaagent:%s/btrace\-agent.jar=noserver,debug=true,scriptOutputFile=%s,script=%s/AllLines.class" % (btrace_home, tmp_tracefile, btrace_home)

    for test in tests:
        test=test.strip()
        
        status=os.system('timeout 90 defects4j test -t '+test+' -w '+w_buggy+jvmargs)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'buggy','__'.join(test.split('::'))))
        
        status=os.system('timeout 90 defects4j test -t '+test+' -w  '+w_patched+jvmargs)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'patched','__'.join(test.split('::'))))
    
    for Test_Case in randoop_tests:
        test='Randoop.'+Test_Case.strip()

        status=os.system('timeout 90 defects4j test -s '+testfile+' -t '+Test_Case.strip()+' -w '+w_buggy+jvmargs)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'buggy','__'.join(test.split('::'))))

        status=os.system('timeout 90 defects4j test -s '+testfile+' -t '+Test_Case.strip()+' -w '+w_patched+jvmargs)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'patched','__'.join(test.split('::'))))


