import os
import pylib.run_trace_randoop
import pylib.run_print_trace
import pylib.get_randoop_coverage
import pylib.get_test_coverage
import pylib.coverage
import pylib.tracer
import time
def checkout(project,bugid,patch_no):
    os.system('defects4j checkout -p '+project+' -v '+bugid+'b -w '+project+bugid+'b')
    if os.path.exists('./'+project+bugid+'b_'+patch_no):
        return
    os.system('cp -r '+project+bugid+'b'+' '+project+bugid+'b_bak')
    os.system('patch -u -p0 < ../patches/'+patch_no)
    os.system('mv '+project+bugid+'b '+project+bugid+'b_'+patch_no)
    os.system('mv '+project+bugid+'b_bak '+project+bugid+'b')

def gen_test_randoop(project,bug_id):
    if not os.path.exists('../test_gen_randoop/'+project+'/randoop/'+str(bug_id)):
        os.system('run_randoop.pl -p '+project+' -v '+str(bug_id)+'b -n '+str(bug_id)+' -o ../test_gen_randoop -b 420')

def trace(project,bugid,patch_no):
    if not os.path.exists('../randoop_cover'):
        os.system('mkdir ../randoop_cover')
    if not os.path.exists('../test_coverage'):
        os.system('mkdir ../test_coverage')
    if not os.path.exists('../traces'):
        os.system('mkdir ../traces')
    
    if not os.path.exists('../randoop_cover/'+project+bugid+'b_'+patch_no+".txt"):
        pylib.get_randoop_coverage.run(project,bugid,patch_no)    
    if not os.path.exists('../randoop_cover/'+project+bugid+'b_'+patch_no+".txt"):
        print('Warning: Randoop failed to generate tests')
        randoop_tests=[]
    else:
        randoop_tests=pylib.coverage.process_cover_trace('../randoop_cover/'+project+bugid+'b_'+patch_no+".txt",20)
    
    if not os.path.exists('../test_coverage/'+project+bugid+'b_'+patch_no+".txt"):
        pylib.get_test_coverage.run(project,bugid,patch_no)
    if not os.path.exists('../test_coverage/'+project+bugid+'b_'+patch_no+".txt"):
        print('error')
        return 1
    else:
        tests=set(list(pylib.coverage.get_trgr_tests(project,bugid))+list(pylib.coverage.process_cover_trace('../test_coverage/'+project+bugid+'b_'+patch_no+".txt")))
    
    pylib.tracer.run(project,bugid,patch_no,tests,randoop_tests)
    return 0

def extract_trace(project,bugid,patch_no):
    os.system('cd pylib && python3 call.py ../../traces/'+project+bugid+'b_'+patch_no)
def parse_trace(project,bugid,patch_no):
    os.system("mkdir "+patch_no)
    print('parsing traces.....................')
    if os.path.exists(os.path.join(patch_no,'LCS_array')):
        return
    val=os.system('timeout 3600 make parse ARGS="'+project+' '+bugid+' '+patch_no+' '+os.path.join(os.getcwd(),'../traces')+' '+os.path.join(os.getcwd(),'../patches')+' '+os.path.join(os.getcwd(),'pylib/projects/')+'" 2>/dev/null >/dev/null')
    if val!=0:
        print('error')
    return

def classify(patch_id):
    os.system('javac classifier.java 2>/dev/null')
    os.system('java classifier '+patch_id+'>'+patch_id+'/result')
    f=open(patch_id+'/result')
    res=f.readline().strip()
    f.close()
    return res

def run(project,bugid,patch_no):
    checkout(project,bugid,patch_no)
    gen_test_randoop(project,bugid)
    
    trace(project,bugid,patch_no)
    parse_trace(project,bugid,patch_no)
    print(classify(patch_no))
    os.system('rm -rf '+project+bugid+'b')
    os.system('rm -rf '+project+bugid+'b_'+patch_no)

import sys
if __name__ == '__main__':
    run(sys.argv[1],sys.argv[2],sys.argv[3])
