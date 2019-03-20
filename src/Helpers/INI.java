package Helpers;

import java.io.File;

public class INI {
    public static INIHelper settingsFile = new INIHelper("settings.ini");
    public static INIHelper itemsFile = new INIHelper("items.ini");
    public static INIHelper filtersFile = new INIHelper("filters.ini");
    public static INIHelper scriptsFile = new INIHelper("scripts.ini");

    public static boolean SettingsFileExist()
    {
        return new File("settings.ini").exists();
    }

    public static boolean ItemsFileExist()
    {
        return new File("items.ini").exists();
    }

    public static boolean FiltersFileExist()
    {
        return new File("filters.ini").exists();
    }

    public static boolean ScriptsFileExist()
    {
        return new File("scripts.ini").exists();
    }
}
