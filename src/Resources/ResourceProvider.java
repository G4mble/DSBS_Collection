package Resources;

public class ResourceProvider
{
    //region Public Methods

    public static String getStopwordPath()
    {
        return getLocalResourcePath("stopwords.txt");
    }

    public static String getContentConfigPath()
    {
        return getLocalResourcePath("contentCleaner_Config.cfg");
    }

    public static String getCSVConfigPath()
    {
        return getLocalResourcePath("csvExtractor_Config.cfg");
    }

    //endregion

    //region Private Methods

    private static String getLocalResourcePath(String fileName)
    {
        return ResourceProvider.class.getResource(fileName).getPath();
    }

    //endregion
}