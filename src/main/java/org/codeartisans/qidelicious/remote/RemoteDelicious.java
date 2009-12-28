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
package org.codeartisans.qidelicious.remote;

import del.icio.us.Delicious;
import java.util.LinkedList;
import java.util.List;
import org.codeartisans.qidelicious.core.Day;
import org.codeartisans.qidelicious.core.Post;
import org.codeartisans.qidelicious.core.Tag;
import org.qi4j.api.composite.TransientBuilder;
import org.qi4j.api.composite.TransientBuilderFactory;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins(RemoteDelicious.Mixin.class)
public interface RemoteDelicious
        extends ServiceComposite
{

    Iterable<Day> findAllDeliciousDays();

    Iterable<Day> findDeliciousDaysByTags(Iterable<Tag> tags);

    Iterable<RemoteTag> findAllTags();

    Iterable<Post> findAllPosts();

    Iterable<Post> findPostsByTags(Iterable<Tag> tags);

    Iterable<Post> findPostsByDay(Day day);

    Iterable<Post> findPostsByTagsAndDay(Iterable<Tag> tags, Day day);

    Post findPostByURL(String url);

    Iterable<Post> findAllRecentPosts(); // ???

    Iterable<Post> findRecentPostsByTags(Iterable<Tag> tags); // ???

    abstract class Mixin
            implements RemoteDelicious
    {

        @Structure
        private TransientBuilderFactory tbf;
        @This
        private Configuration<RemoteConfiguration> config;
        private Delicious delicious;

        private Delicious ensureDelicious()
        {
            if (delicious == null) {
                RemoteConfiguration cfg = config.configuration();
                System.out.println("RemoteDelicious will use the following credentials: " + cfg.username().get() + ":" + cfg.password().get());
                delicious = new Delicious(cfg.username().get(), cfg.password().get(), "https://api.delicious.com/v1/");
            }
            return delicious;
        }

        public Iterable<Day> findAllDeliciousDays()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Day> findDeliciousDaysByTags(Iterable<Tag> tags)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<RemoteTag> findAllTags()
        {
            List<del.icio.us.beans.Tag> deliciousTags = ensureDelicious().getTags();
            List<RemoteTag> remoteTags = new LinkedList<RemoteTag>();
            for (del.icio.us.beans.Tag eachDeliciousTag : deliciousTags) {
                TransientBuilder<RemoteTag> tagBuilder = tbf.newTransientBuilder(RemoteTag.class);
                RemoteTag proto = tagBuilder.prototype();
                proto.tag().set(eachDeliciousTag.getTag());
                proto.count().set(eachDeliciousTag.getCount());
                remoteTags.add(tagBuilder.newInstance());
            }
            return remoteTags;
        }

        public Iterable<Post> findAllPosts()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Post> findPostsByTags(Iterable<Tag> tags)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Post> findPostsByDay(Day day)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Post> findPostsByTagsAndDay(Iterable<Tag> tags, Day day)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Post findPostByURL(String url)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Post> findAllRecentPosts()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Post> findRecentPostsByTags(Iterable<Tag> tags)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
