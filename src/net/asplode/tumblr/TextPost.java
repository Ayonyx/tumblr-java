package net.asplode.tumblr;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.StringBody;

public class TextPost extends Post {

    public TextPost() {
        try {
            entity.addPart("type", new StringBody("regular"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setTitle(String title) throws UnsupportedEncodingException {
        entity.addPart("title", new StringBody(title));
    }

    public void setBody(String body) throws UnsupportedEncodingException {
        entity.addPart("body", new StringBody(body));
    }
}
