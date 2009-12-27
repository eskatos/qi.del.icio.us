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

import del.icio.us.Delicious;
import java.util.LinkedList;
import java.util.List;
import org.codeartisans.qidelicious.domain.Day;
import org.codeartisans.qidelicious.domain.Post;
import org.codeartisans.qidelicious.domain.Tag;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

/**
 * For unit tests to pass and before configuration is working, change the username and password below.
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins(DeliciousApiServiceComposite.Mixin.class)
public interface DeliciousApiServiceComposite
        extends DeliciousApiService, ServiceComposite
{

    abstract class Mixin
            implements DeliciousApiService
    {

        private static final String USERNAME = "USERNAME";
        private static final String PASSWORD = "PASSWORD";
        private static Delicious delicious;
        @Structure
        private UnitOfWorkFactory uowf;

        public Mixin()
        {
            if (delicious == null) {
                delicious = new Delicious(USERNAME, PASSWORD, "https://api.delicious.com/v1/");
            }
        }

        public Iterable<Day> findAllDeliciousDays()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Day> findDeliciousDaysByTags(Iterable<Tag> tags)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<Tag> findAllTags()
        {
            List<del.icio.us.beans.Tag> remoteTags = delicious.getTags();
            List<Tag> tags = new LinkedList<Tag>();
            UnitOfWork uow = uowf.currentUnitOfWork();
            System.out.println(uow);
            for (del.icio.us.beans.Tag eachRemoteTag : remoteTags) {
                EntityBuilder<Tag> tagBuilder = uow.newEntityBuilder(Tag.class);
                Tag proto = tagBuilder.instance();
                proto.tag().set(eachRemoteTag.getTag());
                proto.count().set(eachRemoteTag.getCount());
                tags.add(tagBuilder.newInstance());
            }
            return tags;
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
