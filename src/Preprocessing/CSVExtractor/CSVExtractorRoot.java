package Preprocessing.CSVExtractor;

import Config.CSVProcessConfig;

public class CSVExtractorRoot
{
    public static void main(String[] args)
    {
        CSVProcessConfig config = new CSVProcessConfig();
        CSVFactsPreprocessor preprocessor = new CSVFactsPreprocessor(config);
        preprocessor.runProcess();
    }
}