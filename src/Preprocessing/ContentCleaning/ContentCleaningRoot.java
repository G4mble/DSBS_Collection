package Preprocessing.ContentCleaning;

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
        //TODO Thomas local
        String contentDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\raw_full";
        String saveDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\001_Article Data\\contents\\preprocessed";
        String stopwordFile = "C:\\Users\\Tommy\\Documents\\IntelliJIdea\\Projects\\DSBS_Collection\\src\\resources\\stopwords.txt";
        String playerFileBase = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
        String clubFileBase = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
        String trainerFileBase = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter";
        String filterOutputDirectory = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter\\preprocessed";

        if(FILTER_PREPROCESSING_ONLY)
        {
            String playerFileRaw = playerFileBase + "\\raw\\player.txt";
            String clubFileRaw = clubFileBase + "\\raw\\clubs.txt";
            String trainerFileRaw = trainerFileBase + "\\raw\\trainer.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(stopwordFile, playerFileRaw, clubFileRaw, trainerFileRaw);
            documentCleaner.preprocessFilterFiles(filterOutputDirectory);
            return;
        }

        try(Stream<Path> contentStream = Files.walk(Paths.get(contentDirectory)))
        {
            String playerFilePreprocessed = playerFileBase + "\\preprocessed\\playerList_preprocessed.txt";
            String clubFilePreprocessed = clubFileBase + "\\preprocessed\\clubsList_preprocessed.txt";
            String trainerFilePreprocessed = trainerFileBase + "\\preprocessed\\trainerList_preprocessed.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(stopwordFile, playerFilePreprocessed, clubFilePreprocessed, trainerFilePreprocessed);
            contentStream.filter(Files::isRegularFile).forEach(x -> documentCleaner.cleanProcess(x, saveDirectory));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}