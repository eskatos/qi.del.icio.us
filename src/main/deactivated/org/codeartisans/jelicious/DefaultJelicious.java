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

import java.util.LinkedList;
import java.util.List;
import org.codeartisans.jelicious.domain.Bookmark;
import org.codeartisans.jelicious.domain.Tag;
import org.joda.time.DateTime;
import org.w3c.dom.Node;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class DefaultJelicious
        implements Jelicious
{

    private final JeliciousClient client;
    private final JeliciousFactory factory;

    public DefaultJelicious(JeliciousClient client, JeliciousFactory factory)
    {
        this.client = client;
        this.factory = factory;
    }

    public Iterable<Tag> fetchAllTags()
    {
        List<Tag> tags = new LinkedList<Tag>();
        for (Node tagNode : client.fetchAllTags()) {
            tags.add(factory.createTag(tagNode));
        }
        return tags;
    }

    public DateTime fetchLastBookmarkDateTime()
    {
        return factory.createLastBookmarkDateTime(client.fetchLastBookmarkDateTime());
    }

    public Iterable<Bookmark> fetchAllBookmarks()
    {
        List<Bookmark> bookmarks = new LinkedList<Bookmark>();
        for (Node bookmarkNode : client.fetchAllBookmarks()) {
            bookmarks.add(factory.createBookmark(bookmarkNode));
        }
        return bookmarks;
    }

    public void updateBookmark(Bookmark eachBookmark)
    {
        client.updateBookmark(eachBookmark);
    }

}
