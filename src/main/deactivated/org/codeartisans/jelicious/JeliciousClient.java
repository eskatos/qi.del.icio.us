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

import java.net.URL;
import org.codeartisans.jelicious.domain.Bookmark;
import org.joda.time.DateTime;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parsing agnostic delicious.com client.
 *
 * Implementations must handle exceptions and shall use and abuse the JeliciousException.
 * The delicious api only use the GET verb, so this low level api is very straight forward.
 * See http://delicious.com/help/api
 *
 * This api do not support NotModified nor any etags, caching is an application responsibility.
 *
 * QUID How to ensure client implementations set the meta parameter to yes in all requests to posts/get ?
 *
 * To see what xml payload each method returns, use curl:
 *
 *  curl https://username:password@api.del.icio.us/v1/${QUERY}
 *
 * @author Paul Merlin <paul@nosphere.org>
 */
public interface JeliciousClient
{

    static final String API_V1 = "https://api.del.icio.us/v1/";
    static final String API_V2 = "http://api.del.icio.us/v2/";
    static final String UTF_8 = "UTF-8";

    // posts/all?
    Iterable<Node> fetchAllBookmarks();

    // posts/add?
    void updateBookmark(Bookmark eachBookmark);

    // tags/get?
    Iterable<Node> fetchAllTags();

    // posts/update?
    Node fetchLastBookmarkDateTime();

    // posts/add?url=url&description=title&extended=description&tags=join(tags, " ")&dt=ISO8601&replace=no&shared=no
    Element createNewBookmark(URL url, // required
                              String title, // required
                              String description, // optional
                              Iterable<String> tags, // optional
                              DateTime date, // optional
                              Boolean shared // optional, WARN defaults to yes
            );

    // posts/delete?url=url
    void deleteBookmark(URL url);

    // posts/get
    Element fetchBookmarksInLastBookmarkDay();

    // posts/get?url=url
    Element fetchBookmark(URL url);

    // posts/get?hashes={MD5}+{MD5}+...+{MD5}
    Element fetchBookmarkSet(Iterable<String> hashes);

    // posts/all?tag=join(tags, " ")
    Element fetchBookmarksByTags(Iterable<String> tags);

    // posts/get?dt=ISO8601
    Element fetchBookmarksByDay(DateTime dateInDay);

    // posts/get?tag=foo bar&dt=ISO8601
    Element fetchBookmarksByTagsAndDay(Iterable<String> tags, DateTime dateInDay);

    // posts/recent?count=maxResults
    Element fetchRecentBookmarks(Integer maxResults); // Range 1-100

    // posts/recent?tag=join(tags, " ")&count=maxResults
    Element fetchRecentBookmarksByTags(Iterable<String> tags, Integer maxResults); // Range 1-100

    // posts/dates
    Element fetchAllBookmarkDays();

    // posts/dates?tag=join(tags, " ")
    Element fetchBookmarkDaysByTags(Iterable<String> tags);

    // TODO ...
}
