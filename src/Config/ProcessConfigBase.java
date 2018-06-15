package Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public abstract class ProcessConfigBase
{
    //region Abstract Methods

    protected abstract boolean processLineContent(String line);

    //endregion

    protected void loadConfigurationFromFile(String path) throws IOException
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
    }

    protected void initializeInternal(String path) throws IOException
    {
        loadConfigurationFromFile(path);
    }
}