package Config;

import Resources.ResourceProvider;

public class CSVProcessConfig extends ProcessConfigBase
{
    //region Fields

    private String csvImportDirectory;
    private String csvExportDirectory;
    private boolean addCurrentDateTimeToExportFileName;
    private String exportFileName;
    private String contentTypeSuffix;
    private boolean showPerLineDebugInfo;

    //endregion

    public CSVProcessConfig()
    {
        try
        {
            initializeInternal(ResourceProvider.getCSVConfigPath());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected boolean processLineContent(String line)
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
                case "csvImportDirectory":
                    csvImportDirectory = rightHandSide;
                    break;
                case "csvExportDirectory":
                    csvExportDirectory = rightHandSide;
                    break;
                case "addCurrentDateTimeToExportFileName":
                    addCurrentDateTimeToExportFileName = Boolean.parseBoolean(rightHandSide);
                    break;
                case "exportFileName":
                    exportFileName = rightHandSide;
                    break;
                case "contentTypeSuffix":
                    contentTypeSuffix = rightHandSide;
                    break;
                case "showPerLineDebugInfo":
                    showPerLineDebugInfo = Boolean.parseBoolean(rightHandSide);
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

    //region Getter / Setter

    public String getCsvImportDirectory()
    {
        return csvImportDirectory;
    }

    public String getCsvExportDirectory()
    {
        return csvExportDirectory;
    }

    public boolean getAddCurrentDateTimeToExportFileName()
    {
        return addCurrentDateTimeToExportFileName;
    }

    public String getExportFileName()
    {
        return exportFileName;
    }

    public String getContentTypeSuffix()
    {
        return contentTypeSuffix;
    }

    public boolean getShowPerLineDebugInfo()
    {
        return showPerLineDebugInfo;
    }

    //endregion
}