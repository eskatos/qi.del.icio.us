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
package org.codeartisans.qidelicious.sync;

import java.util.Date;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins(SyncService.Mixin.class)
public interface SyncService
        extends ServiceComposite
{

    void clearWorkingCopy();

    void checkoutRemote();

    void updateWorkingCopy();

    abstract class Mixin
            implements SyncService
    {

        @This
        private Configuration<SyncState> config;

        /**
         * For each (owned) entity type, remove all instances ?
         * Reset CacheState
         */
        public void clearWorkingCopy()
        {
            config.refresh();
            SyncState state = config.configuration();
            if (state.lastSeenRemote().get() != null) {
                System.out.println("SyncService::clearWorkingCopy");
            }
        }

        /**
         * Checkout:
         *      throw ISE if local store not empty
         *      fetch all remote_tags
         *      create each local_tag
         *      fetch all remote_tagbundles
         *      create each local_tagbundle
         *      fetch all remote_posts
         *      create each local_post
         *
         * The last request can be long, usage will tell.
         */
        public void checkoutRemote()
        {
            config.refresh();
            SyncState state = config.configuration();
            if (state.lastSeenRemote().get() != null) {
                throw new IllegalStateException("Unable to checkout remote, local store is used");
            }
            System.out.println("SyncService::checkoutRemote");
            config.configuration().lastSeenRemote().set(new Date());
        }

        /**
         * Update:
         *      fetch all remote_tags
         *      create_or_update each local_tag
         *      fetch all remote_tagbundles
         *      create_or_update each local_tagbundle
         *      fetch remote_posts inbox size
         *      if remote_posts inbox size > 0
         *          fetch new remote_posts
         *          create each new local_post
         *
         * This means at least 3 requests, 4 at most. The delicious.com usage policy is one request per second max.
         * So, this is 2 to 3 seconds at least even without any updates occuring.
         * Grmbl. Asynchronous events ?
         */
        public void updateWorkingCopy()
        {
            config.refresh();
            SyncState state = config.configuration();
            System.out.println("SyncService::updateWorkingCopy");
            state.lastSeenRemote().set(new Date());
        }

    }

}
