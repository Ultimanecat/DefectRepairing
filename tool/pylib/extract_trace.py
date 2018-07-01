import sys
f=open(sys.argv[1],'r')
patch_no=sys.argv[2]
#g=open('/Volumes/Unnamed/patched_function_old/'+patch_no,'r')
#method=g.readline().strip()
from unidiff import PatchSet
import os
def get_patched_class(patch_no):
    patchfile=os.path.join('../../patches',patch_no)
    patch = PatchSet.from_filename(patchfile,encoding='utf-8')
    source_file=patch[0].source_file
    #print(source_file)
    line_no_list=[]
    tmp_file='tmp_result'+patch_no
    for hunki in range(len(patch[0])):
        for i in range(len(patch[0][hunki])):
            if not patch[0][hunki][i].is_context:
                line_no_list.append(str(patch[0][hunki][i-1].source_line_no+1))
                break
    os.system('cd .. && make PatchInfo ARGS="'+os.path.join('../source/',source_file)+' '+tmp_file+' '+','.join(line_no_list)+'" >/dev/null')
    f=open('../'+tmp_file)
    res=f.readlines()[0].strip()
    f.close()
    os.system('rm ../'+tmp_file)
    return res
method=get_patched_class(patch_no)
traces=[]
trace=[]
stackdepth=0
isinTargetMethod=False
#print('<Method_invoked,'+method+'>')
for line in f:
    if line.startswith('<Method_invoked,'+method+'>'):
        stackdepth=1
        trace=[]
        traces.append(trace)
        trace.append(line)
        isinTargetMethod=True
        continue
    if not isinTargetMethod:
        continue
    trace.append(line)
    if line.startswith('<Method_invoked,'):
        stackdepth+=1
#        print(line.strip())
#        print(stackdepth)
#        input()
        continue
    if line.startswith('<ReturnStatement>'):
        stackdepth-=1
#        print(line.strip())
#        print(stackdepth)
#        input()
        if stackdepth == 0:
            isinTargetMethod=False
#print(len(traces))
for line in traces[0]:
    print(line.strip())
