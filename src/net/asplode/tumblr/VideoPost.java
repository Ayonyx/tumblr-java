package net.asplode.tumblr;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class VideoPost extends Post {
    public VideoPost() {
        try {
            entity.addPart("type", new StringBody("video"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setCaption(String caption) throws UnsupportedEncodingException {
        entity.addPart("caption", new StringBody(caption));
    }
        
    public void setSourceFile(File video) throws UnsupportedEncodingException {
        entity.addPart("data", new FileBody(video));
    }

    public void setEmbedText(String embed) throws UnsupportedEncodingException {
        entity.addPart("embed", new StringBody(embed));
    }

}
