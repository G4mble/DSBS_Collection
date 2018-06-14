package Preprocessing.Resources;

public class ResourceProvider
{
    public static String getStopwordPath()
    {
        return getLocalResourcePath("stopwords.txt");
    }

    public static String getConfigPath()
    {
        return getLocalResourcePath("cleanerConfig.cfg");
    }

    private static String getLocalResourcePath(String fileName)
    {
        return ResourceProvider.class.getResource(fileName).getPath();
    }
}