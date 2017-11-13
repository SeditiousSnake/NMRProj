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
}
