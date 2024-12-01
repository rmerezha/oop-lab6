package rmerezha;

import java.util.Random;

public class MatrixGenerator {

    public static int[][] generateMatrix(int n, int Min, int Max) {
        Random random = new Random();
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextInt(Max - Min + 1) + Min;
            }
        }

        return matrix;
    }
}
