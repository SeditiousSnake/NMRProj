import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Point implements Comparable<Point>{
    private double x;
    private double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public void setX(double x) { this.x = x;};

    public double getY(){
        return y;
    }

    public void setY(double y) { this.y = y;};

    public static ArrayList<Point> getData(String inputFile) throws IOException {
        ArrayList<Point> points = new ArrayList<>();

        Path filePath = Paths.get(inputFile);

        try(Scanner inputScanner = new Scanner(filePath)){
            while(inputScanner.hasNext()){
                points.add(getDataPoint(inputScanner.nextLine()));
            }
        }

        return points;
    }

    private static Point getDataPoint(String inputLine){
        String x_coord;
        String y_coord;

        x_coord = inputLine.substring(0, inputLine.indexOf(" "));
        y_coord = inputLine.substring(inputLine.indexOf(" ") + 1);

        return new Point(Double.parseDouble(x_coord), Double.parseDouble(y_coord));
    }

    public void print(){
        System.out.println(x + ", " + y);
    }

    public int compareTo(Point comparePoint){
        return this.getX() > comparePoint.getX() ? 1 : -1;
    }
}
