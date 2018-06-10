package Preprocessing.TokenReplace;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DFLReplacer
{
    public String replaceToken(String content, List<String> tokens, String tokenReplacer)
    {
        ArrayList<String> longestMatches = new ArrayList<>();
        // test if title contains tokens
        for (String t : tokens)
        {
            if (t.length() > 1 && content.contains(t.trim()))
            {
                longestMatches.add(t);
            }
        }
        // sort matches to replace long matches first in case of overlapping matches
        longestMatches.sort((s1, s2) -> Integer.compare(s2.length(), s1.length()));

        for (String match : longestMatches)
        {
            content = content.replaceAll(match, tokenReplacer);
        }
        return content;
    }

    public List<String> replaceTokenTest(List<String> content, Set<String> tokens)
    {
        content.removeAll(tokens);
        return content;
    }


    void loadClubs()
    {
        //TODO Tabea local
//        String filename = "C:\\Users\\Tabea\\Documents\\Studium\\Uni Köln\\Data Science\\DFL Projekt\\Vereine.txt";
//        String filenameWrite = "C:\\Users\\Tabea\\Documents\\Studium\\Uni Köln\\Data Science\\DFL Projekt\\Vereine_names.txt";

        //TODO Thomas local
        String filename = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\filter\\Vereine.txt";
        String filenameWrite = "H:\\Daten\\Uni\\Master\\2.Semester\\DSBS\\03_Industry Project\\002_Workspace\\Vereine_names.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filenameWrite));
             BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            Pattern MY_PATTERN = Pattern.compile("n=\"(.*)\"");
            while ((line = br.readLine()) != null)
            {
                Matcher m = MY_PATTERN.matcher(line);
                String s;
                while (m.find())
                {
                    s = m.group(1);
                    System.out.println(s);
                    bw.write(s);
                    bw.newLine();
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}