/*
 * Copyright (c) 2009 Paul Merlin <paul@nosphere.org>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.codeartisans.jelicious;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codeartisans.jelicious.domain.Bookmark;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class HttpClient4JeliciousClient
        implements JeliciousClient
{

    private final DefaultHttpClient httpClient;
    private final DocumentBuilder documentBuilder;

    public HttpClient4JeliciousClient(String username, String password)
    {
        // HTTP Client
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.useragent", "jelicious");
        httpClient.getCredentialsProvider().setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // XML Document Builder
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setNamespaceAware(false);
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new JeliciousException("Unable to instanciante the DefaultJecliciousClient, that must be bad.", e);
        }
    }

    public Iterable<Node> fetchAllTags()
    {
        try {
            HttpGet httpget = new HttpGet(API_V1 + "tags/get?");
            Document document = fetchPayload(httpget);
            List<Node> nodes = new LinkedList<Node>();
            NodeList tagItems = document.getElementsByTagName("tag");
            for (int idx = 0; idx < tagItems.getLength(); idx++) {
                Node eachNode = tagItems.item(idx);
                nodes.add(eachNode);
            }
            return nodes;
        } catch (NullPointerException ex) {
            throw new JeliciousException("Something was wrong in the delicious response", ex);
        }
    }

    public Node fetchLastBookmarkDateTime()
    {
        try {
            HttpGet httpget = new HttpGet(API_V1 + "posts/update");
            Document document = fetchPayload(httpget);
            return document.getChildNodes().item(0);
        } catch (NullPointerException ex) {
            throw new JeliciousException("Something was wrong in the delicious response", ex);
        }
    }

    public Iterable<Node> fetchAllBookmarks()
    {
        try {
            HttpGet httpget = new HttpGet(API_V1 + "posts/all?");
            Document document = fetchPayload(httpget);
            List<Node> nodes = new LinkedList<Node>();
            NodeList bookmarkItems = document.getElementsByTagName("post");
            for (int idx = 0; idx < bookmarkItems.getLength(); idx++) {
                Node eachNode = bookmarkItems.item(idx);
                nodes.add(eachNode);
            }
            return nodes;
        } catch (NullPointerException ex) {
            throw new JeliciousException("Something was wrong in the delicious response", ex);
        }
    }

    public void updateBookmark(Bookmark eachBookmark)
    {
        if (false) { // FIXME
            try {
                HttpGet httpget = new HttpGet(API_V1 + "posts/add?"); // TODO
                Document document = fetchPayload(httpget);
                Node resultNode = document.getElementsByTagName("result").item(0);
                String result = resultNode.getAttributes().getNamedItem("code").getNodeValue();
                if (!"done".equals(result)) {
                    throw new JeliciousException("Unable to update bookmark, delicious says: " + result);
                }
            } catch (NullPointerException ex) {
                throw new JeliciousException("Something was wrong in the delicious response", ex);
            }
        }
    }

    public Element createNewBookmark(URL url, String title, String description, Iterable<String> tags, DateTime date, Boolean shared)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteBookmark(URL url)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmarksInLastBookmarkDay()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmark(URL url)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmarkSet(Iterable<String> hashes)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmarksByTags(Iterable<String> tags)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmarksByDay(DateTime dateInDay)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmarksByTagsAndDay(Iterable<String> tags, DateTime dateInDay)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchRecentBookmarks(Integer maxResults)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchRecentBookmarksByTags(Iterable<String> tags, Integer maxResults)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchAllBookmarkDays()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element fetchBookmarkDaysByTags(Iterable<String> tags)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Document fetchPayload(HttpGet httpget)
    {
        try {
            HttpResponse response = httpClient.execute(httpget);
            ensureNoHttpError(response);
            String payload = consumePayload(response.getEntity());
            System.out.println("Payload: " + payload);
            return documentBuilder.parse(new InputSource(new StringReader(payload)));
        } catch (IOException ex) {
            throw new JeliciousException("Something went wrong getting the delicious response", ex);
        } catch (SAXException ex) {
            throw new JeliciousException("Something went wrong reading the delicious response", ex);
        } catch (NullPointerException ex) {
            throw new JeliciousException("Something went wrong reading the delicious response", ex);
        }
    }

    /**
     * Quite rude for now, only accept 200 status.
     * It seems that's the delicious way ...
     */
    private void ensureNoHttpError(HttpResponse response)
    {
        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            String protocol = response.getStatusLine().getProtocolVersion().toString();
            String reasonPhrase = response.getStatusLine().getReasonPhrase();
            throw new JeliciousException(protocol + " " + status + " " + reasonPhrase);
        }
    }

    private String consumePayload(HttpEntity entity)
            throws IOException
    {
        if (entity == null) {
            throw new JeliciousException("Something went wrong getting the delicious response: entity was null");
        }
        final String payload = EntityUtils.toString(entity, UTF_8);
        entity.consumeContent();
        return payload;
    }

}
