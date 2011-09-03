package net.asplode.tumblr;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.StringBody;

public class QuotePost extends Post {

    public QuotePost() {
        try {
            entity.addPart("type", new StringBody("quote"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setQuote(String quote) throws UnsupportedEncodingException {
        entity.addPart("quote", new StringBody(quote));
    }
    public void setSource(String source) throws UnsupportedEncodingException {
        entity.addPart("source", new StringBody(source));
    }
}
