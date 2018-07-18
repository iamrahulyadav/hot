package com.hotactress.hot.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;


import com.hotactress.hot.MyApplication;
import com.hotactress.hot.R;
import com.hotactress.hot.models.Point;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuzzleMatrixHelper {

    public static final int PUZZLE_SIZE = 3;
    public static final int PUZZLE_BLOCK_SIZE = 400;
    public static final int RANDOMIZE_STEP_SIZE = 5;
    private static int DUMMY_VALUE = -1;
    private int sideSize;
    private int totalBlocks;
    private int[][] matrix;
    private int[][] solvedMatrix;
    private int[][] initialMatrix;
    private List<int[]> movesHistory = new ArrayList<>();
    private int totalMoves = 0;
    private List<Bitmap> bitmapList;
    private TextView totalMovesUpdateView;
    private List<Point> pointsMovement = Arrays.asList(new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1));

    public PuzzleMatrixHelper(int size, List<Bitmap> bitmaps, TextView totalMovesUpdateView) {

        this.sideSize = size;
        this.bitmapList = bitmaps.subList(0, bitmaps.size());
        this.matrix = new int[sideSize][sideSize];
        this.solvedMatrix = new int[sideSize][sideSize];
        this.totalMovesUpdateView = totalMovesUpdateView;


        for (int i = 0; i < sideSize; i++)
            for (int j = 0; j < sideSize; j++) {
                solvedMatrix[i][j] = i * sideSize + j;
                matrix[i][j] = solvedMatrix[i][j];
            }
        solvedMatrix[sideSize-1][sideSize-1] = matrix[sideSize-1][sideSize-1] = DUMMY_VALUE;
        Bitmap dummyBitmap = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.cross);
        dummyBitmap = Bitmap.createScaledBitmap(dummyBitmap, PUZZLE_BLOCK_SIZE, PUZZLE_BLOCK_SIZE, true);
        bitmaps.set(bitmaps.size()-1, dummyBitmap);
        randomizeOriginalMatrix();
        initialMatrix = MatrixUtils.cloneMatrix(matrix);

    }

    public Point getMovePoint(int xCord, int yCord) {
        if (matrix[xCord][yCord] != DUMMY_VALUE) {
            if (xCord - 1 >= 0 && matrix[xCord - 1][yCord] == DUMMY_VALUE)
                return new Point(xCord - 1, yCord);
            else if (xCord + 1 < matrix.length && matrix[xCord + 1][yCord] == DUMMY_VALUE)
                return new Point(xCord + 1, yCord);
            else if (yCord - 1 >= 0 && matrix[xCord][yCord - 1] == DUMMY_VALUE)
                return new Point(xCord, yCord - 1);
            else if (yCord + 1 < matrix.length && matrix[xCord][yCord + 1] == DUMMY_VALUE)
                return new Point(xCord, yCord + 1);
            else return null;
        }
        return null;
    }

    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        if (matrix[fromX][fromY] != DUMMY_VALUE) {  // only a valid value
            Point p = getMovePoint(fromX, fromY);
            if (p == null || p.x != toX || p.y != toY)
                return false;
            else return true;
        } else {
            if (toX >= 0 && toY >= 0 && toX < sideSize && toY < sideSize)
                return true;
            else return false;
        }
    }

    public void move(int fromX, int fromY, int toX, int toY) {
        int tmp = matrix[fromX][fromY];
        matrix[fromX][fromY] = matrix[toX][toY];
        matrix[toX][toY] = tmp;

        totalMoves++;
        movesHistory.add(new int[]{fromX, fromY, toX, toY});
//        totalMovesUpdateView.setText(String.valueOf(totalMoves));
    }

    public boolean isSolved() {
        return MatrixUtils.isMatrixEqual(matrix, solvedMatrix);
    }

    public Bitmap getBitmap(int x, int y){
        int index = matrix[x][y] >=0 ? matrix[x][y] : bitmapList.size()-1;
        return bitmapList.get(index);
    }


    public void randomizeOriginalMatrix() {
        int[] prevStep = new int[]{0, 0, 0, 0};
        for (int i = 0; i < RANDOMIZE_STEP_SIZE; i++) {
            Point point = MatrixUtils.indexOf(matrix, -1);
            Collections.shuffle(pointsMovement);
            for (Point p : pointsMovement) {
                int x = point.x + p.x;
                int y = point.y + p.y;

                if (x >= 0 && y >= 0 && x < sideSize && y < sideSize) {
                    if (!(prevStep[0] == x && prevStep[1] == y && prevStep[2] == point.x && prevStep[3] == point.y)) {
                        MatrixUtils.swapMatrixElement(matrix, point.x, point.y, x, y);
                        prevStep = new int[]{point.x, point.y, x, y};
                        System.out.println(MessageFormat.format("Reshuffle: {0} {1} to {2} {3}", point.x, point.y, x, y));
                        break;
                    }
                }
            }
        }
    }

    public void resetToInitialMatrix(){
        MatrixUtils.copyMatrix(initialMatrix, matrix);
    }

    public void resetToSolveMatrix(){
        MatrixUtils.copyMatrix(solvedMatrix, matrix);
    }






}