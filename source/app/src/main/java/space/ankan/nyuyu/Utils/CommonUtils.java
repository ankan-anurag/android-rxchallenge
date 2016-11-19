package space.ankan.nyuyu.Utils;

import android.net.Uri;

/**
 * Created by Ankan.
 * Common functions used in the app
 */

public class CommonUtils {

    public static Long extractFilmId(String filmUrl) {
        Uri uri = Uri.parse(filmUrl);
        return Long.valueOf(uri.getLastPathSegment());
    }
}
