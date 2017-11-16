import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Settings {
    public String settingsFileName;
    public String inputFileName;
    public double baseline;
    public double tolerance;
    public int filterType;
    public int filterSize;
    public int numberOfPasses;
    public int integrationTechnique;
    public String outputFileName;

    public Settings(String fileName) throws IOException{
        settingsFileName = fileName;
        getSettings();
    }

    private void getSettings() throws IOException {
        Path settingsPath = Paths.get(settingsFileName);
        try (Scanner fileScanner = new Scanner(settingsPath)){
            inputFileName = truncateSpace(fileScanner.nextLine());
            baseline = Double.parseDouble(truncateSpace(fileScanner.nextLine()));
            tolerance = Double.parseDouble(truncateSpace(fileScanner.nextLine()));
            filterType = Integer.parseInt(truncateSpace(fileScanner.nextLine()));
            filterSize = Integer.parseInt(truncateSpace(fileScanner.nextLine()));
            numberOfPasses = Integer.parseInt(truncateSpace(fileScanner.nextLine()));
            integrationTechnique = Integer.parseInt(truncateSpace(fileScanner.nextLine()));
            outputFileName = truncateSpace(fileScanner.nextLine());

            fileScanner.close();
       }
    }

    private String truncateSpace(String inputString){
        if(inputString.contains(" ")){
            inputString = inputString.substring(0, inputString.indexOf(" "));
        }

        if(inputString.contains("\t")){
            inputString = inputString.substring(0, inputString.indexOf("\t"));
        }

        return inputString;
    }

    public void printSettings(){
        System.out.println("Program Options");
        System.out.println("==============================================");
        System.out.println("Baseline Adjustement \t:\t" + baseline);
        System.out.println("Tolerance \t:\t" + tolerance);
        switch(filterType){
            case(0): System.out.println("No filter");
                break;

            case(1): System.out.println("Boxcar Filtering");
                System.out.println("Boxcar Size (Cyclic)\t:\t" + filterSize);
                System.out.println("Boxcar Passes\t:\t" + numberOfPasses);
                break;

            case(2): System.out.println("Savitzky Golay Filtering");
                System.out.println("SG Filter Size (Cyclic)\t:\t" + filterSize);
                System.out.println("SG Filter Passes\t:\t" + numberOfPasses);
                break;
        }
        System.out.println();

        System.out.println("Integration Method");
        System.out.println("==============================================");
        switch(integrationTechnique){
            case(0): System.out.println("Composite Simpson");
                break;
            case(1): System.out.println("Romberg");
                break;
            case(2): System.out.println("Adaptive Quadrature");
                break;
            case(3): System.out.println("Gaussian Quadrature");
                break;
        }
        System.out.println();

        System.out.println("Plot File Data");
        System.out.println("==============================================");
        System.out.println("File: " + inputFileName);
    }
}
