package core;

import java.util.Arrays;

/**
 * Class, which offers a variety of mouse smoothing methods
 */
public class MouseSmoothing {
	/**
	 * Method sorts an array and returns median
	 * @param arr - array
	 * @return Median of a given array
	 */
	public static double median(double [] arr){
		Arrays.sort(arr);
		int l = arr.length;
		if (l % 2 == 0){
			return (arr[l / 2] + arr[l / 2 - 1]) / 2.0;
		}
		else {
			return arr[l / 2];
		}
	}
	
	/**
	 * Jitter smoothing method (filters out random movements below given treshold)
	 * @param matrix - current coordinates
	 * @param trend - coordinate history matrix
	 * @param xtresh - x treshold value
	 * @param ytresh - y treshold value
	 * @return newly calculated coordinates
	 */
	public static double[] jitterSmoothing(double [] matrix, double [][] trend, int xtresh, int ytresh){
		// Smooth out X and Y using jitter removal hybrid method
		if (jitterModule(matrix[0], trend[0][0], xtresh)){
			matrix[0] = (trend[0][0] + matrix[0]) / 2.0;
		}
		if (jitterModule(matrix[1], trend[0][1], ytresh)){
			matrix[1] = (trend[0][1] + matrix[0]) / 2.0;
		}
		return matrix;
	}
	
	/**
	 * Returns true if coordinate module is higher than treshold value
	 */
	public static boolean jitterModule(double x, double mx, double treshold){
		if (Math.abs(x - mx) > treshold){
			return true;
		}
		return false;
	}
	
	/**
	 * Exponential smoothing method, uses powers and other magic to smooth out actions
	 * @param matrix - coordinates to be smoothed
	 * @param trend - history matrix
	 * @return newly smoothed coordinates
	 */
	public static double[] exponentialSmoothing(double [] matrix, double[][] trend){
		// Exponential smoothing
		// Xn = a * sum-i=0->n[ (1-a)^i * X(n-i) ]
		// N = 7 ; a = 0.35;
		double a = 0.35;
		double invA = 1 - a;
		int n = 6;
		double sumX = 0;
		double sumY = 0;
		for (int i = 0; i < n; i++){
			sumX += Math.pow(invA, i) * trend[n - i][0];
			sumY += Math.pow(invA, i) * trend[n - i][1];
		}
		matrix[0] = a * sumX;
		matrix[1] = a * sumY;
		
		return matrix;
	}
	
	/**
	 * Median smoothing algorithm
	 * @param matrix - coordinates to be smoothed
	 * @param xmed - x median value
	 * @param ymed - y median value
	 * @return smoothed coordinates
	 */
	public static double[] medianSmoothing(double [] matrix, int xmed, int ymed){
		// Smooth out X and Y using median, where element count N=13
		matrix[0] = (matrix[0] + xmed) / 2.0;
		matrix[1] = (matrix[1] + ymed) / 2.0;
		return matrix;
	}
	
	/**
	 * Double averaging smoothing algorithm
	 * @param matrix - coordinates to be smoothed
	 * @param trend - coordinate history array
	 * @return smoothed coordinates
	 */
	public static double [] doubleAverageSmoothing(double [] matrix, double [][] trend){
		// Smooth out X and Y movement using double moving average simplified formula
		// Xn = 5/9 * Xn + 4/9 * X(n-1) + 1/3 * X(n-2) - 2/9 * X(n-3) - 1/9 * X(n-4);		
		matrix[0] = 0.555 * matrix[0] + 0.444 * trend[0][0] + 0.333 * trend[1][0] - 0.222 * trend[2][0] - 0.111 * trend[3][0];
		matrix[1] = 0.555 * matrix[1] + 0.444 * trend[0][1] + 0.333 * trend[1][1] - 0.222 * trend[2][1] - 0.111 * trend[3][1];
		return matrix;
	}
}
