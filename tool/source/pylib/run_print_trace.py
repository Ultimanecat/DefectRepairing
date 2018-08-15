import time

import os

from unidiff import PatchSet
def get_path_to_test(path_to_project):
    f=open(os.path.join(path_to_project,'defects4j.build.properties'),'r')
    for line in f:
        if line.startswith('d4j.dir.src.tests'):
            line=line.strip()
            return line.split('=')[1]

btrace_home=os.path.abspath("./lib/btrace")

def run_btrace(project,bugid,patch_no,tests,tmp_tracefile='tmp_c'):
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
    
    f=open("%s/pattern.java"%(btrace_home))
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


def run(project,bugid,patch_no,tests,tmp_tracefile='tmp_c'):

    tmp_tracefile+=project+bugid+patch_no+'run_print_trace'
    tmp_tracefile=os.path.join(os.getcwd(),tmp_tracefile)
    w_buggy=project+str(bugid)+'b'
    w_patched=w_buggy+'_'+patch_no

    patchfile=os.path.join('../patches',patch_no)
    patch = PatchSet.from_filename(patchfile)
    
    source_file=patch[0].source_file
    target_file=patch[0].target_file
    
    os.system('cp '+source_file+' '+source_file+'.bak')
    os.system('cp '+target_file+' '+target_file+'.bak')

    os.system('make instru_class ARGS="-S '+source_file+' -T '+tmp_tracefile+'"')
    os.system('make instru_class ARGS="-S '+target_file+
              ' -T '+tmp_tracefile+' '+
              ' -P '+patchfile+
              ' -F '+target_file+'"')
    #
    dir_path='../traces/'+w_patched
    if(os.path.exists(tmp_tracefile)):
        os.system('rm '+tmp_tracefile)
    os.system('mkdir '+dir_path)
    os.system('mkdir '+os.path.join(dir_path,'buggy'))
    os.system('mkdir '+os.path.join(dir_path,'patched'))
    if project=='Time':
        os.system('defects4j compile -w '+w_buggy)
        os.system('defects4j compile -w '+w_patched)    
    # clone
    for test in tests:
        test=test.strip()
        testfile=os.path.join(w_buggy,get_path_to_test(w_buggy),test.split('::')[0].replace('.','/')+'.java')
        if project=='Time':
            os.system('rm '+tmp_tracefile)
            status=os.system('timeout 90 defects4j test -t '+test+' -w  '+w_buggy)
        else:
            os.system('cp '+testfile+' '+testfile+'.bak')
            os.system('make GetSingleTest_Chart ARGS="'+testfile+' '+test.split('::')[1]+'"')
            status=os.system('timeout 90 defects4j test -t '+test+' -w '+w_buggy)
            os.system('mv '+testfile+'.bak '+testfile)
        print(testfile)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'buggy','__'.join(test.split('::'))))
    
        testfile=os.path.join(w_patched,get_path_to_test(w_patched),test.split('::')[0].replace('.','/')+'.java')
        if project=='Time':
            os.system('rm '+tmp_tracefile)
            status=os.system('timeout 90 defects4j test -t '+test+' -w  '+w_patched)
        else:
            os.system('cp '+testfile+' '+testfile+'.bak')
            os.system('make GetSingleTest_Chart ARGS="'+testfile+' '+test.split('::')[1]+'"')
            status=os.system('timeout 90 defects4j test -t '+test+' -w '+w_patched)
            os.system('mv '+testfile+'.bak '+testfile)        
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'patched','__'.join(test.split('::'))))
    # clone
    os.system('mv '+source_file+'.bak '+source_file)
    os.system('mv '+target_file+'.bak '+target_file)

