import os
import sys
path=sys.argv[1]
tmp_file='tmp'+path.split('/')[-1]
os.system('ls '+os.path.join(path,'buggy')+">"+tmp_file )
f=open(tmp_file,'r')
os.system('mkdir '+os.path.join(path,'buggy_e'))

for line in f:
    line=line.strip()
    os.system('python3 extract_trace.py '+os.path.join(path,'buggy',line)+' '+path.split('_')[1]+' > '+os.path.join(path,'buggy_e/',line))
f.close()

os.system('ls '+os.path.join(path,'patched')+">"+tmp_file )
f=open(tmp_file,'r')
os.system('mkdir '+os.path.join(path,'patched_e'))
for line in f:
    line=line.strip()
    os.system('python3 extract_trace.py '+os.path.join(path,'patched',line)+' '+path.split('_')[1]+' > '+os.path.join(path,'patched_e/',line))
os.system('rm '+tmp_file)
