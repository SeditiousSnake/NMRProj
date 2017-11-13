import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SpectrumAnalyzer {
    public static void main(String Args[]) throws IOException{
        Settings settings = new Settings("src/nmr.in");

        ArrayList<Point> data = Point.getData(settings.inputFileName);

        Collections.sort(data);

        data = Filters.applyTMS(data, settings.baseline);

        ArrayList<Point> filteredPoints = new ArrayList<>();

        for(int i = 0; i < settings.numberOfPasses; i++) {
            switch (settings.filterType) {
                case 0:
                    filteredPoints = data;
                    break;

                case 1:
                    filteredPoints = Filters.applyBoxcar(data, settings.filterSize);
                    break;

                case 2:
                    filteredPoints = Filters.applyTMS(data, settings.filterSize);
                    break;

                default:
                    filteredPoints = data;
                    break;
            }
        }

        CubicSpline spline = new CubicSpline(filteredPoints);

        double PeakStart = Double.NaN;
        double PeakEnd = Double.NaN;

        ArrayList<Peak> peaks = new ArrayList<>();

        for(int i = 0; i < filteredPoints.size() - 1; i++){
            if(filteredPoints.get(i).getY() < settings.baseline && filteredPoints.get(i + 1).getY() > settings.baseline){
                PeakStart = findRoot(filteredPoints.get(i).getX(), filteredPoints.get(i+1).getX(), spline);
            }

            if(filteredPoints.get(i + 1).getY() < settings.baseline && filteredPoints.get(i).getY() > settings.baseline){
                PeakEnd = findRoot(filteredPoints.get(i).getX(), filteredPoints.get(i+1).getX(), spline);
            }

            if(!Double.isNaN(PeakStart) && !Double.isNaN(PeakEnd)){
                peaks.add(new Peak(PeakStart, PeakEnd));
                PeakStart = Double.NaN;
                PeakEnd = Double.NaN;
            }
        }

        for(Peak peak: peaks){
            peak.print();
        }

        System.out.println(peaks.size() * 2);

        //spline.getSplinePoints(data.get(0).getX(), data.get(data.size()-1).getX(), 0.001);
    }

    //Uses bisection
    static public double findRoot(double endpointA, double endpointB, CubicSpline spline){
        double FA = spline.f(endpointA);
        double FP;
        double root;

        for (int i = 0; i < 10000; i++){
            root = endpointA + (endpointB - endpointA) / 2;
            FP = spline.f(root);

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