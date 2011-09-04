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

public class LinkPost extends Post {

    public LinkPost() {
        try {
            entity.addPart("type", new StringBody("link"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param url URL
     * @throws UnsupportedEncodingException
     */
    public void setURL(String url) throws UnsupportedEncodingException {
        entity.addPart("url", new StringBody(url));
    }

    /**
     * @param name Link name
     * @throws UnsupportedEncodingException
     */
    public void setName(String name) throws UnsupportedEncodingException {
        entity.addPart("name", new StringBody(name));
    }

    /**
     * @param description Link description
     * @throws UnsupportedEncodingException
     */
    public void setDescription(String description) throws UnsupportedEncodingException {
        entity.addPart("description", new StringBody(description));
    }
}
