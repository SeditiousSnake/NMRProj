import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SpectrumAnalyzer {
    public static void main(String Args[]) throws IOException{
        Settings settings = new Settings("src/nmr.in");
        settings.printSettings();

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

        CubicSpline spline = new CubicSpline(data);

        double PeakStart = Double.NaN;
        double PeakEnd = Double.NaN;

        ArrayList<Peak> peaks = new ArrayList<>();

        for(int i = 0; i < data.size() - 1; i++){
            if(data.get(i).getY() < settings.baseline && data.get(i + 1).getY() > settings.baseline){
                PeakStart = findRoot(data.get(i).getX(), data.get(i+1).getX(),settings.tolerance, settings.baseline, spline);
            }

            if(data.get(i + 1).getY() < settings.baseline && data.get(i).getY() > settings.baseline){
                PeakEnd = findRoot(data.get(i).getX(), data.get(i+1).getX(), settings.tolerance, settings.baseline, spline);
            }

            if(!Double.isNaN(PeakStart) && !Double.isNaN(PeakEnd)){
                peaks.add(new Peak(PeakStart, PeakEnd));
                PeakStart = Double.NaN;
                PeakEnd = Double.NaN;
            }
        }

        //Integrate the peaks to find their areas
        for(Peak peak: peaks){
            switch (settings.integrationTechnique) {
                case 0:
                    peak.area = Integrate.compositeSimpsons(peak, settings.baseline, spline);
                    break;

                case 1:
                    peak.area = Integrate.romberg(peak, settings.baseline, spline, settings.tolerance);
                    break;

                case 2:
                    peak.area = Integrate.adaptiveQuadrature(peak, settings.baseline, spline, settings.tolerance);
                    break;

                case 3:
                    peak.area = Integrate.guassianQuadrature(peak, settings.baseline, spline);
                    break;

                default:
                    peak.area = Integrate.adaptiveQuadrature(peak, settings.baseline, spline, settings.tolerance);
            }
        }

        for(Peak peak: peaks){
            peak.findTop(settings.baseline, spline);
        }

        calculateHydrogenCounts(peaks);

        //Print out the data for each peak
        for(int i = 0; i < peaks.size(); i++){
            peaks.get(i).print(i + 1);
        }
    }

    //Uses bisection
    static public double findRoot(double endpointA, double endpointB, double tolerance, double baseline, CubicSpline spline){
        double FA = spline.f(endpointA, baseline);
        double FP;
        double root;

        for (int i = 0; i < 10000; i++){
            root = endpointA + (endpointB - endpointA) / 2;
            FP = spline.f(root, baseline);

            if ((FP == 0) || ((endpointB - endpointA) / 2 < tolerance)){
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

    static private void calculateHydrogenCounts(ArrayList<Peak> peaks){
        double smallestArea = peaks.get(0).area;

        for(Peak peak: peaks){
            if(peak.area < smallestArea) smallestArea = peak.area;
        }

        for(Peak peak: peaks){
            peak.hydrogens = (int) (peak.area / smallestArea);
        }
    }
}