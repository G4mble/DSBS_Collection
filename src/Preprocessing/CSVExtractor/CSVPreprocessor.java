package Preprocessing.CSVExtractor;

import Config.CSVProcessConfig;
import Utility.CommonUtils;
import Utility.FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class CSVPreprocessor
{
    //region Fields

    private final CSVProcessConfig _config;

    //endregion

    //region Constructors

    public CSVPreprocessor(CSVProcessConfig config)
    {
        _config = config;
    }

    //endregion

    //region Private Methods

    private String declutter(String input)
    {
        //only general cleaning that pertains to articleId AND apScore
        return input.replaceAll("[\"]", "");
    }

    private String processArticleId(String input) throws IllegalArgumentException
    {
        input = declutter(input);
        try
        {
            //the articleID should be completely numeric
            Integer.parseInt(input);
        }
        catch(Exception ex)
        {
            throw new IllegalArgumentException("Invalid articleID.");
        }
        input = "\\\\" + input + "-" + _config.getContentTypeSuffix() + "_preprocessed" + ".txt";
        return input;
    }

    private int processApScore(String input) throws NumberFormatException
    {
        input = declutter(input);
        input = input.substring(0, 2);
        input = input.replaceAll("[.,]", "");

        int output = Integer.parseInt(input);
        if(output < 0 || output > 100)
            throw new NumberFormatException("Invalid APScore Range: " + output);
        return output;
    }

    private String createCSVLine(String[] input)
    {
        StringBuilder builder = new StringBuilder();
        for(String item:input)
        {
            builder.append("\"");
            builder.append(item);
            builder.append("\"");
            builder.append(";");
        }
        String output = builder.toString();
        return output.substring(0, output.length() - 1);
    }

    private void runProcessInternal()
    {
        try
        {
            File file = new File(_config.getCsvImportDirectory());
            StringBuilder builder = new StringBuilder();

            String exportFileName = _config.getCsvExportDirectory() + "\\" + _config.getExportFileName()
                    + (_config.getAddCurrentDateTimeToExportFileName() ? "_" + CommonUtils.getCurrentDateTimeString() : "")
                    + ".csv";
            File outputFile = FileHelper.createFileAndDirectory(exportFileName);
            if(outputFile == null)
            {
                System.out.println("Output file null!");
                return;
            }
            try(FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr))
            {
                Set<String> uniqueEntries = new HashSet<>();
                String currentLine;
                //first line csv config
                builder.append(createCSVLine(new String[] {"articleId", "APS"})).append(System.lineSeparator());
                //skip the first line as it only contains headline information
                reader.readLine();
                while((currentLine = reader.readLine()) != null)
                {
                    String[] wordsInLine = currentLine.split(";");
                    String articleId;
                    try
                    {
                        articleId = processArticleId(wordsInLine[1]);
                        if(uniqueEntries.contains(articleId))
                        {
                            if(_config.getShowPerLineDebugInfo())
                                System.out.println("WARNING: Duplicate articleID. Entry removed: " + currentLine);
                            continue;
                        }
                    }
                    catch(IllegalArgumentException ex)
                    {
                        if(_config.getShowPerLineDebugInfo())
                            System.out.println("WARNING: Invalid articleID. Entry removed: " + currentLine);
                        continue;
                    }
                    int apScore;
                    try
                    {
                        apScore = processApScore(wordsInLine[11]);
                    }
                    catch (NumberFormatException | IndexOutOfBoundsException ex)
                    {
                        if(_config.getShowPerLineDebugInfo())
                            System.out.println("WARNING: Invalid APScore value. Entry removed: " + currentLine);
                        continue;
                    }
                    uniqueEntries.add(articleId);
                    //ensure csv format
                    builder.append(createCSVLine(new String [] {articleId, String.valueOf(apScore)}));
                    builder.append(System.lineSeparator());
                }
            }
            FileHelper.writeContentToExistingFile(builder.toString().trim(), outputFile);
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