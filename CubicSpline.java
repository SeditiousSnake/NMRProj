import java.util.ArrayList;

public class CubicSpline {
    ArrayList<Point> dataPoints;
    double[] h;
    double[] a;
    double[] b;
    double[] c;
    double[] d;
    double[] alpha;
    private ArrayList<PieceWiseEquation> pieceWiseEquations= new ArrayList<PieceWiseEquation>();


    public CubicSpline(ArrayList<Point> data){
        dataPoints = data;
        h = new double[data.size() - 1];
        a = new double[data.size()];
        b = new double[data.size()];
        c = new double[data.size()];
        d = new double[data.size()];
        alpha = new double[data.size() - 1];

        populateH();
        populateA();
        populateAlpha();
        solveMatrix();

        for(int i = 0; i < dataPoints.size() - 1; i++){
            double[] coefficients = {a[i], b[i], c[i], d[i]};
            addEquation(coefficients, dataPoints.get(i).getX(), dataPoints.get(i+1).getX());
        }
    }

    private void populateH(){
        for (int i = 1; i < dataPoints.size(); i++){
            h[i - 1] = dataPoints.get(i).getX() - dataPoints.get(i -1).getX();
        }
    }

    private void populateA(){
        for(int i = 0; i < dataPoints.size(); i++){
            a[i] = dataPoints.get(i).getY();
        }
    }

    private void populateAlpha(){
        alpha[0] = 0;
        for (int i = 1; i <dataPoints.size() - 1; i++){
            alpha[i] = (a[i+1] - a[i]) * 3 / h[i] - (a[i] - a[i-1]) * 3 / h[i-1];
        }
    }

    private void solveMatrix(){
        double[] iota = new double[dataPoints.size()];
        double[] zeta = new double[dataPoints.size()];
        double[] mu = new double[dataPoints.size()];

        //Step 3 of Book's Natural Cubic Spline (pg. 147)
        iota[0] = 1;
        mu[0] = 0;
        zeta[0] = 0;

        //Step 4 of Book's Natural Cubic Spline (pg. 147)
        for(int i = 1; i < dataPoints.size() - 1; i++){
            iota[i] = 2*(dataPoints.get(i+1).getX() - dataPoints.get(i-1).getX()) - h[i-1]*mu[i-1];
            mu[i] = h[i] / iota[i];
            zeta[i] = (alpha[i] - h[i-1] * zeta[i - 1]) / iota[i];
        }

        //Step 5 of Book's Natural Cubic Spline (pg. 147)
        iota[dataPoints.size() - 1] = 1;
        mu[dataPoints.size() - 1] = 0;
        c[dataPoints.size() - 1] = 0;

        //Step 6 of Book's Natural Cubic Spline (pg. 147)
        for(int j = dataPoints.size() - 2; j >= 0; j--){
            c[j] = zeta[j] - mu[j] * c[j+1];
            b[j] = (a[j+1] - a[j]) / h[j] - h[j] * (c[j+1] + 2*c[j]) / 3;
            d[j] = (c[j+1] - c[j]) / (3*h[j]);
        }
    }

    public void addEquation(double[] coefficients, double start, double end){
        pieceWiseEquations.add(new PieceWiseEquation(coefficients, start, end));
    }

    public void getSplinePoints(double start, double end, double increment) {
        int currentPiece = 0;
        double max = 0;
        double max_x = 0;
        for (double x = start; x < end; x += increment) {
            if (x >= pieceWiseEquations.get(currentPiece).rangeStart && x < pieceWiseEquations.get(currentPiece).rangeEnd) {
                if (pieceWiseEquations.get(currentPiece).getPoint(x) > max) {
                    max = pieceWiseEquations.get(currentPiece).getPoint(x);
                    max_x = x;
                }
                double roundingValue = 1 / increment;
                double roundedPoint = Math.floor(pieceWiseEquations.get(currentPiece).getPoint(x) * roundingValue) / roundingValue;
                System.out.println(Math.floor(x * roundingValue) / roundingValue + " " + roundedPoint);
            } else if (currentPiece != pieceWiseEquations.size() - 1) {
                currentPiece++;
            } else {
                return;
            }
        }

        System.out.println("The maximum was " + max);
        System.out.println("It occured at " + max_x);
    }

    public double f(double x, double baseline) {
        for (PieceWiseEquation equation : pieceWiseEquations) {
            if (x >= equation.rangeStart && x < equation.rangeEnd) {
                return equation.getPoint(x) - baseline;
            }
        }

        return Double.NaN;
    }

    private class PieceWiseEquation
    {
        double rangeStart;
        double rangeEnd;

        double coefficients[] = new double[4];

        PieceWiseEquation(double[] coefficients, double rangeStart, double rangeEnd){
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;

            this.coefficients = coefficients;
        }

        double getPoint(double x){
            return coefficients[0] + coefficients[1] * (x - rangeStart)+ coefficients[2] * Math.pow((x - rangeStart), 2) + coefficients[3] * Math.pow((x - rangeStart), 3);
        }
    }

    public void findTop(Peak peak){
        double top = f(peak.startPoint, 0);
        int increments = 10000;
        for (int i = 1; i < increments; i++){

        }
    }
}
