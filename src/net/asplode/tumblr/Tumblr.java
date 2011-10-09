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
import java.util.ArrayList;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.HmacSha1MessageSigner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author nsheridan
 * 
 */
public class Tumblr {
    private final String BASE_URL = "http://api.tumblr.com/v2";

    private String email;
    private String password;
    private String blog;
    private ArrayList<BasicNameValuePair> params;
    private String oauth_key;
    private OAuthConsumer consumer;
    HttpClient client;

    /**
     * @param oauth_key
     *            OAuth key.
     * @param oauth_secret
     *            OAuth secret key.
     */
    public Tumblr(String oauth_key, String oauth_secret) {
        this.params = new ArrayList<BasicNameValuePair>();
        this.oauth_key = oauth_key;
        consumer = new CommonsHttpOAuthConsumer(oauth_key, oauth_secret);
        consumer.setMessageSigner(new HmacSha1MessageSigner());
        client = new DefaultHttpClient();
    }

    private static String convertToString(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    private String[] getOAuthTokens() throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IOException {
        ArrayList<BasicNameValuePair> xauth_params = new ArrayList<BasicNameValuePair>();
        xauth_params.add(new BasicNameValuePair("x_auth_mode", "client_auth"));
        xauth_params.add(new BasicNameValuePair("x_auth_username", email));
        xauth_params.add(new BasicNameValuePair("x_auth_password", password));
        HttpPost post = new HttpPost("https://www.tumblr.com/oauth/access_token");
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(xauth_params);
            post.setEntity(entity);
            consumer.sign(post);
            HttpResponse response = client.execute(post);
            String s = convertToString(response.getEntity().getContent());
            String[] tokens = s.split("&");
            String[] result = new String[2];
            result[0] = tokens[0].split("=")[1];
            result[1] = tokens[1].split("=")[1];
            return result;
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    private JSONObject APIKeyGet(String url) throws ClientProtocolException, IOException,
            IllegalStateException, JSONException {
        HttpGet req = new HttpGet(url);
        HttpResponse response = client.execute(req);
        JSONObject result = new JSONObject(convertToString(response.getEntity().getContent()));
        return result;
    }

    private JSONObject OAuthGet(String url) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IOException, IllegalStateException, JSONException {
        String[] oauth_tokens = getOAuthTokens();
        consumer.setTokenWithSecret(oauth_tokens[0], oauth_tokens[1]);
        HttpGet req = new HttpGet(url);
        consumer.sign(req);
        HttpResponse response = client.execute(req);
        JSONObject result = new JSONObject(convertToString(response.getEntity().getContent()));
        return result;
    }

    private JSONObject OAuthPost(String url) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IOException, IllegalStateException, JSONException {
        String[] oauth_tokens = getOAuthTokens();
        consumer.setTokenWithSecret(oauth_tokens[0], oauth_tokens[1]);
        HttpPost req = new HttpPost(url);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        req.setEntity(entity);
        consumer.sign(req);
        HttpResponse response = client.execute(req);
        JSONObject result = new JSONObject(convertToString(response.getEntity().getContent()));
        params.clear();
        return result;
    }

    private JSONObject Get(String url) throws ClientProtocolException, IOException,
            IllegalStateException, JSONException {
        HttpGet req = new HttpGet(url);
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false);
        req.setParams(params);
        HttpResponse response = client.execute(req);
        JSONObject result = new JSONObject(convertToString(response.getEntity().getContent()));
        return result;
    }

    private JSONObject Post(String url) throws ClientProtocolException, IOException,
            IllegalStateException, JSONException {
        HttpPost req = new HttpPost(url);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        req.setEntity(entity);
        HttpResponse response = client.execute(req);
        JSONObject result = new JSONObject(convertToString(response.getEntity().getContent()));
        params.clear();
        return result;
    }

    /**
     * @param email
     *            Email address
     * @param password
     *            Password
     */
    public void setCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * @param blog
     *            Blog url e.g. "myblog.tumblr.com", "tumblr.mydomain.com"
     */
    public void setBlog(String blog) {
        this.blog = blog;
    }

    /**
     * @return Blog info
     * @throws NoBlogException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws IllegalStateException
     * @throws JSONException
     */
    public JSONObject getBlogInfo() throws NoBlogException, ClientProtocolException, IOException,
            IllegalStateException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/info" + "?api_key=" + oauth_key;
        JSONObject result = APIKeyGet(url);
        return result;
    }

