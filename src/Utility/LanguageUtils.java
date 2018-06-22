package Utility;

import org.tartarus.snowball.ext.PorterStemmer;

public class LanguageUtils
{
    private static PorterStemmer _porterStemmer = new PorterStemmer();

    public static String stem(String input)
    {
        _porterStemmer.setCurrent(input);
        _porterStemmer.stem();
        return _porterStemmer.getCurrent();
    }
}
