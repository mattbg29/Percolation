/* *****************************************************************************
 *  Percolation with Quick Find and drawing demonstration
 *  Enter grid axis length and num of trials in command line
 *  Change drawNow from true to false to show/hide a drawing of the first trial
 *  Thanks folks at Princeton for presenting the demonstration and supplying
 *  some of the code leveraged here.
 **************************************************************************** */

import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdStats;

import java.awt.Font;
import java.util.Arrays;

public class Percolation {
    private static int n;
    private static StdStats stats;
    private static final int DELAY = 100;
    private int[] id;
    private int countOpen;
    private QuickFindUF quick;

    public Percolation(int n) {
        id = new int[n * n + n + 2];
        Arrays.fill(id, 0);
        quick = new QuickFindUF(n * n + n + 2);
    }

    public void draw() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setXscale(-0.05 * n, 1.05 * n);
        StdDraw.setYscale(-0.05 * n, 1.05 * n);   // leave a border to write text
        StdDraw.filledSquare(n / 2.0, n / 2.0, n / 2.0);

        // draw n-by-n grid
        int opened = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (isFull(row, col)) {
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                    opened++;
                }
                else if (isOpen(row, col)) {
                    StdDraw.setPenColor(StdDraw.WHITE);
                    opened++;
                }
                else
                    StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledSquare(col + 0.5, n - row - 0.5, 0.45);
            }
        }

        // write status text
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 12));
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0.25 * n, -0.025 * n, opened + " open sites");
        if (percolates()) StdDraw.text(0.75 * n, -0.025 * n, "percolates");
        else StdDraw.text(0.75 * n, -0.025 * n, "does not percolate");

    }

    private int xyTo1D(int row, int col) {
        return row * n + col;
    }

    public void open(int row, int col) {
        int xy1D = xyTo1D(row, col);
        if (id[xy1D] != 1) {
            id[xy1D] = 1;
            countOpen++;
            if (row == 0) {
                quick.union(n * n + n, xy1D);
            }
            if (row == n - 1) {
                quick.union(n * n + n + 1, xy1D);
            }
            if (isOpen(row - 1, col)) {
                quick.union(xyTo1D(row - 1, col), xy1D);
            }
            if (isOpen(row + 1, col)) {
                quick.union(xyTo1D(row + 1, col), xy1D);
            }
            if (isOpen(row, col - 1) && col > 0) {
                quick.union(xyTo1D(row, col - 1), xy1D);
            }
            if (isOpen(row, col + 1) && col < (n - 1)) {
                quick.union(xyTo1D(row, col + 1), xy1D);
            }

        }
    }

    public boolean isOpen(int row, int col) {
        int xy1D = xyTo1D(row, col);
        if (xy1D >= 0 && xy1D < n * n) {
            if (id[xy1D] == 1) {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isFull(int row, int col) {
        if (quick.find(n * n + n) == quick.find(xyTo1D(row, col))) {
            return true;
        }
        return false;
    }

    public int numberOfOpenSites() {
        return countOpen;
    }

    public boolean percolates() {
        if (quick.find(n * n + n) == quick.find(n * n + n + 1)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        boolean drawNow = false;
        double sumAnswer = 0;
        n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        double[] answerArr = new double[t];

        if (drawNow) {
            StdDraw.enableDoubleBuffering();
        }

        for (int trials = 0; trials < t; trials++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int rowNow = (int) Math.floor(Math.random() * (n));
                int colNow = (int) Math.floor(Math.random() * (n));
                perc.open(rowNow, colNow);

                if (trials == 0 && drawNow) {
                    perc.draw();
                    StdDraw.show();
                    StdDraw.pause(DELAY);
                }
            }

            double answer = (double) perc.numberOfOpenSites() / (n * n);
            answerArr[trials] = answer;
            sumAnswer += answer;
        }

        double stddevNow = stats.stddev(answerArr);

        System.out.println(sumAnswer / t);
        System.out.println(stddevNow);
    }
}
