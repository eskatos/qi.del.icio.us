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
import del.icio.us.beans.Bundle;
import del.icio.us.beans.DeliciousDate;
import del.icio.us.beans.Tag;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins(RemoteDeliciousService.Mixin.class)
public interface RemoteDeliciousService
        extends RemoteDelicious, ServiceComposite
{

    abstract class Mixin
            implements RemoteDeliciousService
    {

        @This
        private Configuration<RemoteConfiguration> config;
        @Service
        private RemoteFactory remoteFactory;
        // Using delicious-java ATM
        private Delicious delicious;
        // For throttling
        private DateTime lastRequest;

        public DateTime findLastRemoteUpdate()
        {
            throttleIfNeeded();
            Date lastUpdate = ensureDelicious().getLastUpdate();
            requestDone();
            return new DateTime(lastUpdate);
        }

        public Iterable<RemoteDay> findAllDeliciousDays()
        {
            throttleIfNeeded();
            List<DeliciousDate> dates = ensureDelicious().getDatesWithPost();
            requestDone();
            List<RemoteDay> remoteDays = new LinkedList<RemoteDay>();
            for (DeliciousDate eachDate : dates) {
                remoteDays.add(remoteFactory.newRemoteDayInstance(
                        new DateMidnight(eachDate.getDate())));
            }
            return remoteDays;
        }

        public Iterable<RemoteDay> findDeliciousDaysByTags(Iterable<RemoteTag> tags)
        {
            throttleIfNeeded();
            List<DeliciousDate> dates = ensureDelicious().getDatesWithPost(RemoteUtils.joinRemoteTags(tags));
            List<RemoteDay> remoteDays = new LinkedList<RemoteDay>();
            for (DeliciousDate eachDate : dates) {
                remoteDays.add(remoteFactory.newRemoteDayInstance(
                        new DateMidnight(eachDate.getDate())));
            }
            return remoteDays;
        }

        public Iterable<RemoteTag> findAllTags()
        {
            throttleIfNeeded();
            List<Tag> deliciousTags = ensureDelicious().getTags();
            requestDone();
            List<RemoteTag> remoteTags = new LinkedList<RemoteTag>();
            for (Tag eachDeliciousTag : deliciousTags) {
                remoteTags.add(remoteFactory.newRemoteTagInstance(
                        eachDeliciousTag.getTag(),
                        eachDeliciousTag.getCount()));
            }
            return remoteTags;
        }

        public Iterable<RemoteTagBundle> findAllTagBundles()
        {
            throttleIfNeeded();
            List<Bundle> deliciousBundles = ensureDelicious().getBundles();
            requestDone();
            List<RemoteTagBundle> remoteBundles = new LinkedList<RemoteTagBundle>();
            for (Bundle eachDeliciousBundle : deliciousBundles) {
                remoteBundles.add(remoteFactory.newRemoteTagBundleInstance(
                        eachDeliciousBundle.getName(),
                        RemoteUtils.splitStringTags(eachDeliciousBundle.getTags())));
            }
            return remoteBundles;
        }

        public Iterable<RemotePost> findAllPosts()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<RemotePost> findPostsByTags(Iterable<RemoteTag> tags)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<RemotePost> findPostsByDay(RemoteDay day)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<RemotePost> findPostsByTagsAndDay(Iterable<RemoteTag> tags, RemoteDay day)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public RemotePost findPostByURL(String url)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<RemotePost> findAllRecentPosts()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Iterable<RemotePost> findRecentPostsByTags(Iterable<RemoteTag> tags)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private Delicious ensureDelicious()
        {
            if (delicious == null) {
                RemoteConfiguration cfg = config.configuration();
                System.out.println("RemoteDelicious will use the following credentials: " + cfg.username().get() + ":" + cfg.password().get());
                delicious = new Delicious(cfg.username().get(), cfg.password().get(), "https://api.delicious.com/v1/");
            }
            return delicious;
        }

        private synchronized void throttleIfNeeded()
        {
            if (lastRequest != null && lastRequest.plusSeconds(1).isBeforeNow()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }

        private synchronized void requestDone()
        {
            lastRequest = new DateTime();
        }

    }

}
