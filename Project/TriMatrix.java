/*
 * PROJECT III: TriMatrix.java
 *
 * This file contains a template for the class TriMatrix. Not all methods are
 * implemented. Make sure you have carefully read the project formulation
 * before starting to work on this file. You will also need to have completed
 * the Matrix class.
 *
 * Remember not to change the names, parameters or return types of any
 * variables in this file!
 *
 * The function of the methods and instance variables are outlined in the
 * comments directly above them.
 */
import java.util.Random;
public class TriMatrix extends Matrix {
    /**
     * An array holding the diagonal elements of the matrix.
     */
    private double[] diag;

    /**
     * An array holding the upper-diagonal elements of the matrix.
     */
    private double[] upper;

    /**
     * An array holding the lower-diagonal elements of the matrix.
     */
    private double[] lower;
    
    /**
     * Constructor function: should initialise m and n through the Matrix
     * constructor and set up the data array.
     *
     * @param N  The dimension of the array.
     */
    public TriMatrix(int N) {
        // You need to fill in this method.
        super(N,N);
        diag = new double[N];
        upper = new double[N-1];
        lower = new double[N-1];
    }
    
    /**
     * Getter function: return the (i,j)'th entry of the matrix.
     *
     * @param i  The location in the first co-ordinate.
     * @param j  The location in the second co-ordinate.
     * @return   The (i,j)'th entry of the matrix.
     */
    public double getIJ(int i, int j) {
        // You need to fill in this method.
        if(i < 0 || i >= m || j < 0 || j >= n) throw new MatrixException("indexes out of bounds");
        if(i == j)
        {
            return diag[i];
        }
        else if(i+1 == j)
        {
            return upper[i];
        }
        else if(i-1  == j)
        {
            return lower[j];
        }

        return 0;
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
        if(j < i - 1 || j > i + 1 || i < 0 || i >= m || j < 0 || j >= n)  throw new MatrixException("indexes out of bounds");
        if(i == j)
        {
            diag[i] = val;
        }
        else if(i+1 == j)
        {
            upper[i] = val;
        }
        else if(i-1 == j)
        {
            lower[j] = val;
        }
    }
    
    /**
     * Return the determinant of this matrix.
     *
     * @return The determinant of the matrix.
     */
    public double determinant() {
        // You need to fill in this method.
        double det = 1.0;
        try {
            TriMatrix LU = decomp(); //determinant is the product of the diagonals
            for(int i = 0; i < diag.length; i++)
            {
                det *= LU.getIJ(i, i); 
            }
        }
        catch (MatrixException e)
        {
            det = 0.0;
        }
        return det;
    }
    
    /**
     * Returns the LU decomposition of this matrix. See the formulation for a
     * more detailed description.
     * 
     * @return The LU decomposition of this matrix.
     */
    public TriMatrix decomp() {
        // You need to fill in this method.
        TriMatrix LU = new TriMatrix(diag.length); 
        double[] diag_star = new double[diag.length]; //sets up new the diag of the U matrix (U's upper is just our upper)
        double[] lower_star = new double[lower.length]; //sets up new lower for L matrix (L's diag is equal to 1)
        diag_star[0] = diag[0]; 

        for(int i = 1; i < diag_star.length; i++)
        {
            if(diag_star[i-1] == 0) throw new MatrixException("Matrix is singular"); //checks for dividing by zero errors
            diag_star[i] = diag[i] - lower[i-1]*upper[i-1]/diag_star[i-1];
        }
        for(int i = 0; i < lower_star.length; i++)
        {
            lower_star[i] = lower[i]/diag_star[i];
        }
        for(int i = 0; i < diag.length;i++)
        {
            LU.setIJ(i, i, diag_star[i]);
            if(i < lower.length)
            {
                LU.setIJ(i, i+1, upper[i]);
                LU.setIJ(i+1, i, lower_star[i]);
            }
        }
        return LU;

    }

