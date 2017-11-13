public class Integrate {
    static double compositeSimpsons(Peak peak, double baseline, CubicSpline spline){
        int intervals = 20;
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
}
