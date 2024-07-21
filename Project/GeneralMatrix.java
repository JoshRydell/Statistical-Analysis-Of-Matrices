/*
 * PROJECT III: GeneralMatrix.java
 *
 * This file contains a template for the class GeneralMatrix. Not all methods
 * are implemented. Make sure you have carefully read the project formulation
 * before starting to work on this file. You will also need to have completed
 * the Matrix class.
 *
 * Remember not to change the names, parameters or return types of any
 * variables in this file!
 *
 * The function of the methods and instance variables are outlined in the
 * comments directly above them.
 */

//import java.util.Arrays;
import java.util.Random;

public class GeneralMatrix extends Matrix {
    /**
     * This instance variable stores the elements of the matrix.
     */
    private double[][] data;

    /**
     * Constructor function: should initialise m and n through the Matrix
     * constructor and set up the data array.
     *
     * @param m  The first dimension of the array.
     * @param n  The second dimension of the array.
     */
    public GeneralMatrix(int m, int n) throws MatrixException {
        // You need to fill in this method.
        super(m,n);
        if(m <= 0 || n <=0 ) throw new MatrixException("Dimensions should be positive integers");
        data = new double[m][n];
        for(int i = 0; i < m; i++) //sets all data to 0.0
        {
            for(int j = 0; j < n; j++)
            {
                data[i][j] = 0.0; 
            }
        }

    }

