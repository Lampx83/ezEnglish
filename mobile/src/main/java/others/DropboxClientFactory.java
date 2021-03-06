package others;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttpRequestor;
import com.dropbox.core.v2.DbxClientV2;

import java.util.Locale;

/**
 * Singleton instance of {@link DbxClientV2} and friends
 */
public class DropboxClientFactory {

    private static DbxClientV2 sDbxClient;

    public static void init(String accessToken) {
        if (sDbxClient == null) {
            String userLocale = Locale.getDefault().toString();
//
//            DbxRequestConfig requestConfig = new DbxRequestConfig(
//                    "examples-v2-demo",
//                    userLocale,
//                    OkHttpRequestor.INSTANCE);
            DbxRequestConfig.Builder b = DbxRequestConfig.newBuilder("TuneLabPianoTuner/2.3");
            DbxRequestConfig requestConfig = b.build();

            sDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
    }

    public static DbxClientV2 getClient() {
        return sDbxClient;
    }
}
