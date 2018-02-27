## Requirement
Java 1.8
Python3
unidiff (Python3 package, install with pip)
defects4j 1.1

## Build
```
    cd source && make build(rebuild)
```
Modify defects4j with files in `defects4j-mod`.

## Usage
```
    python3 source/run.py [project] [bugid] [patch_no]
```
eg:
```
    python3 source/run.py Chart 1 Patch1
```
Some intermediate result will be stored in your disk, be sure you have adequete disk space. The patch must have been stored in `../patches` in unidiff format before running.
