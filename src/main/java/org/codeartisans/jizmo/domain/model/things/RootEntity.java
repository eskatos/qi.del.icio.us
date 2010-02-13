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
package org.codeartisans.jizmo.domain.model.things;

import org.codeartisans.jizmo.domain.events.DomainEvent;
import org.codeartisans.jizmo.domain.events.modificational.TagRenamedEvent;
import org.codeartisans.jizmo.domain.events.creational.ThingCreatedEvent;
import org.codeartisans.java.toolbox.exceptions.NullArgumentException;
import org.qi4j.api.common.Optional;
import org.qi4j.api.concern.ConcernOf;
import org.qi4j.api.concern.Concerns;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.spi.Qi4jSPI;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins( RootEntity.Mixin.class )
@Concerns( RootEntity.Concern.class )
public interface RootEntity
        extends EntityComposite
{

    static String IDENTITY = "563daf02-3e3d-4e75-9a7e-86ab2719ca6c-0";

    @Optional
    Property<Long> lastProcessedEventNumber();

    void processDomainEvent( DomainEvent domainEvent );

    ThingEntity newThingCreated( ThingCreatedEvent thingCreatedEvent );

    TagEntity tagRenamed( TagRenamedEvent tagRenamedEvent );

    abstract class Mixin
            implements RootEntity
    {

        @Structure
        private Qi4jSPI spi;
        @Structure
        private UnitOfWorkFactory uowf;
        @Service
        private ThingFactory thingFactory;
        @Service
        private ThingRepository thingRepos;
        @Service
        private TagRepository tagRepos;

        @Override
        public void processDomainEvent( DomainEvent domainEvent )
        {
            Class entityClass = spi.getEntityDescriptor( ( EntityComposite ) domainEvent ).type();
            if ( entityClass.isAssignableFrom( ThingCreatedEvent.class ) ) {
                newThingCreated( ( ThingCreatedEvent ) domainEvent );
            } else if ( entityClass.isAssignableFrom( TagRenamedEvent.class ) ) {
                tagRenamed( ( TagRenamedEvent ) domainEvent );
            }
        }

        @Override
        public ThingEntity newThingCreated( ThingCreatedEvent event )
        {
            return thingFactory.newThingInstance( event.name().get(),
                                                  event.description().get(),
                                                  event.tags().get() );
        }

        @Override
        public TagEntity tagRenamed( TagRenamedEvent event )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            TagEntity originalTag = tagRepos.findByName( event.oldName().get() );
            NullArgumentException.ensureNotNull( "No tag named " + event.oldName().get(), originalTag );
            TagEntity existingTag = tagRepos.findByName( event.newName().get() );
            if ( existingTag == null ) {
                originalTag.name().set( event.newName().get() );
                return originalTag;
            }
            for ( ThingEntity eachThingWithOriginalTag : thingRepos.findByTag( originalTag.name().get() ) ) {
                eachThingWithOriginalTag.tags().remove( originalTag );
                eachThingWithOriginalTag.tags().add( existingTag );
                existingTag.incrementCount();
            }
            uow.remove( originalTag );
            return existingTag;
        }

    }

    abstract class Concern
            extends ConcernOf<RootEntity>
            implements RootEntity
    {

        @Override
        public ThingEntity newThingCreated( ThingCreatedEvent thingCreatedEvent )
        {
            ThingEntity thing = next.newThingCreated( thingCreatedEvent );
            lastProcessedEventNumber().set( thingCreatedEvent.eventNumber().get() );
            return thing;
        }

        @Override
        public TagEntity tagRenamed( TagRenamedEvent tagRenamedEvent )
        {
            TagEntity tag = next.tagRenamed( tagRenamedEvent );
            lastProcessedEventNumber().set( tagRenamedEvent.eventNumber().get() );
            return tag;
        }

    }

}
