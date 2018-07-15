package com.hotactress.hot.utils;


import com.hotactress.hot.models.Point;

public class MatrixUtils {

    public static void swapMatrixElement(int[][] mat, int fromX, int fromY, int toX, int toY) {
        int t = mat[fromX][fromY];
        mat[fromX][fromY] = mat[toX][toY];
        mat[toX][toY] = t;
    }

    public static int[][] cloneMatrix(int [][]mat){
        int [][] newMat = new int[mat.length][mat[0].length];
        for (int i = 0 ; i < mat.length ; i++)
            System.arraycopy(mat[i], 0, newMat[i], 0, mat[0].length);
        return newMat;
    }

    public static Point indexOf(int[][] mat, int elm) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++)
                if (mat[i][j] == elm) {
                    return new Point(i, j);
                }
        }
        return null;
    }

    public static boolean isMatrixEqual(int[][] mat1, int[][] mat2) {
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat1.length; j++) {
                if (mat1[i][j] != mat2[i][j])
                    return false;
            }
        }
        return true;
    }

    public static void copyMatrix(int [][]srcMat, int [][]desMat){
        for (int i = 0 ; i < srcMat.length ; i++)
            System.arraycopy(srcMat[i], 0, desMat[i], 0, srcMat[0].length);
    }
}
