package Utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FileHelper
{
    public static void writeContentToExistingFile(String content, File file) throws IOException
    {
        try(FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw))
        {
            writer.write(content);
            writer.flush();
        }
    }

    public static File createFileAndDirectory(String fullName) throws IOException
    {
        File file = new File(fullName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }

    public static Set<String> loadDocumentLinesToSet(String fileName, Charset charset) throws IOException
    {
        List<String> lines = Files.readAllLines(new File(fileName).toPath(), charset);
        return new HashSet<>(lines);
    }

    public static LinkedHashSet<String> loadDocumentLinesToLinkedHashSet(String fileName, Charset charset) throws IOException
    {
        List<String> lines = Files.readAllLines(new File(fileName).toPath(), charset);
        return new LinkedHashSet<>(lines);
    }

    public static List<String> loadDocumentLinesToList(String fileName, Charset charset) throws IOException
    {
        return Files.readAllLines(new File(fileName).toPath(), charset);
    }
}