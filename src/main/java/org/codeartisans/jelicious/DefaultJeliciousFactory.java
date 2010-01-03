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

import org.codeartisans.jelicious.domain.TagSet;
import org.codeartisans.jelicious.domain.BookmarkDay;
import org.codeartisans.jelicious.domain.Bookmark;
import org.codeartisans.jelicious.domain.Tag;
import org.joda.time.DateTime;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class DefaultJeliciousFactory
        implements JeliciousFactory
{

    public Tag createTag(Node node)
    {
        try {
            String tag = node.getAttributes().getNamedItem("tag").getNodeValue();
            Integer count = Integer.valueOf(node.getAttributes().getNamedItem("count").getNodeValue());
            return new Tag(tag, count);
        } catch (NumberFormatException ex) {
            throw new JeliciousException("Unable to parse the delicious response, something went plain wrong!", ex);
        }
    }

    public DateTime createLastBookmarkDateTime(Node node)
    {
        String deliciousDate = node.getAttributes().getNamedItem("time").getNodeValue();
        return new DateTime(deliciousDate);
    }

    public Bookmark createBookmark(Node node)
    {
        NamedNodeMap attrs = node.getAttributes();
        String url = attrs.getNamedItem("href").getNodeValue();
        String hash = attrs.getNamedItem("hash").getNodeValue();
        String description = attrs.getNamedItem("description").getNodeValue();
        DateTime time = new DateTime(attrs.getNamedItem("time").getNodeValue());
        String extended = attrs.getNamedItem("extended").getNodeValue();
        String meta = attrs.getNamedItem("meta").getNodeValue();
        Boolean shared = (attrs.getNamedItem("shared") == null);
        return new Bookmark(url, shared);
    }

    public TagSet createTagSet(Node node)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BookmarkDay createBookmarkDay(Node node)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
