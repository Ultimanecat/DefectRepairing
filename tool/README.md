## Requirement
Java 1.7 (Please set JAVA_HOME before running the script)

Python3

unidiff (Python3 package)

```
pip3 install unidiff
```

defects4j 1.3 (Defects4JPath/framework/bin must be in $PATH)

## Check out the source code
[destinationPath] must contain no space
```
    git clone https://github.com/Ultimanecat/DefectRepairing.git [destinationPath]
    cd [destinationPath]/tool
```

## Build
```
    cd source && make build(rebuild)
```

## Defects4j 
Modify defects4j with files in `defects4j-mod`.
```
	cp -frap defects4j-mod/framework/ [PathtoDefects4j]/
```
## Usage
Run the tests
```
    cd source
    python3 run.py [project] [bugid] [patch_no]
```
e.g.:
```
    python3 run.py Chart 1 Patch1
```
Some intermediate result will be stored in your disk, so please make sure you have enough disk space available. The patches must have been stored in `../patches` in unidiff format before running.

Analyze the results
```
    Java classifier [patch_no]
```
The last line of the output indicates whether the patch is correct or incorrect.

## Troubleshooting
* The tool identifiers more patches as "incorrect" than reported in the ICSE'18 paper.

  One of the possible cause is the difference in hardware. The experiment was carried on a high-performance desktop PC. If you are running the experiment on a laptop or a PC with low-voltage CPU, the result will probably change. In such case, you may increase the timeout limit for Randoop, which is located in line 17 of run.py, and the original value is 180. Based on our experience, when the timeout limit is larger than a certain threshold, the output will be stable.
