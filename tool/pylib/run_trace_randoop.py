import os
from unidiff import PatchSet
def get_path_to_test(path_to_project):
    f=open(os.path.join(path_to_project,'defects4j.build.properties'),'r')
    for line in f:
        if line.startswith('d4j.dir.src.tests'):
            line=line.strip()
            return line.split('=')[1]

def run(project,bugid,patch_no,randoop_tests,tmp_tracefile='tmp_d'):
    tmp_tracefile+=project+bugid+patch_no+'run_trace_randoop.py'
    tmp_tracefile=os.path.join(os.getcwd(),tmp_tracefile)
    w_buggy=project+str(bugid)+'b'
    w_patched=w_buggy+'_'+patch_no
    #
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

    test='randoop'
    #
    testfile='../test_gen_randoop/'+project+'/randoop/'+bugid+'/'+project+'-'+bugid+'b-randoop.'+bugid+'.tar.bz2'
    
    comp_flag=True
    for Test_Case in randoop_tests:
        test='Randoop.'+Test_Case.strip()
        if comp_flag:
            status=os.system('timeout 90 defects4j test -s '+testfile+' -t '+Test_Case.strip()+' -w '+w_buggy)
        else:
            status=os.system('timeout 90 defects4j test -s '+testfile+' -t '+Test_Case.strip()+' -n -w '+w_buggy)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'buggy','__'.join(test.split('::'))))
        
        if comp_flag:
            status=os.system('timeout 90 defects4j test -s '+testfile+' -t '+Test_Case.strip()+' -w '+w_patched)
        else:
            status=os.system('timeout 90 defects4j test -s '+testfile+' -t '+Test_Case.strip()+' -n -w '+w_patched)
        if status==0:
            os.system('mv '+tmp_tracefile+' '+os.path.join(dir_path,'patched','__'.join(test.split('::'))))
        comp_flag=False
    
    os.system('mv '+source_file+'.bak '+source_file)
    os.system('mv '+target_file+'.bak '+target_file)
