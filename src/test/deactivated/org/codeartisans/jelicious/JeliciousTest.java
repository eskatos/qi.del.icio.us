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

import org.codeartisans.jelicious.domain.Bookmark;
import org.codeartisans.jelicious.domain.Tag;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Theses are not really tests as they don't assert anything.
 * We will need a mock delicious service and fixtures to write assertions.
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
public class JeliciousTest
{

    private static Jelicious jelicious;

    @BeforeClass
    public static void setUp()
    {
        JeliciousClient client = new HttpClient4JeliciousClient("username", "password");
        JeliciousFactory factory = new DefaultJeliciousFactory();
        jelicious = new DefaultJelicious(client, factory);
    }

    @Ignore
    @Test
    public void fetchAllTags()
    {
        Iterable<Tag> allTags = jelicious.fetchAllTags();
        for (Tag eachTag : allTags) {
            System.out.println("Tag " + eachTag.tag() + " has been used " + eachTag.count() + " times.");
        }
    }

    @Ignore
    @Test
    public void fetchLastBookmarkDateTime()
    {
        System.out.println(jelicious.fetchLastBookmarkDateTime());
    }

    @Ignore
    @Test
    public void ensureAllPostsArePrivate()
    {
        Iterable<Bookmark> bookmarks = jelicious.fetchAllBookmarks();
        for (Bookmark eachBookmark : bookmarks) {
            if (eachBookmark.shared()) {
                System.out.println("This url is public: " + eachBookmark.url());
                eachBookmark.share();
                jelicious.updateBookmark(eachBookmark);
            }
        }
    }

}
