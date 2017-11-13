import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SpectrumAnalyzer {
    public static void main(String Args[]) throws IOException{
        Settings settings = new Settings("src/nmr.in");

        ArrayList<Point> data = Point.getData(settings.inputFileName);

        Collections.sort(data);

        data = Filters.applyTMS(data, settings.baseline);

        for(int i = 0; i < settings.numberOfPasses; i++) {
            switch (settings.filterType) {
                case 0:
                    data = data;
                    break;

                case 1:
                    data = Filters.applyBoxcar(data, settings.filterSize);
                    break;

                case 2:
                    data = Filters.applySavitzkyGolay(data, settings.filterSize);
                    break;

                default:
                    data = data;
                    break;
            }
        }

//        for (Point point: data){
//            point.print();
//        }

        CubicSpline spline = new CubicSpline(data);

        double PeakStart = Double.NaN;
        double PeakEnd = Double.NaN;

        ArrayList<Peak> peaks = new ArrayList<>();

        for(int i = 0; i < data.size() - 1; i++){
            if(data.get(i).getY() < settings.baseline && data.get(i + 1).getY() > settings.baseline){
                PeakStart = findRoot(data.get(i).getX(), data.get(i+1).getX(), settings.baseline, spline);
            }

            if(data.get(i + 1).getY() < settings.baseline && data.get(i).getY() > settings.baseline){
                PeakEnd = findRoot(data.get(i).getX(), data.get(i+1).getX(), settings.baseline, spline);
            }

            if(!Double.isNaN(PeakStart) && !Double.isNaN(PeakEnd)){
                peaks.add(new Peak(PeakStart, PeakEnd));
                PeakStart = Double.NaN;
                PeakEnd = Double.NaN;
            }
        }

        for(Peak peak: peaks){
            peak.print();
            peak.area = Integrate.compositeSimpsons(peak, settings.baseline, spline);
            System.out.println(peak.area);
        }

        System.out.println(peaks.size());

        //spline.getSplinePoints(data.get(0).getX(), data.get(data.size()-1).getX(), 0.001);
    }

    //Uses bisection
    static public double findRoot(double endpointA, double endpointB, double baseline, CubicSpline spline){
        double FA = spline.f(endpointA, baseline);
        double FP;
        double root;

        for (int i = 0; i < 10000; i++){
            root = endpointA + (endpointB - endpointA) / 2;
            FP = spline.f(root, baseline);

            if ((FP == 0) || ((endpointB - endpointA) / 2 < .00000000001)){
                return root;
            }

            if (FA*FP > 0){
                endpointA = root;
                FA = FP;
            } else {
                endpointB = root;
            }
        }

        System.out.println("Method failed after " + 10000 + " iterations.");
        return Double.NaN;
    }
}