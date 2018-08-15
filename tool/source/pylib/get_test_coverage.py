import os
import time
from unidiff import PatchSet
def get_path_to_source(path_to_project):
    f=open(os.path.join(path_to_project,'defects4j.build.properties'),'r')
    for line in f:
        if line.startswith('d4j.dir.src.classes'):
            line=line.strip()
            return line.split('=')[1]

def get_path_to_test(path_to_project):
    f=open(os.path.join(path_to_project,'defects4j.build.properties'),'r')
    for line in f:
        if line.startswith('d4j.dir.src.tests'):
            line=line.strip()
            return line.split('=')[1]

def run(project,bugid,patch_no,tmp_tracefile='tmp_b'):
        w_buggy=project+bugid+'b'
        test='randoop'
        
        tmp_tracefile+=project+bugid+patch_no+'get_test_coverage'
        tmp_tracefile=os.path.join(os.getcwd(),tmp_tracefile)
        #
        testdir=os.path.join(w_buggy,get_path_to_test(w_buggy))
        os.system('cp -r '+testdir+' '+testdir+'_bak')
        
        
        os.system('make TestCaseInstr ARGS="'+testdir+' '+tmp_tracefile+' '+project+'"')
        
        print(w_buggy+'_'+patch_no)
        #
        patch = PatchSet.from_filename('../patches/'+patch_no)
        souce_file_list=[]
        for filei in range(len(patch)):
            source_file=patch[filei].source_file
            souce_file_list.append(source_file)
            line_no_list=[]
            for hunki in range(len(patch[filei])):
                for i in range(len(patch[filei][hunki])):
                    if not patch[filei][hunki][i].is_context:
                        line_no_list.append(str(patch[filei][hunki][i-1].source_line_no+1))
                        break
            os.system('cp '+source_file+' '+source_file+'.bak')
            os.system('make MthdInstr ARGS="'+source_file+' '+tmp_tracefile+' '+','.join(line_no_list)+'"')

        os.system('defects4j compile -w '+w_buggy)
        if(os.path.exists(tmp_tracefile)):
            os.system('rm '+tmp_tracefile)
        os.system('defects4j test -n -r -w '+w_buggy)
        os.system('mv '+tmp_tracefile+' ../test_coverage/'+w_buggy+'_'+patch_no+'.txt')
        for source_file in souce_file_list:
            os.system('rm '+source_file)
            os.system('mv '+source_file+'.bak '+source_file)
        os.system('rm -rf '+testdir)
        os.system('mv '+testdir+'_bak '+testdir)
        
        os.system('rm -rf '+w_buggy)
        os.system('defects4j checkout -p '+project+' -v '+bugid+'b -w '+project+bugid+'b')
