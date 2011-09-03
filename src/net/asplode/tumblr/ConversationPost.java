package net.asplode.tumblr;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.StringBody;

public class ConversationPost extends Post {

    public ConversationPost() {
        try {
            entity.addPart("type", new StringBody("conversation"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setTitle(String title) throws UnsupportedEncodingException {
        entity.addPart("title", new StringBody(title));
    }

    public void setConversation(String conversation) throws UnsupportedEncodingException {
        entity.addPart("conversation", new StringBody(conversation));
    }
}
