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
package org.codeartisans.blob.events;

import org.joda.time.DateTime;
import org.qi4j.api.entity.Lifecycle;
import org.qi4j.api.entity.LifecycleException;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Immutable;
import org.qi4j.api.property.Property;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( DomainEvent.Mixin.class )
public interface DomainEvent
        extends Lifecycle
{

    @Immutable
    Property<DateTime> recordDate();

    @Immutable
    Property<Long> eventNumber();

    abstract class Mixin
            implements DomainEvent
    {

        @This
        private DomainEvent meAsDomainEvent;
        @Service
        private DomainEventsRepository domainEventsRepos;

        public void create()
                throws LifecycleException
        {
            DateTime now = new DateTime();
            meAsDomainEvent.recordDate().set( now );
            DomainEvent lastRecorded = domainEventsRepos.findLastRecordedEvent();
            if ( lastRecorded == null ) {
                meAsDomainEvent.eventNumber().set( 1L );
            } else {
                meAsDomainEvent.eventNumber().set( lastRecorded.eventNumber().get() + 1 );
            }
        }

        public void remove()
                throws LifecycleException
        {
            // NOOP
        }

    }

}
