package net.asplode.tumblr;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class AudioPost extends Post {
    public AudioPost() {
        try {
            entity.addPart("type", new StringBody("audio"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setCaption(String caption) throws UnsupportedEncodingException {
        entity.addPart("caption", new StringBody(caption));
    }
        
    public void setSourceFile(File audio) throws UnsupportedEncodingException {
        entity.addPart("data", new FileBody(audio));
    }

}
