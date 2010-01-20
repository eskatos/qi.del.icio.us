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

import java.util.List;
import org.codeartisans.blob.domain.entities.ThingEntity;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( DomainEventsFactory.Mixin.class )
public interface DomainEventsFactory
        extends ServiceComposite
{

    ThingCreatedEvent newThingCreatedEvent( String name, String shortdesc, List<String> tags );

    TagRenamedEvent newTagRenamedEvent( String identity, String newName );

    abstract class Mixin
            implements DomainEventsFactory
    {

        @Structure
        UnitOfWorkFactory unitOfWorkFactory;
        @Service
        UuidIdentityGeneratorService uuidGenerator;

        public ThingCreatedEvent newThingCreatedEvent( String name, String shortdesc, List<String> tags )
        {
            UnitOfWork uow = unitOfWorkFactory.currentUnitOfWork();
            EntityBuilder<ThingCreatedEvent> builder = uow.newEntityBuilder( ThingCreatedEvent.class );
            ThingCreatedEvent state = builder.instance();
            state.thingIdentity().set( uuidGenerator.generate( ThingEntity.class ) );
            state.name().set( name );
            state.shortdesc().set( shortdesc );
            state.tags().set( tags );
            return builder.newInstance();
        }

        public TagRenamedEvent newTagRenamedEvent( String tagIdentity, String newName )
        {
            UnitOfWork uow = unitOfWorkFactory.currentUnitOfWork();
            EntityBuilder<TagRenamedEvent> builder = uow.newEntityBuilder( TagRenamedEvent.class );
            TagRenamedEvent state = builder.instance();
            state.tagIdentity().set( tagIdentity );
            state.newName().set( newName );
            return builder.newInstance();
        }

    }

}
