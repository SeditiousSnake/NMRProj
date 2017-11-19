import java.lang.reflect.Array;
import java.util.ArrayList;

public class Integrate {
    //Composite Simpson's Rule from page 205 of the book
    static double compositeSimpsons(Peak peak, double baseline, CubicSpline spline){
        int intervals = 50;
        double h = (peak.endPoint - peak.startPoint) / intervals;
        double approximation = 0;
        double X = peak.startPoint;
        double XI0 = spline.f(peak.startPoint, baseline) + spline.f(peak.endPoint, baseline);
        double XI1 = 0;
        double XI2 = 0;

        for(int i = 1; i < intervals; i++){
            X = peak.startPoint + i *h;
            if (i % 2 == 0){
                XI2 += spline.f(X, baseline);
            } else {
                XI1 += spline.f(X, baseline);
            }
        }

        approximation = h * (XI0 + 2 * XI2 + 4 * XI1) / 3;

        return approximation;
    }

    //Romberg Integration from page 216 of the book
    static double romberg(Peak peak, double baseline, CubicSpline spline, double tolerance){
        ArrayList<Double> row1 = new ArrayList<>();
        ArrayList<Double> row2 = new ArrayList<>();
        //Step 1: Set h = b - a
        double h = peak.endPoint - peak.startPoint;
        double approx;
        boolean finished = false;

        //Step 1: row1, element 1 = (h/2) * (f(a) + f(b))
        row1.add(h/2 * (spline.f(peak.startPoint, baseline) + spline.f(peak.endPoint, baseline)));

        int i = 2;
        while(!finished){
            approx = row1.get(0) / 2;
            for(int k = 1; k < Math.pow(2, i-2) + 1; k++){
                approx += h * spline.f(peak.startPoint + (k-0.5)*h, baseline) / 2;
            }

            if(row2.size() > 0) row2.set(0, approx);
            else row2.add(approx);

            for(int j = 2; j < i + 1; j++){
                approx = row2.get(j - 2) + (row2.get(j - 2) - row1.get(j - 2)) / (Math.pow(4, j-1) - 1);
                if(row2.size() > j) row2.set(j - 1, approx);
                else row2.add(approx);
            }

            h = h / 2;

            for(int j = 0; j < i; j++){
                if (row1.size() > j) row1.set(j, row2.get(j));
                else row1.add(row2.get(j));
            }

            if ((Math.abs(row1.get(row1.size() - 1) - row1.get(row1.size()-2)) < tolerance) && (i > 2)) finished = true;
            i++;
        }

        return row1.get(row1.size() - 1);
    }

    //Adaptive Quadrature from page 224 of the book
    static double adaptiveQuadrature(Peak peak, double baseline, CubicSpline spline, double tolerance){
        double approx = 0;
        int i = 1;
        ArrayList<Double> TOL = new ArrayList<>();
        ArrayList<Double> a = new ArrayList<>();
        ArrayList<Double> h = new ArrayList<>();
        ArrayList<Double> FA = new ArrayList<>();
        ArrayList<Double> FC = new ArrayList<>();
        ArrayList<Double> FB = new ArrayList<>();
        ArrayList<Double> S = new ArrayList<>();
        ArrayList<Double> L = new ArrayList<>();

        TOL.add(10 * tolerance);
        a.add(peak.startPoint);
        h.add((peak.endPoint - peak.startPoint) / 2);
        FA.add(spline.f(peak.startPoint, baseline));
        FC.add(spline.f(peak.startPoint + h.get(i - 1), baseline));
        FB.add(spline.f(peak.endPoint, baseline));
        S.add(h.get(i-1) * (FA.get(i-1) + 4*FC.get(i-1) + FB.get(i-1)) / 3);
        L.add((double) 1);

        double[] v = new double[8];

        double FD;
        double FE;
        double S1;
        double S2;

        while (i > 0){
            //Step 3 of book's adaptive quadrature
            FD = spline.f(a.get(i - 1) + h.get(i - 1) /2, baseline);
            FE = spline.f(a.get(i - 1) + 3 * h.get(i - 1) / 2, baseline);
            S1 = h.get(i - 1) * (FA.get(i - 1) + 4 * FD + FC.get(i - 1)) / 6;
            S2 = h.get(i - 1) * (FC.get(i - 1) + 4 * FE + FB.get(i - 1)) / 6;
            v[0] = a.get(i - 1);
            v[1] = FA.get(i - 1);
            v[2] = FC.get(i - 1);
            v[3] = FB.get(i - 1);
            v[4] = h.get(i - 1);
            v[5] = TOL.get(i - 1);
            v[6] = S.get(i - 1);
            v[7] = L.get(i - 1);

            //Step 4
            i--;

            if (Math.abs(S1 + S2 - v[6]) < v[5]) {
                approx += S1 + S2;
            } else {
                i++;
                set(a, i, v[0] + v[4]);
                set(FA, i, v[2]);
                set(FC, i, FE);
                set(FB, i, v[3]);
                set(h, i, v[4] / 2);
                set(TOL, i, v[5] / 2);
                set(S, i, S2);
                set(L, i, v[7] + 1);

                i++;
                set(a, i, v[0]);
                set(FA, i, v[1]);
                set(FC, i, FD);
                set(FB, i, v[2]);
                set(h, i, h.get(i-2));
                set(TOL, i, TOL.get(i-2));
                set(S, i, S1);
                set(L, i, L.get(i - 2));
            }
        }

        return approx;
    }

    //Attempt at Guassian Quadrature, can't figure out why this isn't accurate
    public static double guassianQuadrature(Peak peak, double baseline, CubicSpline spline){
        double X[] = new double[8];
        double W[] = new double[8];

        X[0] = -9.602898564975363E-001;
        X[1] = -7.966664774136267E-001;
        X[2] = -5.255324099163290E-001;
        X[3] = -1.834346424956498E-001;
        X[4] = 1.834346424956498E-001;
        X[5] =  5.255324099163290E-001;
        X[6] =  7.966664774136267E-001;
        X[7] =  9.602898564975363E-001;
        W[0] =  1.012285362903706E-001;
        W[1] =  2.223810344533744E-001;
        W[2] =  3.137066458778874E-001;
        W[3] =  3.626837833783621E-001;
        W[4] =  3.626837833783621E-001;
        W[5] =  3.137066458778874E-001;
        W[6] =  2.223810344533744E-001;
        W[7] = 1.012285362903706E-001;

        double sum = 0;

        double A = peak.startPoint;
        double B = peak.endPoint;

        for(int i = 0; i < 8; i++){
            sum += W[i] * spline.f(((B-A)*X[i]+(A+B)) / 2, baseline) * (B-A) / 2;
        }

        return sum;
    }

    //If list element at index exits, replace it with the new element
    //If not, add the new element
    private static void set(ArrayList<Double> list, int index, double value){
        if(list.size() >= index) list.set(index - 1, value);
        else list.add(index-1, value);
    }
}
