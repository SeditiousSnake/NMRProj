public class Peak {
    public double startPoint;
    public double endPoint;
    public double position;
    public double area;

    public Peak(double startPoint, double endPoint){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.position = (startPoint + endPoint) / 2;
    }

    public void print(){
        System.out.println(startPoint + ", " + endPoint);
    }
}
