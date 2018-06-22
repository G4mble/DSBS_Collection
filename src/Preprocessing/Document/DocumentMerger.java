package Preprocessing.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DocumentMerger
{
    private static final String _firstSource = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\abstracts\\raw";
    private static final String _secondSource = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\raw";
    private static final String _saveDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\__combined\\raw";

    public static void main(String[] args)
    {
        processDocuments();
    }

    private static String getSecond(Path first)
    {
        String[] fileNameElements = first.toFile().getName().split("-");
        return _secondSource + "\\" + fileNameElements[0] + "-content.txt";
    }

    private static void mergeDocuments(Path first)
    {
        System.out.println("Processing " + first + " ...");
        try
        {
            String second = getSecond(first);
            List<String> firstContent = Files.readAllLines(first);
            List<String> secondContent = Files.readAllLines(Paths.get(second));
            firstContent.addAll(secondContent);
            File outputFile = new File(_saveDirectory + "\\" + first.toFile().getName().replace(".txt", "").replace("abstract", "combined") + ".txt");
            Files.write(outputFile.toPath(), firstContent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void processDocuments()
    {
        try
        {
            Stream<Path> firstStream = Files.walk(Paths.get(_firstSource));
            firstStream.filter(Files::isRegularFile).forEach(x -> mergeDocuments(x));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
