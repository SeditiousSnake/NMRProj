import java.util.ArrayList;

public class Integrate {
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

    static double adaptiveQuadrature(Peak peak, double baseline, CubicSpline spline, double tolerance){
        return 0;
    }
}