    /**
     * Add the matrix to another matrix A.
     *
     * @param B  The Matrix to add to this matrix.
     * @return   The sum of this matrix with the matrix A.
     */
    public Matrix add(Matrix A){
        // You need to fill in this method.
        if(A.m != diag.length || A.n != diag.length) throw new MatrixException("incompatible dimensions");
        if(A instanceof TriMatrix) //checks if A is trimatrix so it can run O(n) time
        {
            Matrix sum = new TriMatrix(diag.length);
            for(int i = 0; i < diag.length; i++)
            {
                sum.setIJ(i,i, diag[i] + A.getIJ(i,i));
                if(i < upper.length)
                {
                    sum.setIJ(i+1,i, lower[i] + A.getIJ(i+1,i));
                    sum.setIJ(i,i+1, upper[i] + A.getIJ(i,i+1));
                }
            }

            return sum;
        }
        else
        { //if A is general it can run O(n^2) time
            Matrix sum = new GeneralMatrix(diag.length, diag.length);
            for(int i = 0; i < diag.length; i++)
            {
                for(int j = 0; j < diag.length; j++)
                {
                    sum.setIJ(i, j, A.getIJ(i,j) + getIJ(i,j));
                }
            }
            return sum;
        }
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
        if(A.m != diag.length) throw new MatrixException("incompatible dimensions");
        if(A instanceof TriMatrix) //checks if A is trimatrix so it can run O(n) time
        {
            Matrix prod = new GeneralMatrix(diag.length,diag.length);
            for(int i = 0; i < diag.length; i++)
            {
                double val = diag[i]*A.getIJ(i, i);
                if(i >= 1) val += lower[i-1]*A.getIJ(i-1,i);
                if(i < upper.length) val += upper[i]*A.getIJ(i+1, i);
                prod.setIJ(i, i, val);
            }
            for(int i = 0; i < upper.length; i++)
            {
                prod.setIJ(i+1, i, diag[i+1]*A.getIJ(i+1, i) + A.getIJ(i, i)*lower[i]);
                prod.setIJ(i, i+1, diag[i]*A.getIJ(i, i+1) + A.getIJ(i+1, i+1)*upper[i]);
            }
            for(int i = 0; i < upper.length - 1; i++)
            {
                prod.setIJ(i+2, i, lower[i+1]*A.getIJ(i+1, i));
                prod.setIJ(i, i+2, upper[0]*A.getIJ(i+1, i+2));
            }
            return prod;

        }
        else
        {
            Matrix prod = new GeneralMatrix(diag.length, A.n); //if A is general it can run O(n^2) time
            for(int i = 0; i < diag.length; i++)
            {
                for(int j = 0; j < A.n; j++)
                {
                    double val = 0.0; 
                    if(i+1 < A.n) val += upper[i]*A.getIJ(i+1, j);
                    if(i >= 1) val += lower[i-1]*A.getIJ(i-1, j);
                    val += diag[i]*A.getIJ(i, j);
                    prod.setIJ(i, j, val);
                }  
            }
            return prod;
        }
    }
    
    /**
     * Multiply the matrix by a scalar.
     *
     * @param a  The scalar to multiply the matrix by.
     * @return   The product of this matrix with the scalar a.
     */
    public Matrix multiply(double a) {
        // You need to fill in this method.
        Matrix prod = new TriMatrix(diag.length);
        for(int i = 0; i < diag.length; i++)
        {
            prod.setIJ(i, i, diag[i] * a); //multiplies all entries by scalar a;
            if(i < upper.length)
            {
                prod.setIJ(i, i+1, upper[i] * a); 
                prod.setIJ(i+1, i, lower[i] * a);
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
        for(int i = 0 ; i < lower.length; i++)
        {
            lower[i] = rand.nextDouble();
            upper[i] = rand.nextDouble();
        }
        for(int i = 0 ; i < diag.length; i++)
        {
            diag[i] = rand.nextDouble();
        }
    }
    
    /*
     * Your tester function should go here.
     */
    public static void main(String[] args) {
        int N = 4;
        Matrix At = new TriMatrix(N);
        Matrix Bt = new TriMatrix(N);
        At.random();
        Bt.random();
        Matrix Ag = new GeneralMatrix(N,N);
        Matrix Bg = new GeneralMatrix(N,N);
        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < N; j++)
            {
                Ag.setIJ(i, j, At.getIJ(i, j));
                Bg.setIJ(i, j, Bt.getIJ(i, j));
            }
        }

        System.out.println(Ag.add(Bg).multiply(2).determinant());
        System.out.println(At.add(Bt).multiply(2).determinant());
    }
}