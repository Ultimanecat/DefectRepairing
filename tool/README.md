## Requirement
Java 1.7

Python3

unidiff (Python3 package)

```
pip3 install unidiff
```

defects4j 1.1

## Build
```
    cd source && make build(rebuild)
```

## Defects4j 
	Modify defects4j with files in `defects4j-mod`.
```
	cp -frap defects4j-mod/framework/ "PathtoDefects4j/"
```
## Usage
```
    cd source
    python3 run.py [project] [bugid] [patch_no]
```
e.g.:
```
    python3 run.py Chart 1 Patch1
```
Some intermediate result will be stored in your disk, so please make sure you have enough disk space available. The patches must have been stored in `../patches` in unidiff format before running.
