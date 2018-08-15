import os
import time
from unidiff import PatchSet
def get_path_to_source(path_to_project):
    f=open(os.path.join(path_to_project,'defects4j.build.properties'),'r')
    for line in f:
        if line.startswith('d4j.dir.src.classes'):
            line=line.strip()
            return line.split('=')[1]
#f=open('readme.md','r')
def run(project,bugid,patch_no,tmp_tracefile='tmp_a'):
        #v=line[2:].split(' ')
        #project=v[0]
        #bugid=v[1]
        tmp_tracefile+=project+bugid+patch_no+'get_randoop_coverage'
        tmp_tracefile=os.path.join(os.getcwd(),tmp_tracefile)
        w_buggy=project+bugid+'b'
        test='randoop'
        #
        testfiledir='../test_gen_randoop/'+project+'/randoop/'+bugid+'/'
        targetfile=testfiledir+project+'-'+bugid+'b-randoop.'+bugid+'.instr.tar.bz2'
        testfile=testfiledir+project+'-'+bugid+'b-randoop.'+bugid+'.tar.bz2'
        targetdir=testfiledir+'suite'
        os.system('mkdir '+targetdir)
        os.system('tar xvf '+testfile+' -C '+targetdir)
        os.system('make TestCaseInstr ARGS="'+targetdir+' '+tmp_tracefile+' Randoop"')
        os.system('cd '+targetdir+' && tar -c ./* | bzip2 > ../'+project+'-'+bugid+'b-randoop.'+bugid+'.instr.tar.bz2')
        #patch_no=line[3:].split(' ')[1][1:]
        #print(patch_no)

        #os.system('cp -r '+v+' '+v+'_Patch'+line[3:].split(' ')[1][1:])
        #print(line)
        #if line[0]=='[':
        #    bug_loc=line.split(']')[0][1:]
        #else :
        #    bug_loc=line.split(' (')[0]
        #bug_loc=bug_loc.split/(':')
        #bug_loc=bug_loc[0].replace('.','/')+'.java:'+bug_loc[1]
        print(w_buggy+'_'+patch_no)
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
        if(os.path.exists(tmp_tracefile)):
            os.system('rm '+tmp_tracefile)
        os.system('defects4j test -s '+targetfile+' -w '+w_buggy)
        os.system('mv '+tmp_tracefile+' ../randoop_cover/'+w_buggy+'_'+patch_no+'.txt')
        for source_file in souce_file_list:
            os.system('rm '+source_file)
            os.system('mv '+source_file+'.bak '+source_file)
        os.system('rm -rf '+w_buggy)
        os.system('defects4j checkout -p '+project+' -v '+bugid+'b -w '+project+bugid+'b')
