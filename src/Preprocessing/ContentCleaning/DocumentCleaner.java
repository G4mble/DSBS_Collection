package Preprocessing.ContentCleaning;

import Config.ProcessConfig;
import Preprocessing.TokenReplace.DFLReplacer;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;

public class DocumentCleaner
{
    private Set<String> _stopwords;
    private Set<String> _playerList;
    private Set<String> _clubList;
    private Set<String> _trainerList;

    private ProcessConfig _config;

    public DocumentCleaner(String stopwordFile, String playerFile, String clubFile, String trainerFile)
    {
        loadStopwords(stopwordFile);
        loadPlayers(playerFile);
        loadClubs(clubFile);
        loadTrainers(trainerFile);
    }

    public DocumentCleaner(ProcessConfig config, String stopwordFile, String playerFile, String clubFile, String trainerFile)
    {
        this(stopwordFile, playerFile, clubFile, trainerFile);
        _config = config;
    }

    private void loadStopwords(String fileName)
    {
        System.out.println("INFO: Loading stopwords from file...");

        _stopwords = new HashSet<>();
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

        _playerList = new HashSet<>();
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

        _clubList = new HashSet<>();
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

        _trainerList = new HashSet<>();
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
        input = unescapeText(input);
        input = transformToLowerCaseTrim(input);
        input = replaceUmlauts(input);
        input = replaceDates(input);
        input = stripPunctuation(input);
        input = replaceSpecialCharactersWithWhitespace(input);
        input = normalizeText(input);
        input = replaceNonAsciiCharacters(input);
        input = normalizeWhitespaces(input);
        return input.trim();
    }

    private String fullCleanProcessInternal(String input)
    {
        //TODO WARNING: CHANGING THE ORDER MIGHT RESULT IN UNEXPECTED RESULTS

        //TODO TS abbreviation removal

        input = replaceBundesligaCom(input);
        input = fixWhitespaces(input);
        input = fixMacEncoding(input);
        input = unescapeText(input);
        input = transformToLowerCaseTrim(input);
        input = replaceUmlauts(input);
        input = replaceDates(input);
        input = stripPunctuation(input);
        input = replaceSpecialCharactersWithWhitespace(input);
        input = normalizeText(input);
        input = replaceNonAsciiCharacters(input);

        if(_config.getPerformPerWordProcesses())
        {
            List<String> inputSplit = new ArrayList<>(Arrays.asList(input.split(" ")));
            {
                DFLReplacer dflReplacer = new DFLReplacer();
                if(_config.getReplacePlayerTokens())
                    inputSplit = dflReplacer.replaceTokenTest(inputSplit, _playerList, "<token_player>");
                if(_config.getReplaceClubTokens())
                    inputSplit = dflReplacer.replaceTokenTest(inputSplit, _clubList, "<token_club>");
                if(_config.getReplaceTrainerTokens())
                    inputSplit = dflReplacer.replaceTokenTest(inputSplit, _trainerList, "<token_trainer>");
                if(_config.getRemovePlayerTokens())
                    inputSplit = dflReplacer.removeToken(inputSplit, _playerList);
                if(_config.getRemoveClubTokens())
                    inputSplit = dflReplacer.removeToken(inputSplit, _clubList);
                if(_config.getRemoveTrainerTokens())
                    inputSplit = dflReplacer.removeToken(inputSplit, _trainerList);
            }

            StringBuilder builder = new StringBuilder();
            inputSplit.forEach(x -> builder.append(x).append(" "));
            input = builder.toString().trim();
        }

        if(_config.getRemoveStopwords())
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

    private String processFilterTokensPerWord(Set<String> tokens, boolean splitTokens)
    {
        StringBuilder content = new StringBuilder();
        for(String currentToken:tokens)
        {
            currentToken = replaceHypenAndApostrope(currentToken);

            if(splitTokens)
            {
                String[] tokenParts = currentToken.split(" ");
                for(String currentPart:tokenParts)
                {
                    currentPart = filterTokenCleanProcessInternal(currentPart);
                    if(currentPart.length() >= 3)
                        content.append(currentPart).append(System.lineSeparator());
                }
            }
            else
            {
                currentToken = filterTokenCleanProcessInternal(currentToken);
                content.append(currentToken).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    private File createFileAndDirectory(String fullName) throws IOException
    {
        File file = new File(fullName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }

    public void preprocessFilterFiles(String saveDirectory)
    {
        System.out.println("INFO: Preprocessing filter files...");

        File playerOutputFile;
        File trainerOutputFile;
        File clubsOutputFile;
        try
        {
            playerOutputFile = createFileAndDirectory(saveDirectory + "\\playerList_preprocessed.txt");
            trainerOutputFile = createFileAndDirectory(saveDirectory + "\\trainerList_preprocessed.txt");
            clubsOutputFile = createFileAndDirectory(saveDirectory + "\\clubsList_preprocessed.txt");
        }
        catch (Exception ex)
        {
            System.out.println("Terminating...");
            ex.printStackTrace();
            return;
        }

        String playerContent = processFilterTokensPerWord(_playerList, _config.getSplitPlayerTokens());
        writeContentToExistingFile(playerContent.trim(), playerOutputFile);

        String trainerContent = processFilterTokensPerWord(_trainerList, _config.getSplitTrainerTokens());
        writeContentToExistingFile(trainerContent.trim(), trainerOutputFile);

        String clubsContent = processFilterTokensPerWord(_clubList, _config.getSplitClubTokens());
        writeContentToExistingFile(clubsContent.trim(), clubsOutputFile);
    }

    public String removeStopwords(List<String> input)
    {
        input.removeAll(_stopwords);
        StringBuilder builder = new StringBuilder();
        input.forEach(x -> builder.append(x).append(" "));
        return builder.toString().trim();
    }

    public String removeStopwords(String input)
    {
        String[] wordsInLine = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for(String word:wordsInLine)
        {
            if(_stopwords.contains(word) || word.length() < _config.getTokenMinLength())
                continue;
            builder.append(word).append(" ");
        }
        return builder.toString().trim();
    }

    public String replaceBundesligaCom(String input)
    {
        return input.replace("bundesliga.com", " ");
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

    public String replaceSpecialCharactersWithWhitespace(String input)
    {
        input = input.replaceAll("[<>_]+", " ");
        return input.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'’\\-|^°@€]+", " ");
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

    public String replaceHypenAndApostrope(String input)
    {
        return input.replaceAll("['-]+", " ");
    }

    public String unescapeText(String input)
    {
        input = StringEscapeUtils.unescapeXml(input);
        input = StringEscapeUtils.unescapeJava(input);
        input = input.replace("c;", "c");
        input = input.replace("ø", "oe");
        return input;
    }
}