    /**
     * @return Blog avatar, 64x64
     * @throws NoBlogException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getAvatar() throws NoBlogException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/avatar";
        JSONObject result = Get(url);
        return result;
    }

    /**
     * @param size
     *            Size of avatar. 16, 24, 30, 40, 48, 64, 96, 128, 512.
     * @return Avatar of requested size.
     * @throws NoBlogException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getAvatar(int size) throws NoBlogException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/avatar/" + size;
        JSONObject result = Get(url);
        return result;
    }

    /**
     * @return Followers
     * @throws NoBlogException
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getFollowers() throws NoBlogException, OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/followers";
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @param notes
     *            Include note count and note metadata
     * @return Posts
     * @throws NoBlogException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getPosts(boolean notes) throws NoBlogException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/posts?api_key=" + oauth_key;
        if (notes) {
            url += "&notes_info";
        }
        JSONObject result = APIKeyGet(url);
        return result;
    }

    /**
     * @param notes
     *            Include note count and note metadata
     * @param offset
     *            Post number to start at
     * @return Posts
     * @throws NoBlogException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getPosts(boolean notes, int offset) throws NoBlogException,
            ClientProtocolException, IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/posts?api_key=" + oauth_key + "&offset="
                + offset;
        if (notes) {
            url += "&notes_info";
        }
        JSONObject result = APIKeyGet(url);
        return result;
    }

    /**
     * @param postid
     *            Post ID
     * @param notes
     *            Include note count and note metadata
     * @return Specific post
     * @throws NoBlogException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getPost(String postid, boolean notes) throws NoBlogException,
            ClientProtocolException, IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/posts?api_key=" + oauth_key + "&id=" + postid;
        if (notes) {
            url += "&notes_info";
        }
        JSONObject result = APIKeyGet(url);
        return result;
    }

    /**
     * @return Posts in the queue
     * @throws NoBlogException
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getQueue() throws NoBlogException, OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/posts/queue";
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @return Draft posts
     * @throws NoBlogException
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getDrafts() throws NoBlogException, OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        String url = BASE_URL + "/blog/" + blog + "/posts/draft";
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @param id
     *            Post ID to reblog
     * @param key
     *            Reblog key of the post to be reblogged
     * @param comment
     *            Comment to include with the reblog
     * @return Status of the request
     * @throws NoBlogException
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject reblogPost(String id, String key, String comment) throws NoBlogException,
            OAuthMessageSignerException, OAuthExpectationFailedException,
            OAuthCommunicationException, ClientProtocolException, IllegalStateException,
            IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("reblog_key", key));
        params.add(new BasicNameValuePair("comment", comment));
        String url = BASE_URL + "/blog/" + blog + "/post/reblog";
        JSONObject result = OAuthPost(url);
        return result;
    }

    /**
     * @param id
     *            Post ID to delete.
     * @return Status of the request
     * @throws NoBlogException
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject deletePost(String id) throws NoBlogException, OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        if (blog == null) {
            throw new NoBlogException();
        }
        params.add(new BasicNameValuePair("id", id));
        String url = BASE_URL + "/blog/" + blog + "/post/delete";
        JSONObject result = OAuthPost(url);
        return result;
    }

    /**
     * @return User info
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getUserInfo() throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/info";
        JSONObject result = OAuthPost(url);
        return result;
    }

    /**
     * @param notes
     *            Include note count and note metadata
     * @return Dashboard
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getDashboard(boolean notes) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/dashboard";
        if (notes) {
            url += "?notes_info";
        }
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @param notes
     *            Include note count and note metadata
     * @param offset
     *            Post number to start at.
     * @return Dashboard
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getDashboard(boolean notes, int offset) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/dashboard?offset=" + offset;
        if (notes) {
            url += "&notes_info";
        }
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @return Liked posts
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getLikes() throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/likes";
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @param offset
     *            Post number to start at
     * @return Liked posts
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getLikes(int offset) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/likes?offset=" + offset;
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @return Blogs the user is following
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getFollowing() throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/following";
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @param offset
     *            Post number to start at
     * @return Blogs the user is following
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getFollowing(int offset) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/following?offset=" + offset;
        JSONObject result = OAuthGet(url);
        return result;
    }

    /**
     * @param blogURL
     *            URL of the blog to follow
     * @return Status of the request
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject follow(String blogURL) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/follow";
        params.add(new BasicNameValuePair("url", blogURL));
        JSONObject result = OAuthPost(url);
        return result;
    }

    /**
     * @param blogURL
     *            URL of the blog to unfollow
     * @return Status of the request
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject unfollow(String blogURL) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/unfollow";
        params.add(new BasicNameValuePair("url", blogURL));
        JSONObject result = OAuthPost(url);
        return result;
    }

    /**
     * @param id
     *            Post ID
     * @param reblog_key
     *            Post reblog key
     * @return Status of the request
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject likePost(String id, String reblog_key) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/like";
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("reblog_key", reblog_key));
        JSONObject result = OAuthPost(url);
        return result;
    }

    /**
     * @param id
     *            Post ID
     * @param reblog_key
     *            Post reblog key
     * @return Status of the request
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject unlikePost(String id, String reblog_key) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException,
            IllegalStateException, IOException, JSONException {
        String url = BASE_URL + "/user/unlike";
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("reblog_key", reblog_key));
        JSONObject result = OAuthPost(url);
        return result;
    }
}
