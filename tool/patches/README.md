All of our patches are in Unidiff format.

`./INFO` contains JSON file of the information of each patch.

To generate such patch file for our tool, use the command:
```
    diff -r -u -w [path_to_source_of_buggy_version] [path_to_source_of_patch_version]
```

