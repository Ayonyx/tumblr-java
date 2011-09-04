/* Copyright (c) 2011 Niall Sheridan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * @param title Conversation title
     * @throws UnsupportedEncodingException
     */
    public void setTitle(String title) throws UnsupportedEncodingException {
        entity.addPart("title", new StringBody(title));
    }

    /**
     * @param conversation Conversation
     * @throws UnsupportedEncodingException
     */
    public void setConversation(String conversation) throws UnsupportedEncodingException {
        entity.addPart("conversation", new StringBody(conversation));
    }
}
