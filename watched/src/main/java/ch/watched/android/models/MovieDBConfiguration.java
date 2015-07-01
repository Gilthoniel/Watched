package ch.watched.android.models;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * MovieDB configuration
 */
public class MovieDBConfiguration {

    private ImageConf images;
    private List<String> change_keys;

    public String getUrl() {
        return images.getBaseUrl();
    }

    public String getPosterSize() {
        return images.getPosterSize();
    }

    private class ImageConf {

        private String base_url;
        private String secured_base_url;
        private List<String> poster_sizes;

        public String getBaseUrl() {
            return base_url;
        }

        public String getPosterSize() {
            for (String size : poster_sizes) {
                if (size.matches("w[0-9]{3,}")) {
                    if (Character.getNumericValue(size.charAt(1)) >= 2) {
                        return size;
                    }
                }
            }

            return "original";
        }
    }
}
