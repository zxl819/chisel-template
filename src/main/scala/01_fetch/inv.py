import numpy as np
B = np.array([[2, 0,-1], [-1, 1,1],[-1,2,1]])
C = np.array([[1, 4,5], [4, 18,26],[3,16,30]])
B_mat = np.matrix(B)
#print(B_mat.I)
inv_B = np.linalg.inv(B)
inv_C = np.linalg.inv(C)

print(inv_C)

a = np.array([[1, 2, 4,17],[3, 6, -12,3],[2, 3, -3,2],[0,2,-2,6]])
b = np.array([[17],[3],[3],[4]])
x = np.linalg.solve(a, b)
print (x)
print (np.allclose(np.dot(a, x), b))