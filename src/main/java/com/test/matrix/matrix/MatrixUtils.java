package com.test.matrix.matrix;


import java.util.Random;


public final class MatrixUtils {

    private MatrixUtils() {

    }

    public static Matrix generateWithRandomInts(int rows, int columns, int bound) {
      Random random = new Random(System.currentTimeMillis());
      Matrix m = new Matrix(rows, columns);
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          m.set(i, j, (double) random.nextInt(bound));
        }
      }
      return m;
    }
}
