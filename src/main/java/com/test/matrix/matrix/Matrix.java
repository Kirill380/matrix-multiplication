package com.test.matrix.matrix;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Matrix {

  private Double[][] data;

  public Matrix(int rowNumber, int columnNumber) {
    data = new Double[rowNumber][columnNumber];
  }

  public Matrix(Double[][] data) {
    int numOfRows = data.length;
    int numOfColumns = data[0].length; // assume that number of elements in each row is the same
    this.data = new Double[numOfRows][numOfColumns];
    for (int i = 0; i < numOfRows; i++) {
      System.arraycopy(data[i], 0, this.data[i], 0, numOfColumns);
    }
  }

  public Double get(int rowIndex, int columnIndex) {
    return data[rowIndex][columnIndex];
  }

  public void set(int rowIndex, int columnIndex, Double value) {
    data[rowIndex][columnIndex] = value;
  }

  public int getNumberOfRows() {
    return data.length;
  }

  public int getNumberOfColumns() {
    return data[0].length;
  }

  /**
   * Multiply current matrix on matrix b.
   * Complexity of algorithm is O(n^3)
   */
  public Matrix multiply(Matrix b) {
    if (this.getNumberOfColumns() != b.getNumberOfRows()) {
      throw new IllegalArgumentException("Matrix can't be multiplied because they have different sizes");
    }
    Double[][] newMatrix = new Double[this.getNumberOfRows()][b.getNumberOfColumns()];

    for (int i = 0; i < this.getNumberOfRows(); i++) {
      for (int j = 0; j < b.getNumberOfColumns(); j++) {
        newMatrix[i][j] = 0.0;
        for (int k = 0; k < this.getNumberOfColumns(); k++) {
          newMatrix[i][j] += this.get(i, k) * b.get(k, j);
        }
      }
    }
    return new Matrix(newMatrix);
  }

  public Matrix multiplyParallel(Matrix b, int threadsNum) throws InterruptedException {
    if (this.getNumberOfColumns() != b.getNumberOfRows()) {
      throw new IllegalArgumentException("Matrix can't be multiplied because they have different sizes");
    }

    Matrix resultMatrix = new Matrix(this.getNumberOfRows(), b.getNumberOfColumns());
    int numOfThreads = threadsNum > 0 ? threadsNum : Runtime.getRuntime().availableProcessors();

    ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

    int numberOfRows = this.getNumberOfRows();
    int batchSize = numberOfRows > numOfThreads ? numberOfRows / numOfThreads : 1;
    int n = numberOfRows > numOfThreads ? numOfThreads : numberOfRows;

    for (int i = 0; i < n; i++) {
      int start = i * batchSize;
      int end = (i + 1 != n) ? (i + 1) * batchSize : numberOfRows;
      executor.submit(new RowMultiplicationTask(this, b, resultMatrix, start, end));
    }
    executor.shutdown();
    while (!executor.awaitTermination(1, TimeUnit.HOURS)) {
      System.out.println("Still waiting for calculation");
    }
    return resultMatrix;
  }


  private static class RowMultiplicationTask implements Runnable {

    private final Matrix matrixA;

    private final Matrix matrixB;

    private final Matrix resultMatrix;

    private final int startIndex;

    private final int endIndex;

    public RowMultiplicationTask(Matrix matrixA, Matrix matrixB, Matrix resultMatrix, int startIndex, int endIndex) {
      this.matrixA = matrixA;
      this.matrixB = matrixB;
      this.resultMatrix = resultMatrix;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    @Override
    public void run() {
      for (int i = startIndex; i < endIndex; i++) {
        for (int j = 0; j < matrixB.getNumberOfColumns(); j++) {
          Double sum = 0.0;
          for (int k = 0; k < matrixA.getNumberOfColumns(); k++) {
            sum += matrixA.get(i, k) * matrixB.get(k, j);
          }
          resultMatrix.set(i, j, sum);
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Matrix matrix = (Matrix) o;

    return Arrays.deepEquals(data, matrix.data);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(data);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < getNumberOfRows(); i++) {
      result.append("|| ");
      for (int j = 0; j < getNumberOfColumns(); j++) {
        result.append(get(i, j)).append(" ");
      }
      result.append("||\n");
    }
    return result.toString();
  }
}
