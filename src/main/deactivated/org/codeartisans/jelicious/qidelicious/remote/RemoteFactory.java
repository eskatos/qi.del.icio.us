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

import org.joda.time.DateMidnight;
import org.qi4j.api.composite.TransientBuilder;
import org.qi4j.api.composite.TransientBuilderFactory;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins(RemoteFactory.Mixin.class)
public interface RemoteFactory
        extends ServiceComposite
{

    RemoteDay newRemoteDayInstance(DateMidnight dateMidnight);

    RemoteTag newRemoteTagInstance(String tag, Integer count);

    RemoteTagBundle newRemoteTagBundleInstance(String name, Iterable<String> tags);

    abstract class Mixin
            implements RemoteFactory
    {

        @Structure
        private TransientBuilderFactory tbf;

        public RemoteDay newRemoteDayInstance(DateMidnight dateMidnight)
        {
            TransientBuilder<RemoteDay> dayBuilder = tbf.newTransientBuilder(RemoteDay.class);
            RemoteDay proto = dayBuilder.prototype();
            proto.dateMidnight().set(dateMidnight);
            return dayBuilder.newInstance();
        }

        public RemoteTag newRemoteTagInstance(String tag, Integer count)
        {
            TransientBuilder<RemoteTag> tagBuilder = tbf.newTransientBuilder(RemoteTag.class);
            RemoteTag proto = tagBuilder.prototype();
            proto.tag().set(tag);
            proto.count().set(count);
            return tagBuilder.newInstance();
        }

        public RemoteTagBundle newRemoteTagBundleInstance(String name, Iterable<String> tags)
        {
            TransientBuilder<RemoteTagBundle> tagBundleBuilder = tbf.newTransientBuilder(RemoteTagBundle.class);
            RemoteTagBundle proto = tagBundleBuilder.prototype();
            proto.name().set(name);
            proto.tags().set(tags);
            return tagBundleBuilder.newInstance();
        }

    }

}