    /**
     * Constructor function. This is a copy constructor; it should create a
     * copy of the matrix A.
     *
     * @param A  The matrix to create a copy of.
     */
    public GeneralMatrix(GeneralMatrix A) {
        // You need to fill in this method.
        super(A.m, A.n);
        data = new double[m][n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                data[i][j] = A.getIJ(i, j); //copies each element over
            }
        }

    }
    
    /**
     * Getter function: return the (i,j)'th entry of the matrix.
     *
     * @param i  The location in the first co-ordinate.
     * @param j  The location in the second co-ordinate.
     * @return   The (i,j)'th entry of the matrix.
     */
    public double getIJ(int i, int j) {
        // You need to fill in this method
        if(i < 0 || i >= m || j < 0 || j >= n) throw new MatrixException("indexes out of bounds");
        return data[i][j];
    }
    
    /**
     * Setter function: set the (i,j)'th entry of the data array.
     *
     * @param i    The location in the first co-ordinate.
     * @param j    The location in the second co-ordinate.
     * @param val  The value to set the (i,j)'th entry to.
     */
    public void setIJ(int i, int j, double val) {
        // You need to fill in this method.
        if(i < 0 || i >= m || j < 0 || j >= n) throw new MatrixException("Indices out of range");
        data[i][j] = val;
    }
    
    /**
     * Return the determinant of this matrix.
     *
     * @return The determinant of the matrix.
     */
    public double determinant() {
        // You need to fill in this method.
        double[] d = new double[1];
        double det = 1.0;
        try 
        {          
            Matrix LU = decomp(d); //determinant is the product of the diagonals
            for(int i = 0; i < m; i++)
            {
                det *= LU.getIJ(i, i); 
            }
        } 
        catch (MatrixException e)
        {
            det = 0.0;
        } 
        return det * d[0];
    }

    /**
     * Add the matrix to another matrix A.
     *
     * @param A  The Matrix to add to this matrix.
     * @return   The sum of this matrix with the matrix A.
     */
    public Matrix add(Matrix A) {
        // You need to fill in this method.
        if(A.m != m || A.n != n) throw new MatrixException("incompatible dimensions");
        Matrix sum = new GeneralMatrix(m, n);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                sum.setIJ(i, j, A.getIJ(i, j) + data[i][j]); //adds each element and sets it to sum
            }
        }

        return sum;
    }
    
    /**
     * Multiply the matrix by another matrix A. This is a _left_ product,
     * i.e. if this matrix is called B then it calculates the product BA.
     *
     * @param A  The Matrix to multiply by.
     * @return   The product of this matrix with the matrix A.
     */
    public Matrix multiply(Matrix A) {
        // You need to fill in this method.
        if(n != A.m) throw new MatrixException("incompatible dimensions");
        Matrix prod = new GeneralMatrix(m, A.n);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < A.n; j++)
            {
                for(int k = 0; k < n; k++)
                {
                    prod.setIJ(i,j, prod.getIJ(i, j) + getIJ(i,k)*A.getIJ(k,j));
                }
            }
        }

        return prod;
    }

    /**
     * Multiply the matrix by a scalar.
     *
     * @param a  The scalar to multiply the matrix by.
     * @return   The product of this matrix with the scalar a.
     */
    public Matrix multiply(double a) {
        // You need to fill in this method.
        Matrix prod = new GeneralMatrix(this);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                prod.setIJ(i,j,a*getIJ(i, j)); // multiplies each element by a scalar
            }
        }
        return prod;
    }

    /**
     * Populates the matrix with random numbers which are uniformly
     * distributed between 0 and 1.
     */
    public void random() {
        // You need to fill in this method.
        Random rand = new Random();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                setIJ(i,j,rand.nextDouble());
            }
        }
    }

    /**
     * Returns the LU decomposition of this matrix; i.e. two matrices L and U
     * so that A = LU, where L is lower-diagonal and U is upper-diagonal.
     * 
     * On exit, decomp returns the two matrices in a single matrix by packing
     * both matrices as follows:
     *
     * [ u_11 u_12 u_13 u_14 ]
     * [ l_21 u_22 u_23 u_24 ]
     * [ l_31 l_32 u_33 u_34 ]
     * [ l_41 l_42 l_43 l_44 ]
     *
     * where u_ij are the elements of U and l_ij are the elements of l. When
     * calculating the determinant you will need to multiply by the value of
     * d[0] calculated by the function.
     * 
     * If the matrix is singular, then the routine throws a MatrixException.
     *
     * This method is an adaptation of the one found in the book "Numerical
     * Recipies in C" (see online for more details).
     * 
     * @param d  An array of length 1. On exit, the value contained in here
     *           will either be 1 or -1, which you can use to calculate the
     *           correct sign on the determinant.
     * @return   The LU decomposition of the matrix.
     */
    public GeneralMatrix decomp(double[] d) {
        // This method is complete. You should not even attempt to change it!!
        if (n != m)
            throw new MatrixException("Matrix is not square");
        if (d.length != 1)
            throw new MatrixException("d should be of length 1");
        
        int           i, imax = -10, j, k; 
        double        big, dum, sum, temp;
        double[]      vv   = new double[n];
        GeneralMatrix a    = new GeneralMatrix(this);
        
        d[0] = 1.0;
        
        for (i = 1; i <= n; i++) {
            big = 0.0;
            for (j = 1; j <= n; j++)
                if ((temp = Math.abs(a.data[i-1][j-1])) > big)
                    big = temp;
            if (big == 0.0)
                throw new MatrixException("Matrix is singular");
            vv[i-1] = 1.0/big;
        }
        
        for (j = 1; j <= n; j++) {
            for (i = 1; i < j; i++) {
                sum = a.data[i-1][j-1];
                for (k = 1; k < i; k++)
                    sum -= a.data[i-1][k-1]*a.data[k-1][j-1];
                a.data[i-1][j-1] = sum;
            }
            big = 0.0;
            for (i = j; i <= n; i++) {
                sum = a.data[i-1][j-1];
                for (k = 1; k < j; k++)
                    sum -= a.data[i-1][k-1]*a.data[k-1][j-1];
                a.data[i-1][j-1] = sum;
                if ((dum = vv[i-1]*Math.abs(sum)) >= big) {
                    big  = dum;
                    imax = i;
                }
            }
            if (j != imax) {
                for (k = 1; k <= n; k++) {
                    dum = a.data[imax-1][k-1];
                    a.data[imax-1][k-1] = a.data[j-1][k-1];
                    a.data[j-1][k-1] = dum;
                }
                d[0] = -d[0];
                vv[imax-1] = vv[j-1];
            }
            if (a.data[j-1][j-1] == 0.0)
                a.data[j-1][j-1] = 1.0e-20;
            if (j != n) {
                dum = 1.0/a.data[j-1][j-1];
                for (i = j+1; i <= n; i++)
                    a.data[i-1][j-1] *= dum;
            }
        }
        
        return a;
    }

    /*
     * Your tester function should go here.
     */
    public static void main(String[] args) {
        // You need to fill in this method.
        Matrix A = new GeneralMatrix(3,3);
        int counter = 1;
        for(int i = 0; i < A.m; i++)
        {
            for(int j = 0; j < A.n; j++)
            {
                A.setIJ(i, j, counter);
                counter++;
            }
        }
        A.setIJ(2, 2, 10);
        System.out.println(A.toString());
        Matrix A2 = A.multiply(A);
        Matrix A3 = A2.multiply(A);
        A3 = A3.multiply(-1);
        A2 = A2.multiply(16);
        A = A.multiply(12);
        System.out.println((A3.add(A2)).add(A).toString());
        A.setIJ(2, 2, -10);
        A.setIJ(0, 2, -0);
        System.out.println(A.toString());
    }
}