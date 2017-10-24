package com.test.matrix;

import com.test.matrix.matrix.Matrix;
import com.test.matrix.matrix.MatrixUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class MatrixTest {

  private Matrix a;

  private Matrix b;

  private Matrix c;

  @Before
  public void init() {
    a = new Matrix(new Double[][]{
            {1.0, 2.0},
            {3.0, 4.0}
    });

    b = new Matrix(new Double[][]{
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0}
    });

    c = new Matrix(new Double[][]{
            {9.0, 12.0, 15.0},
            {19.0, 26.0, 33.0}
    });

  }


  @Test
  public void multiply_validMatrix_ok() throws InterruptedException {
    Assert.assertEquals(c, a.multiply(b));
  }

  @Test
  public void parallelMultiply_oneThread_ok() throws InterruptedException {
    Assert.assertEquals(c, a.multiplyParallel(b, 1));
  }

  @Test
  public void parallelMultiply_twoThreads_ok() throws InterruptedException {
    Assert.assertEquals(c, a.multiplyParallel(b, 2));
  }

  @Test
  public void parallelMultiply_negativeNumberOfThreads_ok() throws InterruptedException {
    Assert.assertEquals(c, a.multiplyParallel(b, -1));
  }

  @Ignore
  @Theory
  public void multiplyEstimatePerformance(
          @TestedOn(ints = {1000, 1500}) int size) throws InterruptedException {
    Matrix matrix1 = MatrixUtils.generateWithRandomInts(size, size, 10);
    Matrix matrix2 = MatrixUtils.generateWithRandomInts(size, size, 10);

    final int numOfProcessors = Runtime.getRuntime().availableProcessors();
    System.out.println("Number of processors: " + numOfProcessors);
    System.out.println("Matrix size: " + size + "x" + size);

    long start = System.currentTimeMillis();
    matrix1.multiply(matrix2);
    System.out.println("Sequential multiplication: " + (System.currentTimeMillis() - start) / 1000.0 + " s");

    for (int i = 2; i <= numOfProcessors + 2; i++) {
      start = System.currentTimeMillis();
      matrix1.multiplyParallel(matrix2, i);
      System.out.println("Parallel multiplication with " + i + " threads: " + (System.currentTimeMillis() - start) / 1000.0 + " s");
    }
  }


}
