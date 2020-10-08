package me.davidml16.arewards.utils.TimeAPI;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.handlers.LanguageHandler;

public class TimeUtils {

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    public final static LanguageHandler languageHandler = Main.get().getLanguageHandler();

    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" ").append(temp > 1 ? languageHandler.getMessage("Times.Days") : languageHandler.getMessage("Times.Day"))
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" ").append(temp > 1 ? languageHandler.getMessage("Times.Hours") : languageHandler.getMessage("Times.Hour"))
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" ").append(temp > 1 ? languageHandler.getMessage("Times.Minutes") : languageHandler.getMessage("Times.Minute"))
                        .append(duration >= ONE_SECOND ? ", " : "");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" ").append(temp > 1 ? languageHandler.getMessage("Times.Seconds") : languageHandler.getMessage("Times.Second"));
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }
}