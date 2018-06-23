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
        //region Global Paths

        String stopwordFile = ResourceProvider.getStopwordPath();

        //endregion

        //region Config Init

        ContentProcessConfig config = new ContentProcessConfig();

        //endregion

        //region Token-Filter Preprocessing

        if(config.getConfigureForTokenFilterPreprocessing())
        {
            String playerFileRaw = config.getFilterDirectoryBase() + "\\raw\\player.txt";
            String clubFileRaw = config.getFilterDirectoryBase() + "\\raw\\clubs.txt";
            String trainerFileRaw = config.getFilterDirectoryBase() + "\\raw\\trainer.txt";
            String stadiumsFileRaw = config.getFilterDirectoryBase() + "\\raw\\stadiums.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(config, stopwordFile, playerFileRaw, clubFileRaw, trainerFileRaw, stadiumsFileRaw);
            documentCleaner.preprocessFilterFiles(config.getFilterDirectoryBase() + "\\preprocessed");
            return;
        }

        //endregion

        //region Content-File Preprocessing

        try(Stream<Path> contentStream = Files.walk(Paths.get(config.getContentInputDirectory())))
        {
            String playerFilePreprocessed = config.getFilterDirectoryBase() + "\\preprocessed\\playerList_preprocessed.txt";
            String clubFilePreprocessed = config.getFilterDirectoryBase() + "\\preprocessed\\clubsList_preprocessed.txt";
            String trainerFilePreprocessed = config.getFilterDirectoryBase() + "\\preprocessed\\trainerList_preprocessed.txt";
            String stadiumsFilePreprocessed = config.getFilterDirectoryBase() + "\\preprocessed\\stadiumsList_preprocessed.txt";

            DocumentCleaner documentCleaner = new DocumentCleaner(config, stopwordFile, playerFilePreprocessed, clubFilePreprocessed, trainerFilePreprocessed, stadiumsFilePreprocessed);
            contentStream.filter(Files::isRegularFile).forEach(x -> documentCleaner.cleanProcess(x, config.getContentOutputDirectory()));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //endregion
    }
}