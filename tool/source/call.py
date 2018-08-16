import multiprocessing
import os
import run
import json

if __name__ == '__main__':
    #patch_no_List=[1,2]
    patch_no_List=[ 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 36, 37, 38, 44, 45, 46, 47, 48, 49, 51, 53, 54, 55, 58, 59, 62, 63, 64, 65, 66, 67, 68, 69, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 88, 89, 90, 91, 92, 93, 150, 151, 152, 153, 154, 155, 157, 158, 159, 160, 161, 162, 163, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 191, 192, 193, 194, 195, 196, 197, 198, 199, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 'HDRepair1', 'HDRepair3', 'HDRepair4', "HDRepair5", "HDRepair6", "HDRepair7", "HDRepair8", "HDRepair9", 'HDRepair10']
    List=[]
    for patch_no in patch_no_List:
        f=os.path.join('../patches/INFO',"Patch"+str(patch_no)+".json")
        f=open(f)
        data=json.load(f)
        f.close()
        project=data['project']
        bugid=data['bug_id']
        patch_no=data['ID']
        List.append((project,bugid,patch_no))

    for Patch in List:
        print(Patch)
        project,bugid,patch_no=Patch
        run.run(project,bugid,patch_no)
