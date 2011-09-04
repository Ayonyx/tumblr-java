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
