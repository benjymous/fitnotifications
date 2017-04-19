package com.abhijitvalluri.android.fitnotifications.services;

import android.app.Notification;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Message Extractor that formats "Now playing" notifications into a sensible message
 */

public class NowPlayingMessageExtractor extends GenericMessageExtractor {

    // Indicate that the first line is the song name
    private final boolean songNameFirst;

    public NowPlayingMessageExtractor(boolean songNameFirst) {
        this.songNameFirst = songNameFirst;
    }

    private Map<String, String> mNotificationStringMap = new HashMap<>();

    @Override
    public CharSequence[] getTitleAndText(String appPackageName, Bundle extras, int notificationFlags) {
        CharSequence[] titleAndText = super.getTitleAndText(appPackageName, extras, notificationFlags);

        Set<String> keys = extras.keySet();

        String text = null;

        if (keys.contains(Notification.EXTRA_MEDIA_SESSION)) {
            // Only handle media notifiations, to ignore any other spam
            if (songNameFirst) {
                text = titleAndText[0].toString() + " - " + titleAndText[1].toString();
            } else {
                text = titleAndText[1].toString() + " - " + titleAndText[0].toString();
            }
        }
        String prevNotificationText = mNotificationStringMap.put(appPackageName, text);
        // TODO: add more specific checks to avoid blocking legitimate identical notifications
        if (text.equals(prevNotificationText)) {
            // do not send the duplicate notification, but only for every 2nd occurrence
            // (i.e. when the same text arrives for the 3rd time - send it)
            mNotificationStringMap.remove(appPackageName);
            text = null;
        }

        return new CharSequence[] { "Playing", text };
    }

}
