package ch.watched.android.models;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * MovieDB configuration
 */
public class MovieDBConfiguration implements Serializable {

    private static final long serialVersionUID = -2183465196807646L;

    private ImageConf images;
    private List<String> change_keys;

    public String getUrl() {
        return images.getBaseUrl();
    }

    public String getPosterSize() {
        return images.getPosterSize();
    }

    public String getBackdropSize() {
        return images.backdrop_sizes.get(1);
    }

    public String getStillSize() {
        return images.still_sizes.get(images.still_sizes.size() - 2);
    }

    private class ImageConf implements Serializable {

        private static final long serialVersionUID = -2183465135396807646L;

        private String base_url;
        private String secured_base_url;
        private List<String> poster_sizes;
        private List<String> backdrop_sizes;
        private List<String> still_sizes;

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
