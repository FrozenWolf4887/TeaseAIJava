package me.goddragon.teaseai.api.scripts.nashorn;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.api.media.MediaURL;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ShowImageFunction extends CustomFunctionExtended {
    public ShowImageFunction() {
        super("showImage", "showPicture", "displayImage", "displayPicture");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    protected File onCall(String pathOrUrl) {
        return onCall(pathOrUrl, 0);
    }

    protected File onCall(String pathOrUrl, Integer durationSeconds) {
        File file = null;

        if (isHttpUrl(pathOrUrl)) {
            file = tryGetImageFromUrl(pathOrUrl);
        } else {
            file = tryGetImageFromFilepath(pathOrUrl);
        }

        if (file == null) {
            TeaseLogger.getLogger().log(Level.SEVERE,
                    String.format("Matching image file for path '%s' does not exist", pathOrUrl));
        } else {
            MediaHandler.getHandler().showPicture(file, durationSeconds);
        }

        return file;
    }

    protected File onCall(File file) {
        return onCall(file, 0);
    }

    protected File onCall(File file, Integer durationSeconds) {
        MediaHandler.getHandler().showPicture(file, durationSeconds);
        return file;
    }

    protected File onCall(MediaURL mediaUrl) {
        return onCall(mediaUrl, 0);
    }

    protected File onCall(MediaURL mediaUrl, Integer durationSeconds) {
        final File file = mediaUrl.getRandomMedia();
        MediaHandler.getHandler().showPicture(file, durationSeconds);
        return file;
    }

    private boolean isHttpUrl(String path) {
        final String lowerCasePath = path.toLowerCase();
        return lowerCasePath.startsWith("http://") || lowerCasePath.startsWith("https://");
    }

    private File tryGetImageFromUrl(String url) {
        try {
            return MediaHandler.getHandler().getImageFromURL(url);
        } catch (IOException e) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, String.format("Failed to fetch from url '%s'", url));
        }

        return null;
    }

    private File tryGetImageFromFilepath(String path) {
        return FileUtils.getRandomMatchingFile(path);
    }
}
