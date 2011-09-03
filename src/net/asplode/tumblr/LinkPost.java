package net.asplode.tumblr;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.StringBody;

public class LinkPost extends Post {

    public LinkPost() {
        try {
            entity.addPart("type", new StringBody("link"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setURL(String url) throws UnsupportedEncodingException {
        entity.addPart("url", new StringBody(url));
    }

    public void setName(String name) throws UnsupportedEncodingException {
        entity.addPart("name", new StringBody(name));
    }

    public void setDescription(String description) throws UnsupportedEncodingException {
        entity.addPart("description", new StringBody(description));
    }
}
