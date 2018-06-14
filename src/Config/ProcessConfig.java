package Config;

import Preprocessing.Resources.ResourceProvider;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

public class ProcessConfig
{
    private boolean replacePlayerTokens;
    private boolean replaceClubTokens;
    private boolean replaceTrainerTokens;
    private boolean removeTrainerTokens;
    private boolean removeClubTokens;
    private boolean removePlayerTokens;
    private boolean removeStopwords;

    private int tokenMinLength;
    private boolean splitTrainerTokens;
    private boolean splitClubTokens;
    private boolean splitPlayerTokens;
    private boolean checkTokenMinLength;
    private boolean configureForTokenFilterPreprocessing;

    public ProcessConfig()
    {
        initializeInternal();
    }

    private boolean processLineContent(String line)
    {
        line = line.replaceAll("\\%.*?\\%", "");
        if (line.length() == 0)
            return true; //config still valid as we just removed a commented line or hit an empty line

        String[] elements = line.split("=");
        if (elements.length != 2)
            return false;

        String leftHandSide = elements[0].trim();
        String rightHandSide = elements[1].trim();

        try
        {
            switch(leftHandSide)
            {
                case "replacePlayerTokens":
                    replacePlayerTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "replaceClubTokens":
                    replaceClubTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "replaceTrainerTokens":
                    replaceTrainerTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "removeStopwords":
                    removeStopwords = Boolean.parseBoolean(rightHandSide);
                    break;
                case "removePlayerTokens":
                    removePlayerTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "removeClubTokens":
                    removeClubTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "removeTrainerTokens":
                    removeTrainerTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "checkTokenMinLength":
                    checkTokenMinLength = Boolean.parseBoolean(rightHandSide);
                    break;
                case "tokenMinLength":
                    tokenMinLength = Integer.parseInt(rightHandSide);
                    break;
                case "splitPlayerTokens":
                    splitPlayerTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "splitClubTokens":
                    splitClubTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "splitTrainerTokens":
                    splitTrainerTokens = Boolean.parseBoolean(rightHandSide);
                    break;
                case "configureForTokenFilterPreprocessing":
                    configureForTokenFilterPreprocessing = Boolean.parseBoolean(rightHandSide);
                    break;
                default:
                    return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    private void loadConfigurationFromFile(String path)
    {
        try (Stream<String> lineStream = Files.lines(new File(path).toPath()))
        {
            for (String line : (Iterable<String>) lineStream::iterator)
            {
                if (!processLineContent(line))
                {
                    System.out.println("ERROR: Invalid configuration detected.");
                    return;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void initializeInternal()
    {
        loadConfigurationFromFile(ResourceProvider.getConfigPath());
    }

    public boolean getReplacePlayerTokens() {
        return replacePlayerTokens;
    }

    public boolean getReplaceClubTokens() {
        return replaceClubTokens;
    }

    public boolean getReplaceTrainerTokens() {
        return replaceTrainerTokens;
    }

    public boolean getRemoveTrainerTokens() {
        return removeTrainerTokens;
    }

    public boolean getRemoveClubTokens() {
        return removeClubTokens;
    }

    public boolean getRemovePlayerTokens() {
        return removePlayerTokens;
    }

    public boolean getRemoveStopwords() {
        return removeStopwords;
    }

    public int getTokenMinLength() {
        return tokenMinLength;
    }

    public boolean getSplitTrainerTokens() {
        return splitTrainerTokens;
    }

    public boolean getSplitClubTokens() {
        return splitClubTokens;
    }

    public boolean getSplitPlayerTokens() {
        return splitPlayerTokens;
    }

    public boolean getPerformPerWordProcesses()
    {
        return replaceClubTokens || replaceTrainerTokens || replacePlayerTokens ||
                removeClubTokens || removeTrainerTokens || removePlayerTokens ||
                removeStopwords || checkTokenMinLength;
    }

    public boolean getCheckTokenMinLength()
    {
        return checkTokenMinLength;
    }

    public boolean getConfigureForTokenFilterPreprocessing()
    {
        return configureForTokenFilterPreprocessing;
    }
}