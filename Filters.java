import java.util.ArrayList;

public class Filters {
    private static int[] seventeenPointSGCoefficients = {-21, -6, 7, 18, 27, 34, 39, 42, 43, 42, 39, 34, 27, 18, 7, -6, -21};
    private static int[] elevenPointSGCoefficients = {-36, 9, 44, 69, 84, 89, 84, 69, 44, 9, -36};
    private static int[] fivePointSGCoefficients = {-3, 12, 17, 12, -3};

    private static double fivePointSGNormalization = 35;
    private static double elevenPointSGNormalization = 429;
    private static double seventeenPointSGNormalization = 323;


    public static ArrayList<Point> applyBoxcar(ArrayList<Point> data, int filterSize){
        ArrayList<Point> filteredPoints = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            double x_coord = data.get(i).getX();
            double y_coord = 0;

            for(int j = (i - (filterSize - 1) / 2); j < (i + ((filterSize - 1) / 2) + 1); j++){
                if (j < 0){
                    y_coord += data.get(data.size() + j).getY() / filterSize;
                } else {
                    y_coord += data.get(j % data.size()).getY() / filterSize;
                }
            }

            filteredPoints.add(new Point(x_coord, y_coord));
        }

        return filteredPoints;
    }

    public static ArrayList<Point> applySavitzkyGolay(ArrayList<Point> data, int filterSize){
        ArrayList<Point> filteredPoints = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            double x_coord = data.get(i).getX();
            double y_coord = 0;

            for(int j = (-1 * ((filterSize - 1) / 2)); j < (((filterSize - 1) / 2) + 1); j++){
                 switch(filterSize) {
                     case 5:
                         if (i + j < 0) {
                             y_coord += fivePointSGCoefficients[j + 2] * data.get(data.size() + (i + j)).getY() / fivePointSGNormalization;
                         } else {
                             y_coord += fivePointSGCoefficients[j + 2] * data.get((i + j) % data.size()).getY() / fivePointSGNormalization;
                         }
                         break;

                     case 11:
                         if (i + j < 0) {
                             y_coord += elevenPointSGCoefficients[j + 5] * data.get(data.size() + (i + j)).getY() / elevenPointSGNormalization;
                         } else {
                             y_coord += elevenPointSGCoefficients[j + 5] * data.get((i + j) % data.size()).getY() / elevenPointSGNormalization;
                         }
                         break;

                     case 17:
                         if (i + j < 0) {
                             y_coord += seventeenPointSGCoefficients[j + 8] * data.get(data.size() + (i + j)).getY() / seventeenPointSGNormalization;
                         } else {
                             y_coord += seventeenPointSGCoefficients[j + 8] * data.get((i + j) % data.size()).getY() / seventeenPointSGNormalization;
                         }
                         break;
                 }
            }
            filteredPoints.add(new Point(x_coord, y_coord));
        }

        return filteredPoints;
    }

    public static ArrayList<Point> applyTMS(ArrayList<Point> data, double baseline){
        double highestPointAboveTMS = Double.NaN;
        for (Point point: data){
            if (point.getY() > baseline){
                if (Double.isNaN(highestPointAboveTMS)){
                    highestPointAboveTMS = point.getX();
                } else if (highestPointAboveTMS < point.getX()){
                    highestPointAboveTMS = point.getX();
                }
            }
        }

        System.out.println("Plot shifted " + highestPointAboveTMS + " ppm for TMS calibration");
        System.out.println("");

        ArrayList<Point> dataAdjustedForTMS = new ArrayList<>();

        if (!Double.isNaN(highestPointAboveTMS)){
            for (Point point: data){
                double adjustedX = point.getX() - highestPointAboveTMS;
                dataAdjustedForTMS.add(new Point(adjustedX, point.getY()));
            }
        }

        return dataAdjustedForTMS;
    }
}
