import random
random.seed(1737)
def get_tests(filepath):
    tests=[]
    test_f=open(filepath,'r')
    for tline in test_f:
        tests.append('::'.join(tline.strip().split(':')))
    return tests
def get_trgr_tests(project,bugid):
    f=open('pylib/projects/'+project+'/trigger_tests/'+bugid)
    l=[]
    for line in f:
        if line.startswith('---'):
            l.append(line.strip()[4:])
    return l
def process_cover_trace(tracefile,shuffle=0,add_fail_test=False):
    test_set=set()
    f=open(tracefile,'r')
    tmp_line=" "
    for line in f:
        line=line.strip()
        if line == '---covered':
            if tmp_line!=" ":
                test_set.add(tmp_line)
        else:
            tmp_line=line
    tests=[]
    for line in test_set:
        if line.startswith('---'):
            line=line[3:]
        tests.append('::'.join(line.split(':')))
    if shuffle!=0:
        random.shuffle(tests)
        tests=tests[:shuffle]
    return tests
