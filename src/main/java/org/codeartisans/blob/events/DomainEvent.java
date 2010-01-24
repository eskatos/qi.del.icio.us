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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.qi4j.api.entity.Lifecycle;
import org.qi4j.api.entity.LifecycleException;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Immutable;
import org.qi4j.api.property.Property;
import org.qi4j.spi.util.Base64Encoder;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( DomainEvent.Mixin.class )
public interface DomainEvent
        extends Lifecycle
{

    @Immutable
    Property<DateTime> creationDate();

    // FIXME QUID ? Not used ATM.
    @Immutable
    Property<String> eventHash();

    abstract class Mixin
            implements DomainEvent
    {

        @This
        private DomainEvent meAsDomainEvent;

        public void create()
                throws LifecycleException
        {
            try {
                DateTime now = new DateTime();
                meAsDomainEvent.creationDate().set( now );
                MessageDigest md = MessageDigest.getInstance( "SHA" );
                md.update( ( now.getMillis() + UUID.randomUUID().toString() ).getBytes() );
                meAsDomainEvent.eventHash().set( new String( Base64Encoder.encode( md.digest(), false ) ) );
            } catch ( NoSuchAlgorithmException ex ) {
                throw new LifecycleException( "Unable to compute DomainEvent hash", ex );
            }

        }

        public void remove()
                throws LifecycleException
        {
            // NOOP
        }

    }

}
