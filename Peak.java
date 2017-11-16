public class Peak {
    public double startPoint;
    public double endPoint;
    public double position;
    public double area;
    public double top;
    public int hydrogens;

    public Peak(double startPoint, double endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.position = (startPoint + endPoint) / 2;
    }

    public void findTop(double baseline, CubicSpline spline){
        double currentTop = spline.f(startPoint, baseline);
        double currentX = startPoint;

        while(currentX < endPoint){
            currentX += .00001;
            double currentY = spline.f(currentX, baseline);
            if(currentY > currentTop){
                currentTop = currentY;
            }
        }

        top = currentTop;
    }

    public void print(int peakNumber) {
        System.out.println("Peak " + peakNumber);
        System.out.println("\tBegin: " + startPoint +" \tEnd: " + endPoint + " \tLocation: " + (startPoint + endPoint / 2));
        System.out.println("\tTop: " + top);
        System.out.println("\tArea: " + area);
        System.out.println("\tHydrogens: " + hydrogens);
    }
}
