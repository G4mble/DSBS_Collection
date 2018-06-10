package Preprocessing.ContentCleaning;

import Preprocessing.TokenReplace.DFLReplacer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class DocumentCleaner
{
    private List<String> _stopwords;
    private List<String> _playerList;
    private List<String> _clubList;
    private List<String> _trainerList;

    public DocumentCleaner(String stopwordFile, String playerFile, String clubFile, String trainerFile)
    {
        loadStopwords(stopwordFile);
        loadPlayers(playerFile);
        loadClubs(clubFile);
        loadTrainers(trainerFile);
    }

    private void loadStopwords(String fileName)
    {
        System.out.println("INFO: Loading stopwords from file...");

        _stopwords = new ArrayList<>();
        try
        {
            List<String> lines = Files.readAllLines(new File(fileName).toPath());
            _stopwords.addAll(lines);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void loadPlayers(String fileName)
    {
        System.out.println("INFO: Loading playerNames from file...");

        _playerList = new ArrayList<>();
        try
        {
            List<String> lines = Files.readAllLines(new File(fileName).toPath());
            _playerList.addAll(lines);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void loadClubs(String fileName)
    {
        System.out.println("INFO: Loading clubNames from file...");

        _clubList = new ArrayList<>();
        try
        {
            List<String> lines = Files.readAllLines(new File(fileName).toPath(), Charset.forName("ISO-8859-1"));
            _clubList.addAll(lines);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void loadTrainers(String fileName)
    {
        System.out.println("INFO: Loading trainerNames from file...");

        _trainerList = new ArrayList<>();
        try
        {
            List<String> lines = Files.readAllLines(new File(fileName).toPath());
            _trainerList.addAll(lines);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private String filterTokenCleanProcessInternal(String input)
    {
        //TODO WARNING: CHANGING THE ORDER MIGHT RESULT IN UNEXPECTED RESULTS

        input = fixWhitespaces(input);
        input = transformToLowerCaseTrim(input);
        input = replaceUmlauts(input);
        input = replaceDates(input);
        input = stripPunctuation(input);
        input = replaceSpecialCharacters(input);
        input = replaceNonAsciiCharacters(input);
        input = normalizeText(input);
        input = normalizeWhitespaces(input);
        return input.trim();
    }

    private String fullCleanProcessInternal(String input)
    {
        //TODO WARNING: CHANGING THE ORDER MIGHT RESULT IN UNEXPECTED RESULTS

        //TODO TS abbreviation removal

        input = fixWhitespaces(input);
        input = fixMacEncoding(input);
        input = transformToLowerCaseTrim(input);
        input = replaceUmlauts(input);
        input = replaceDates(input);
        input = stripPunctuation(input);
        input = replaceSpecialCharacters(input);
        input = replaceNonAsciiCharacters(input);
        input = normalizeText(input);
        {
            DFLReplacer dflReplacer = new DFLReplacer();
            input = dflReplacer.replaceToken(input, _playerList, "<token_player>");
            input = dflReplacer.replaceToken(input, _clubList, "<token_club>");
            input = dflReplacer.replaceToken(input, _trainerList, "<token_trainer>");
        }
        input = removeStopwords(input);
        input = normalizeWhitespaces(input);
        return input.trim();
    }

    private void writeContentToExistingFile(String content, File file)
    {
        try(FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw))
        {
            writer.write(content);
            writer.flush();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void cleanProcess(Path path, String saveDirectory)
    {
        System.out.println("INFO: Processing " + path + " ...");
        File contentFile;
        File outputFile;
        try
        {
            contentFile = path.toFile();
            outputFile = new File(saveDirectory + "\\" + contentFile.getName().replace(".txt", "") + "_preprocessed.txt");
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        }
        catch (Exception ex)
        {
            System.out.println("Terminating...");
            ex.printStackTrace();
            return;
        }

        String currentLine;
        StringBuilder builder = new StringBuilder();
        try(FileReader fr = new FileReader(contentFile);
            BufferedReader reader = new BufferedReader(fr))
        {
            while((currentLine = reader.readLine()) != null)
            {
                if(currentLine.length() < 1)
                    continue;

                currentLine = fullCleanProcessInternal(currentLine);
                builder.append(currentLine).append(System.lineSeparator());
            }
            writeContentToExistingFile(builder.toString().trim(), outputFile);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void preprocessFilterFiles(String saveDirectory)
    {
        System.out.println("INFO: Preprocessing filter files...");

        File playerOutputFile;
        File trainerOutputFile;
        File clubsOutputFile;
        try
        {
            playerOutputFile = new File(saveDirectory + "\\playerList_preprocessed.txt");
            playerOutputFile.getParentFile().mkdirs();
            playerOutputFile.createNewFile();

            trainerOutputFile = new File(saveDirectory + "\\trainerList_preprocessed.txt");
            trainerOutputFile.getParentFile().mkdirs();
            trainerOutputFile.createNewFile();

            clubsOutputFile = new File(saveDirectory + "\\clubsList_preprocessed.txt");
            clubsOutputFile.getParentFile().mkdirs();
            clubsOutputFile.createNewFile();
        }
        catch (Exception ex)
        {
            System.out.println("Terminating...");
            ex.printStackTrace();
            return;
        }

        StringBuilder playerContent = new StringBuilder();
        for(String player:_playerList)
        {
            player = filterTokenCleanProcessInternal(player);
            playerContent.append(player).append(System.lineSeparator());
        }
        writeContentToExistingFile(playerContent.toString().trim(), playerOutputFile);

        StringBuilder trainerContent = new StringBuilder();
        for(String trainer:_trainerList)
        {
            trainer = filterTokenCleanProcessInternal(trainer);
            trainerContent.append(trainer).append(System.lineSeparator());
        }
        writeContentToExistingFile(trainerContent.toString().trim(), trainerOutputFile);

        StringBuilder clubsContent = new StringBuilder();
        for(String club:_clubList)
        {
            club = filterTokenCleanProcessInternal(club);
            clubsContent.append(club).append(System.lineSeparator());
        }
        writeContentToExistingFile(clubsContent.toString().trim(), clubsOutputFile);
    }

    public String removeStopwords(String input)
    {
        String[] wordsInLine = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for(String word:wordsInLine)
        {
            if(_stopwords.contains(word))
                continue;
            builder.append(word).append(" ");
        }
        return builder.toString().trim();
    }

    public String transformToLowerCaseTrim(String input)
    {
        return input.toLowerCase().trim();
    }

    public String fixWhitespaces(String input)
    {
        return input.replaceAll("\\u00A0", " ");
    }

    public String normalizeWhitespaces(String input)
    {
        return input.trim().replaceAll("[ ]+", " ");
    }

    public String replaceUmlauts(String input)
    {
        return input.replaceAll("ü", "ue").replaceAll("ä", "ae")
                    .replaceAll("ö", "oe").replaceAll("ß", "ss");
    }

    public String replaceDates(String input)
    {
        //remove all dates separated by dot, hyphen or slash [dd.MM.YY{YY} OR MM.dd.YY{YY} OR d.M.YY{YY} OR M.d.YY{YY}]
        return input.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
    }

    public String stripPunctuation(String input)
    {
        return input.replaceAll("[?!,;.:]+", " ");
    }

    public String replaceUnderscoreAngleBracketApostrophe(String input)
    {
        return input.replaceAll("[<>_']", " ");
    }

    public String replaceSpecialCharacters(String input)
    {
        input = input.replaceAll("[<>_]+", " ");
        return input.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'\\-|^°@€]+", " ");
    }
    
    public String normalizeText(String input)
    {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        return input.replaceAll("[^\\p{ASCII}]", "");
    }

    public String replaceNonAsciiCharacters(String input)
    {
        return input.replaceAll("[^\\x00-\\x7F]+", " ").replaceAll("\\uFFFD", " ");
    }

    public String fixMacEncoding(String input)
    {
        return input.replaceAll("([a-z])([A-Z]+)", "$1 $2");
    }
}