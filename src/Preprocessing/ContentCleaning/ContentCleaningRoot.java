package Preprocessing.ContentCleaning;

import Config.ContentProcessConfig;
import Resources.ResourceProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ContentCleaningRoot
{
    public static void main(String[] args)
    {
        //region Local Paths

        //TODO Thomas local
        String contentDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\title\\extracted_raw";
        String saveDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\title\\preprocessed";
//        String contentDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\abstracts\\raw";
//        String saveDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\abstracts\\preprocessed";
        //TODO select one depending on acbstrac or content
//        String contentDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\abstracts\\raw";
//        String saveDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\abstracts\\preprocessed";
        String tokenFilterBase = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
        String filterOutputDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter\\preprocessed";

        //TODO Thomas Laptop local
//        String contentDirectory = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\raw_full";
//        String saveDirectory = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\preprocessed";
//        String tokenFilterBase = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
//        String filterOutputDirectory = "D:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter\\preprocessed";

        //endregion

        //region Global Paths

        String stopwordFile = ResourceProvider.getStopwordPath();

        //endregion

        //region Config Init

        ContentProcessConfig config = new ContentProcessConfig();

        //endregion

        //region Token-Filter Preprocessing

        if(config.getConfigureForTokenFilterPreprocessing())
        {
            String playerFileRaw = tokenFilterBase + "\\raw\\player.txt";
            String clubFileRaw = tokenFilterBase + "\\raw\\clubs.txt";
            String trainerFileRaw = tokenFilterBase + "\\raw\\trainer.txt";
            String stadiumsFileRaw = tokenFilterBase + "\\raw\\stadiums.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(config, stopwordFile, playerFileRaw, clubFileRaw, trainerFileRaw, stadiumsFileRaw);
            documentCleaner.preprocessFilterFiles(filterOutputDirectory);
            return;
        }

        //endregion

        //region Content-File Preprocessing

        try(Stream<Path> contentStream = Files.walk(Paths.get(contentDirectory)))
        {
            String playerFilePreprocessed = tokenFilterBase + "\\preprocessed\\playerList_preprocessed.txt";
            String clubFilePreprocessed = tokenFilterBase + "\\preprocessed\\clubsList_preprocessed.txt";
            String trainerFilePreprocessed = tokenFilterBase + "\\preprocessed\\trainerList_preprocessed.txt";
            String stadiumsFilePreprocessed = tokenFilterBase + "\\preprocessed\\stadiumsList_preprocessed.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(config, stopwordFile, playerFilePreprocessed, clubFilePreprocessed, trainerFilePreprocessed, stadiumsFilePreprocessed);
            contentStream.filter(Files::isRegularFile).forEach(x -> documentCleaner.cleanProcess(x, saveDirectory));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //endregion
    }
}