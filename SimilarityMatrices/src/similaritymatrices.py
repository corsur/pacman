# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.

import fnmatch 
import os 

count = 1.0/29.0
similarity_matrix = []
for i in range(71):
    similarity_matrix += [[]]
    similarity_matrix[i] += [0 for j in range(71)]

for file in os.listdir('.'): 
    if fnmatch.fnmatch(file, '*.txt'):
        f = open(file, 'r')
        lines = f.readlines()
        for scene in range(71):
            flag = False
            for ln in lines:
                ln = ln.strip('\n')
                ln = ln.split(' ')
                if str(scene+1) in ln:
                    line = ln
            for current_scene in line:
                similarity_matrix[scene][int(current_scene)-1] += count

for i in range(71):
    print similarity_matrix[i]