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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;


abstract class Post {

    enum State {
        PUBLISH("published"), DRAFT("draft"), QUEUE("queue");
        private String state;

        private State(String state) {
            this.state = state;
        }

        private String getState() {
            return state;
        }
    }

    private final String POST_URL = "http://www.tumblr.com/api/write";
    MultipartEntity entity;
    private String email;
    private String password;

    public Post() {
        entity = new MultipartEntity();
    }

    /**
     * @return HTTP status code
     * @throws NoCredentialsException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public int postToTumblr() throws NoCredentialsException, ClientProtocolException, IOException {
        if (email == null || password == null) {
            throw new NoCredentialsException();
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost request = new HttpPost(POST_URL);
        request.setEntity(entity);
        HttpResponse response = httpclient.execute(request);
        System.out.println(convertToString(response.getEntity().getContent()));
        return response.getStatusLine().getStatusCode();
    }

    /**
     * @param postId
     *            The id of the post to be modified.
     * @return HTTP status code
     * @throws NoCredentialsException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public int postToTumblr(String postId) throws NoCredentialsException, ClientProtocolException,
            IOException {
        if (email == null || password == null) {
            throw new NoCredentialsException();
        }
        entity.addPart("post-id", new StringBody(postId));
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost request = new HttpPost(POST_URL);
        request.setEntity(entity);
        HttpResponse response = httpclient.execute(request);
        return response.getStatusLine().getStatusCode();
    }

    /**
     * @param blog
     *            The blog to post to.
     * @throws UnsupportedEncodingException
     */
    public void setBlog(String blog) throws UnsupportedEncodingException {
        if (!blog.endsWith(".tumblr.com")) {
            blog += ".tumblr.com";
        }
        entity.addPart("group", new StringBody(blog));
    }

    /**
     * @param email
     *            Email address
     * @param password
     *            Password
     * @throws UnsupportedEncodingException
     */
    public void setCredentials(String email, String password) throws UnsupportedEncodingException {
        this.email = email;
        this.password = password;
        entity.addPart("email", new StringBody(email));
        entity.addPart("password", new StringBody(password));
    }

    /**
     * @param date
     *            Publish date and time for queued posts
     * @throws UnsupportedEncodingException
     */
    public void setPublishOn(String date) throws UnsupportedEncodingException {
        entity.addPart("publish-on", new StringBody(date));
    }

    /**
     * @param slug
     *            Custom string to appear in the post's URL
     * @throws UnsupportedEncodingException
     */
    public void setSlug(String slug) throws UnsupportedEncodingException {
        entity.addPart("slug", new StringBody(slug));
    }

    /**
     * @param state
     *            Post state.
     * @see State
     * @throws UnsupportedEncodingException
     */
    public void setState(State state) throws UnsupportedEncodingException {
        entity.addPart("state", new StringBody(state.getState()));
    }

    /**
     * @param tags
     *            Comma-separated list of post tags
     * @throws UnsupportedEncodingException
     */
    public void setTags(String tags) throws UnsupportedEncodingException {
        entity.addPart("tags", new StringBody(tags));
    }

    /**
     * @param twitter
     *            One of the following values: <br>
     *            "no" - Do not send post to twitter.<br>
     *            "auto" - Send to Twitter with an automatically generated
     *            summary of the post.<br>
     *            Any other value - A custom message to send to Twitter for this
     *            post.
     * @throws UnsupportedEncodingException
     */
    public void setTwitter(String twitter) throws UnsupportedEncodingException {
        entity.addPart("send-to-twitter", new StringBody(twitter));
    }

    private String convertToString(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

}
