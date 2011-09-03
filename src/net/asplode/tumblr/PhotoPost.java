package net.asplode.tumblr;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class PhotoPost extends Post {

    public PhotoPost() {
        try {
            entity.addPart("type", new StringBody("photo"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setCaption(String caption) throws UnsupportedEncodingException {
        entity.addPart("caption", new StringBody(caption));
    }

    public void setClickThroughURL(String url) throws UnsupportedEncodingException {
        entity.addPart("click-through-url", new StringBody(url));
    }

    public void setSourceURL(String url) throws UnsupportedEncodingException {
        entity.addPart("source", new StringBody(url));
    }

    public void setSourceFile(File image) throws UnsupportedEncodingException {
        entity.addPart("data", new FileBody(image));
    }
}
