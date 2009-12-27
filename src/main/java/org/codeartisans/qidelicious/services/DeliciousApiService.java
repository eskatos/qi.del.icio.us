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
package org.codeartisans.qidelicious.services;

import org.codeartisans.qidelicious.domain.day.Day;
import org.codeartisans.qidelicious.domain.post.Post;
import org.codeartisans.qidelicious.domain.tag.Tag;

/**
 * Only for internal use, Mixins are HTTP delicious api wrappers.
 * See: http://delicious.com/help/api
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
public interface DeliciousApiService
{

    Iterable<Day> findAllDeliciousDays();

    Iterable<Day> findDeliciousDaysByTags(Iterable<Tag> tags);

    Iterable<Tag> findAllTags();

    Iterable<Post> findAllPosts();

    Iterable<Post> findPostsByTags(Iterable<Tag> tags);

    Iterable<Post> findPostsByDay(Day day);

    Iterable<Post> findPostsByTagsAndDay(Iterable<Tag> tags, Day day);

    Post findPostByURL(String url);

    Iterable<Post> findAllRecentPosts(); // ???

    Iterable<Post> findRecentPostsByTags(Iterable<Tag> tags); // ???

}
