package Preprocessing.ContentCleaning;

import Config.ProcessConfig;
import Preprocessing.Resources.ResourceProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ContentCleaningRoot
{
    //TODO set to true if only player/club/trainer lists should be preprocessed [should be done at least once]
    private static final boolean FILTER_PREPROCESSING_ONLY = false;

    public static void main(String[] args)
    {
        //region Local Paths

        //TODO Thomas local
        String contentDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\raw_full";
        String saveDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\preprocessed";
        String tokenFilterBase = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
        String filterOutputDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter\\preprocessed";

        //TODO Thomas Laptop local
//        String contentDirectory = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\raw_full";
//        String saveDirectory = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\preprocessed";
//        String playerFileBase = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
//        String clubFileBase = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
//        String trainerFileBase = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
//        String filterOutputDirectory = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter\\preprocessed";

        //endregion

        //region Global Paths

        String stopwordFile = ResourceProvider.getStopwordPath();

        //endregion

        ProcessConfig config = new ProcessConfig();
        if(config.getConfigureForTokenFilterPreprocessing())
        {
            String playerFileRaw = tokenFilterBase + "\\raw\\player.txt";
            String clubFileRaw = tokenFilterBase + "\\raw\\clubs.txt";
            String trainerFileRaw = tokenFilterBase + "\\raw\\trainer.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(config, stopwordFile, playerFileRaw, clubFileRaw, trainerFileRaw);
            documentCleaner.preprocessFilterFiles(filterOutputDirectory);
            return;
        }

        try(Stream<Path> contentStream = Files.walk(Paths.get(contentDirectory)))
        {
            String playerFilePreprocessed = tokenFilterBase + "\\preprocessed\\playerList_preprocessed.txt";
            String clubFilePreprocessed = tokenFilterBase + "\\preprocessed\\clubsList_preprocessed.txt";
            String trainerFilePreprocessed = tokenFilterBase + "\\preprocessed\\trainerList_preprocessed.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(config, stopwordFile, playerFilePreprocessed, clubFilePreprocessed, trainerFilePreprocessed);
            contentStream.filter(Files::isRegularFile).forEach(x -> documentCleaner.cleanProcess(x, saveDirectory));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}