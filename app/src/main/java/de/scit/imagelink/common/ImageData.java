package de.scit.imagelink.common;

public class ImageData {

    private String key;
    private String base64;
    private String originalBase64;
    private String contentType;

    public ImageData(String key, String base64, String contentType) {
        this.key = key;
        this.base64 = base64;
        this.originalBase64 = null;
        this.contentType = contentType;
    }

    public String getKey() {
        return key;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isOriginal() {
        return originalBase64 != null;
    }

    public void setOriginalBase64(String base64) {
        this.originalBase64 = base64;
    }

    public String getOriginalBase64() {
        return originalBase64;
    }
}
