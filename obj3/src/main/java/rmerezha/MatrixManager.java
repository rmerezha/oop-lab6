package rmerezha;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatrixManager {

    public static double[][] parseMatrix(String input) {
        String text = input.lines().filter(line -> !line.isEmpty()).collect(Collectors.joining("\n"));
        System.out.println(text);
        String[] lines = text.trim().split("\n");
        int n = lines.length;
        if (n != lines[0].split("\\s+").length) {
            throw new IllegalArgumentException("Input string contains invalid characters");
        }

        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            String[] values = lines[i].trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(values[j]);
            }
        }

        return matrix;
    }

    public static double getDeterminant(double[][] matrix) {
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(matrix);
        return new LUDecomposition(realMatrix).getDeterminant();
    }
}
