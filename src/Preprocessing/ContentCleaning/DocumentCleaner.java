package Preprocessing.ContentCleaning;

import Config.ContentProcessConfig;
import Preprocessing.TokenReplace.DFLReplacer;
import Utility.CollectionHelper;
import Utility.FileHelper;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;

public class DocumentCleaner
{
    //region Fields

    private Set<String> _stopwords;
    private Set<String> _playerList;
    private Set<String> _clubList;
    private Set<String> _trainerList;

    private final ContentProcessConfig _config;

    //endregion

    //region Constructors

    public DocumentCleaner(ContentProcessConfig config, String stopwordFile, String playerFile, String clubFile, String trainerFile)
    {
        _config = config;
        loadStopwords(stopwordFile);
        loadPlayers(playerFile);
        loadClubs(clubFile);
        loadTrainers(trainerFile);
    }

    //endregion

    //region Private Methods

    private void loadStopwords(String fileName)
    {
        System.out.println("INFO: Loading stopwords from file...");
        try
        {
            _stopwords = FileHelper.loadDocumentLinesToSet(fileName, Charset.forName("UTF-8"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void loadPlayers(String fileName)
    {
        System.out.println("INFO: Loading playerNames from file...");
        try
        {
            _playerList = FileHelper.loadDocumentLinesToSet(fileName, Charset.forName("UTF-8"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void loadClubs(String fileName)
    {
        System.out.println("INFO: Loading clubNames from file...");
        try
        {
            _clubList = FileHelper.loadDocumentLinesToSet(fileName, Charset.forName("ISO-8859-1"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void loadTrainers(String fileName)
    {
        System.out.println("INFO: Loading trainerNames from file...");
        try
        {
            _trainerList = FileHelper.loadDocumentLinesToSet(fileName, Charset.forName("UTF-8"));
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
            if(_config.getRemoveStopwords())
                inputSplit = removeStopwords(inputSplit);
            if(_config.getCheckTokenMinLength())
                inputSplit = ensureTokenMinLength(inputSplit);

            input = CollectionHelper.collectionToString(inputSplit);
        }
        input = normalizeWhitespaces(input);
        return input.trim();
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
        return content.toString().trim();
    }

    private List<String> ensureTokenMinLength(List<String> input)
    {
        Set<String> toRemove = new HashSet<>();
        for(String word:input)
        {
            if(word.length() < _config.getTokenMinLength())
                toRemove.add(word);
        }
        input.removeAll(toRemove);
        return input;
    }

    private List<String> removeStopwords(List<String> input)
    {
        input.removeAll(_stopwords);
        return input;
    }

    private String replaceBundesligaCom(String input)
    {
        return input.replace("bundesliga.com", " ");
    }

    private String transformToLowerCaseTrim(String input)
    {
        return input.toLowerCase().trim();
    }

    private String fixWhitespaces(String input)
    {
        return input.replaceAll("\\u00A0", " ");
    }

    private String normalizeWhitespaces(String input)
    {
        return input.trim().replaceAll("[ ]+", " ");
    }

    private String replaceUmlauts(String input)
    {
        return input.replaceAll("ü", "ue").replaceAll("ä", "ae")
                    .replaceAll("ö", "oe").replaceAll("ß", "ss");
    }

    private String replaceDates(String input)
    {
        //remove all dates separated by dot, hyphen or slash [dd.MM.YY{YY} OR MM.dd.YY{YY} OR d.M.YY{YY} OR M.d.YY{YY}]
        return input.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
    }

    private String stripPunctuation(String input)
    {
        return input.replaceAll("[?!,;.:]+", " ");
    }

    private String replaceSpecialCharactersWithWhitespace(String input)
    {
        input = input.replaceAll("[<>_]+", " ");
        return input.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'’\\-|^°@€]+", " ");
    }

    private String normalizeText(String input)
    {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        return input.replaceAll("[^\\p{ASCII}]", "");
    }

    private String replaceNonAsciiCharacters(String input)
    {
        return input.replaceAll("[^\\x00-\\x7F]+", " ").replaceAll("\\uFFFD", " ");
    }

    private String fixMacEncoding(String input)
    {
        return input.replaceAll("([a-z])([A-Z]+)", "$1 $2");
    }

    private String replaceHypenAndApostrope(String input)
    {
        return input.replaceAll("['-]+", " ");
    }

    private String unescapeText(String input)
    {
        input = StringEscapeUtils.unescapeXml(input);
        input = StringEscapeUtils.unescapeJava(input);
        input = input.replace("c;", "c");
        input = input.replace("ø", "oe");
        return input;
    }

    //endregion

    //region Public Methods

    public void cleanProcess(Path path, String saveDirectory)
    {
        //entry point
        try
        {
            System.out.println("INFO: Processing " + path + " ...");

            File contentFile = path.toFile();
            File outputFile = new File(saveDirectory + "\\" + contentFile.getName().replace(".txt", "") + "_preprocessed.txt");
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();

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
                FileHelper.writeContentToExistingFile(builder.toString().trim(), outputFile);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void preprocessFilterFiles(String saveDirectory)
    {
        //entry point
        try
        {
            System.out.println("INFO: Preprocessing filter files...");

            File playerOutputFile;
            File trainerOutputFile;
            File clubsOutputFile;
            try
            {
                playerOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\playerList_preprocessed.txt");
                trainerOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\trainerList_preprocessed.txt");
                clubsOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\clubsList_preprocessed.txt");
            }
            catch (Exception ex)
            {
                System.out.println("Terminating...");
                ex.printStackTrace();
                return;
            }

            String playerContent = processFilterTokensPerWord(_playerList, _config.getSplitPlayerTokens());
            FileHelper.writeContentToExistingFile(playerContent.trim(), playerOutputFile);

            String trainerContent = processFilterTokensPerWord(_trainerList, _config.getSplitTrainerTokens());
            FileHelper.writeContentToExistingFile(trainerContent.trim(), trainerOutputFile);

            String clubsContent = processFilterTokensPerWord(_clubList, _config.getSplitClubTokens());
            FileHelper.writeContentToExistingFile(clubsContent.trim(), clubsOutputFile);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //endregion
}