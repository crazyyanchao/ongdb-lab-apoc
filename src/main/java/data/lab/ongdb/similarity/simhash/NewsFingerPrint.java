package data.lab.ongdb.similarity.simhash;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity.simhash.NewsFingerPrint
 * @Description: TODO(新闻指纹)
 * @date 2020/5/22 15:12
 */
public class NewsFingerPrint {

    // TITLE SIM-HASH-VALUE
    private String titleSimHash;

    // CONTENT SIM-HASH-VALUE
    private String contentSimHash;

    public NewsFingerPrint(String titleSimHash, String contentSimHash) {
        this.titleSimHash = titleSimHash;
        this.contentSimHash = contentSimHash;
    }

    public NewsFingerPrint(String titleOrContentSimHash) {
        this.contentSimHash = titleOrContentSimHash;
    }

    public String getTitleSimHash() {
        return titleSimHash;
    }

    public void setTitleSimHash(String titleSimHash) {
        this.titleSimHash = titleSimHash;
    }

    public String getContentSimHash() {
        return contentSimHash;
    }

    public void setContentSimHash(String contentSimHash) {
        this.contentSimHash = contentSimHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewsFingerPrint that = (NewsFingerPrint) o;
        return Objects.equals(titleSimHash, that.titleSimHash) &&
                Objects.equals(contentSimHash, that.contentSimHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleSimHash, contentSimHash);
    }

    @Override
    public String toString() {
        return "NewsFingerPrint{" +
                "titleSimHash='" + titleSimHash + '\'' +
                ", contentSimHash='" + contentSimHash + '\'' +
                '}';
    }
}
