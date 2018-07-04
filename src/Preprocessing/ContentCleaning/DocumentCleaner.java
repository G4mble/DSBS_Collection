package Preprocessing.ContentCleaning;

import Config.ContentProcessConfig;
import Preprocessing.TokenReplace.DFLReplacer;
import Utility.CollectionHelper;
import Utility.FileHelper;
import Utility.LanguageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;

public class DocumentCleaner
{
    //region Fields

    private Set<String> _stopwords;
    private Set<String> _playerList;
    private LinkedHashSet<String> _clubList;
    private Set<String> _trainerList;
    private Set<String> _stadiumsList;
    private Set<String> _monthsList;

    private final ContentProcessConfig _config;

    //endregion

    //region Constructors

    public DocumentCleaner(ContentProcessConfig config, String stopwordFile, String playerFile, String clubFile, String trainerFile, String stadiumsFile)
    {
        _config = config;
        _monthsList = new HashSet<>(Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"));
        loadStopwords(stopwordFile);
        loadPlayers(playerFile);
        loadClubs(clubFile);
        loadTrainers(trainerFile);
        loadStadiums(stadiumsFile);
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
//            _clubList = FileHelper.loadDocumentLinesToSet(fileName, Charset.forName("ISO-8859-1"));
            List<String> tmpClubList = FileHelper.loadDocumentLinesToList(fileName, Charset.forName("UTF-8"));

            tmpClubList.sort(Collections.reverseOrder(Comparator.comparingInt(String::length)));
            _clubList = new LinkedHashSet<>(tmpClubList);
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

    private void loadStadiums(String fileName)
    {
        System.out.println("INFO: Loading stadiumNames from file...");
        try
        {
            _stadiumsList = FileHelper.loadDocumentLinesToSet(fileName, Charset.forName("UTF-8"));
        }
        catch(Exception ex)
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

        input = replaceArticleReference(input);
        input = fixWhitespaces(input);
        input = fixMacEncoding(input);
        input = replaceCRLF(input);
        input = unescapeText(input);
        input = transformToLowerCaseTrim(input);
        input = replaceUmlauts(input);
        input = replaceDates(input);
        input = replaceBundesligaCom(input);
        input = replaceMatchResultsForLaterRemoval(input);
        input = stripPunctuation(input);
        input = replaceSpecialCharactersWithWhitespace(input);
        input = normalizeText(input);
        input = replaceNonAsciiCharacters(input);
        input = finallyReplaceMatchResults(input);

        if(_config.getRemoveStopwords() || _config.getRemoveMonths())
        {
            List<String> inputSplit = new ArrayList<>(Arrays.asList(input.split(" ")));

            if(_config.getRemoveStopwords())
                inputSplit = removeStopwords(inputSplit);
            if(_config.getRemoveMonths())
                inputSplit = removeMonths(inputSplit);

            input = CollectionHelper.collectionToString(inputSplit);
        }

        DFLReplacer dflReplacer = new DFLReplacer();
        if(_config.getReplaceStadiumTokens())
            input = dflReplacer.replaceTokenOnSentenceBasis(input, _stadiumsList, "<stadium_name>");
        if(_config.getReplaceClubTokens())
            input = dflReplacer.replaceTokenOnSentenceBasis(input, _clubList, "<club_name>");

        if(_config.getPerformSecondChargePerWordProcesses())
        {
            List<String> inputSplit = new ArrayList<>(Arrays.asList(input.split(" ")));

            if(_config.getReplaceTrainerTokens())
                inputSplit = dflReplacer.replaceTokenOnWordBasis(inputSplit, _trainerList, "<coach_name>");
            if(_config.getReplacePlayerTokens())
                inputSplit = dflReplacer.replaceTokenOnWordBasis(inputSplit, _playerList, "<player_name>");
            if(_config.getRemovePlayerTokens())
                inputSplit = dflReplacer.removeToken(inputSplit, _playerList);
            if(_config.getRemoveClubTokens())
                inputSplit = dflReplacer.removeToken(inputSplit, _clubList);
            if(_config.getRemoveTrainerTokens())
                inputSplit = dflReplacer.removeToken(inputSplit, _trainerList);
            if(_config.getUseStemming())
                inputSplit = stemTokens(inputSplit);
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
                    if(currentPart.length() >= 2)
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
        input = input.replaceAll("\u0092", " ");
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

    private List<String> stemTokens(List<String> input)
    {
        List<String> output = new ArrayList<>();
        for(String item:input)
        {
            item = LanguageUtils.stem(item);
            output.add(item);
        }
        return output;
    }

    private List<String> removeMonths(List<String> input)
    {
        input.removeAll(_monthsList);
        return input;
    }

    private String replaceMatchResultsForLaterRemoval(String input)
    {
        return input.replaceAll("\\s\\d+\\-\\d+[\\s\\.]", " grmblbtzmatchresult ");
    }

    private String finallyReplaceMatchResults(String input)
    {
        return input.replaceAll("\\bgrmblbtzmatchresult\\b", "<match_result>");
    }

    private String replaceArticleReference(String input)
    {
        input = input.replaceAll("artikel#(\\d){1,6}", "");
        input = input.replaceAll("Artikel#(\\d){1,6}", "");
        input = input.replace("artikel#", "");
        return input.replace("Artikel#", "");
    }

    private String replaceCRLF(String input)
    {
        input = input.replaceAll("\r", " ");
        return input.replaceAll("\n", " ");
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
            String outputFileName = saveDirectory + "\\" + contentFile.getName().replace(".txt", "") + "_preprocessed.txt";
            File outputFile = FileHelper.createFileAndDirectory(outputFileName);

            String fileContent = FileUtils.readFileToString(contentFile, StandardCharsets.UTF_8);
            fileContent = fullCleanProcessInternal(fileContent);
            FileHelper.writeContentToExistingFile(fileContent.trim(), outputFile);

//            String currentLine;
//            StringBuilder builder = new StringBuilder();
//            try(FileReader fr = new FileReader(contentFile);
//                BufferedReader reader = new BufferedReader(fr))
//            {
//                while((currentLine = reader.readLine()) != null)
//                {
//                    if(currentLine.length() < 1)
//                        continue;

//                    currentLine = fullCleanProcessInternal(currentLine);
//                    builder.append(currentLine).append(System.lineSeparator());
//                }
//                FileHelper.writeContentToExistingFile(builder.toString().trim(), outputFile);
//            }
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
            File stadiumsOutputFile;
            try
            {
                playerOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\playerList_preprocessed.txt");
                trainerOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\trainerList_preprocessed.txt");
                clubsOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\clubsList_preprocessed.txt");
                stadiumsOutputFile = FileHelper.createFileAndDirectory(saveDirectory + "\\stadiumsList_preprocessed.txt");
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

            String stadiumsContent = processFilterTokensPerWord(_stadiumsList, false);
            FileHelper.writeContentToExistingFile(stadiumsContent.trim(), stadiumsOutputFile);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //endregion
}