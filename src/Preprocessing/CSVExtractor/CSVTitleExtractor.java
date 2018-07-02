package Preprocessing.CSVExtractor;

import Utility.FileHelper;
import javafx.util.Pair;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CSVTitleExtractor
{
    private final String _inputPath = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\csv\\raw\\dim_web_articles.txt";
    private final String _outputPath = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\title\\extracted_raw";
    private final String _contentTypeSuffix = "title";

    public static void main(String[] args)
    {
        CSVTitleExtractor csvTitleExtractor = new CSVTitleExtractor();
        csvTitleExtractor.runProcess();
    }

    private String declutter(String input)
    {
        //only general cleaning that pertains to articleId AND apScore
        return input.replaceAll("[\"]", "");
    }

    private Pair<Long, String> processArticleId(String input) throws IllegalArgumentException
    {
        input = declutter(input);
        long id;
        try
        {
            //the articleID should be completely numeric
            id = Long.parseLong(input);
        }
        catch(Exception ex)
        {
            throw new IllegalArgumentException("Invalid articleID.");
        }
        input = "\\\\" + input + "-" + _contentTypeSuffix + "_preprocessed" + ".txt";
        return new Pair<>(id, input);
    }

    private String processTitleContent(String input)
    {
        return declutter(input);
    }

    private void runProcessInternal()
    {
        try
        {
            Path path = new File(_inputPath).toPath();
            List<String> excelLines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);

            long currentDocumentId;
            //skip the first line as it only contains headline information
            excelLines.remove(0);
            for(String currentLine:excelLines)
            {
                String[] wordsInLine = currentLine.split(";");
                try
                {
                    Pair<Long, String> articleIdPair = processArticleId(wordsInLine[0]);
                    currentDocumentId = articleIdPair.getKey();
                }
                catch(IllegalArgumentException ex)
                {
                    ex.printStackTrace();
                    continue;
                }
                String titleContent;
                try
                {
                    titleContent = processTitleContent(wordsInLine[1]);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
                String exportFileName = _outputPath + "\\"
                        + currentDocumentId
                        + "-" + _contentTypeSuffix + ".txt";
                File outputFile = FileHelper.createFileAndDirectory(exportFileName);
                if(outputFile == null)
                {
                    System.out.println("Output file null!");
                    return;
                }
                FileHelper.writeContentToExistingFile(titleContent.trim(), outputFile);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //endregion

    //region Public Methods

    public void runProcess()
    {
        //entry point
        try
        {
            System.out.println("INFO: Initiating CSVPreprocessing...");
            runProcessInternal();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //endregion
}
