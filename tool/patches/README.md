All of our patches are in Unidiff format.

`./INFO` contains JSON file of the information of each patch.

Each JSON file contains such informations of a patch:
```
    ID: Patch ID
    bug_id: Bug ID in Defects4J
    project: Project Name
    tool: The automatic program repair tool
    correctness: Patch correctness labeled by human
```

To generate such patch file for our tool, use the command:
```
    diff -r -u -w [path_to_source_of_buggy_version] [path_to_source_of_patch_version]
```

To checkout a patched version of a patch, simply use the `checkout` function in `run.py` or use the commands like below:
```
    defects4j checkout -p Chart -v 1b -w ./Chart1b
    patch -u -p0 --input=[PatchFile]
```


## Sources of Data

Nopol2015, jGenprog, jKali: https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-may (Patch)
                            [Automatic Repair of Real Bugs: An Experience Report on the Defects4J Dataset](http://arxiv.org/pdf/1505.07002) (Label)

Nopol2017: [The Patches of the Nopol Automatic Repair System on the Bugs of Defects4J version 1.1.0](https://hal.archives-ouvertes.fr/hal-01480084) (Patch) (The correctness labels were judged by us.)


ACS: [ACS: Accurate Condition Synthesis](https://github.com/Adobee/ACS/blob/master/README.md#v-evaluation) (Patch and Label)

HDRepair: [DiffTGen](https://github.com/qixin5/DiffTGen/tree/master/expt0/dataset) (Patch and Label)

## Patch Correctness

Note that some of labels have been updated from "Unknown" or "Incorrect" to "Correct" so the statistic may be not identical with the paper [Identifying Patch Correctness in Test-Based Program Repair](https://arxiv.org/abs/1706.09120). The update is based on findings in recent publications.

Patch3: "Unknown" to "Correct". See [arja-supplemental.pdf](https://github.com/yyxhdy/arja-supplemental/blob/master/arja-supplemental.pdf)

PatchHDRepair7: "Incorrect" to "Correct". See [dataset](https://github.com/qixin5/DiffTGen/tree/master/expt0/dataset)
