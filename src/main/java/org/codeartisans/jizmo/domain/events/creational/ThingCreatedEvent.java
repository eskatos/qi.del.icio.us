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
package org.codeartisans.jizmo.domain.events.creational;

import java.util.List;
import org.codeartisans.jizmo.domain.model.fragments.Name;
import org.codeartisans.jizmo.domain.model.fragments.Text;
import org.codeartisans.jizmo.domain.events.DomainEvent;
import org.qi4j.api.common.Optional;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.property.Property;
import org.qi4j.library.constraints.annotation.MaxLength;
import org.qi4j.library.constraints.annotation.NotEmpty;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public interface ThingCreatedEvent
        extends DomainEvent, EntityComposite
{

    @NotEmpty
    Property<String> thingIdentity();

    @NotEmpty
    @MaxLength( 256 )
    Name name();

    @Optional
    @MaxLength( 1024 )
    Text description();

    @Optional
    // QUID How to ensure here that each tag is under 128 characters ?
    Property<List<String>> tags();

}
