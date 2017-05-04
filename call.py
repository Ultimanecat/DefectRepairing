import time
import sys

import os
f=open(sys.argv[1],'r')
for line in f:
    line=line.strip().split('_')
    os.system('make parse --silent ARGS="'+line[0]+' '+line[1]+' '+line[2]+'" ')
    #input()
    time.sleep(1)
