package Settings;

import java.util.ArrayList;

public class Settings {

    public static String _lang = "en-US";
    public static Boolean _miniMode = false;
    public static Boolean _topmostMode = false;
    public static Boolean _messageMode = true;
    public static ArrayList<String[]> _filters = new ArrayList<>();
    public static ArrayList<String[]> _scripts = new ArrayList<>();

    public static String lang = "en-US";
    public static Boolean miniMode = false;
    public static Boolean topmostMode = false;
    public static Boolean messageMode = true;
    public static ArrayList<String[]> filters = new ArrayList<>();
    public static ArrayList<String[]> scripts = new ArrayList<>();

    public static int _getLang() {
        switch (_lang) {
            case "en-US":
                return 0;
            case "ro-RO":
                return 1;
            case "de-DE":
                return 2;
            case "ja-JP":
                return 3;
            default:
                return -1;
        }
    }

    public static void _setLang(int index) {
        switch (index) {
            case 0:
                _lang = "en-US";
                break;
            case 1:
                _lang = "ro-RO";
                break;
            case 2:
                _lang = "de-DE";
                break;
            case 3:
                _lang = "ja-JP";
                break;
            default:
                _lang = "en-US";
                break;
        }
    }

    public static void Save() {
        miniMode = _miniMode;
        topmostMode = _topmostMode;
        lang = _lang;
        messageMode = _messageMode;
        filters = _filters;
        scripts = _scripts;
    }

    public static void _reset() {
        _miniMode = miniMode;
        _topmostMode = topmostMode;
        _lang = lang;
        _messageMode = messageMode;
        _filters = filters;
        _scripts = scripts;
    }

    public static void Reset() {
        _miniMode = miniMode = false;
        _topmostMode = topmostMode = false;
        _lang = lang = "en-US";
        _messageMode = messageMode = true;
    }

    public static void ResetData() {
        _filters = filters = new ArrayList<>();
        _scripts = scripts = new ArrayList<>();
    }

}
