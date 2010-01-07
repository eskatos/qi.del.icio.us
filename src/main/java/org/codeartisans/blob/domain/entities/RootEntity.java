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
package org.codeartisans.blob.domain.entities;

import org.codeartisans.blob.events.TagRenamedEvent;
import org.codeartisans.blob.events.ThingCreatedEvent;
import org.joda.time.DateTime;
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

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins(RootEntity.Mixin.class)
@Concerns(RootEntity.Concern.class)
public interface RootEntity
        extends EntityComposite
{

    @Optional
    Property<DateTime> lastProcessedEventDateTime();

    ThingEntity newThingCreated(ThingCreatedEvent thingCreatedEvent);

    TagEntity tagRenamed(TagRenamedEvent tagRenamedEvent);

    abstract class Mixin
            implements RootEntity
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Service
        private TagRepository tagRepos;

        public ThingEntity newThingCreated(ThingCreatedEvent thingCreatedEvent)
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            ThingEntity thing = uow.newEntity(ThingEntity.class, thingCreatedEvent.thingIdentity().get());
            thing.name().set(thingCreatedEvent.name().get());
            thing.shortdesc().set(thingCreatedEvent.shortdesc().get());
            if (thingCreatedEvent.tags().get() != null) {
                for (String eachTag : thingCreatedEvent.tags().get()) {
                    TagEntity tagEntity = tagRepos.findByName(eachTag);
                    if (tagEntity == null) {
                        tagEntity = uow.newEntity(TagEntity.class);
                        tagEntity.name().set(eachTag);
                    }
                    tagEntity.count().set(tagEntity.count().get() + 1);
                    thing.tags().add(tagEntity);
                }
            }
            thing.mimeType().set(thingCreatedEvent.mimetype().get());
            return thing;
        }

        public TagEntity tagRenamed(TagRenamedEvent tagRenamedEvent)
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            TagEntity tag = uow.get(TagEntity.class, tagRenamedEvent.tagIdentity().get());
            tag.name().set(tagRenamedEvent.newName().get());
            return tag;
        }

    }

    abstract class Concern
            extends ConcernOf<RootEntity>
            implements RootEntity
    {

        public ThingEntity newThingCreated(ThingCreatedEvent thingCreatedEvent)
        {
            ThingEntity thing = next.newThingCreated(thingCreatedEvent);
            lastProcessedEventDateTime().set(thingCreatedEvent.creationDate().get());
            return thing;
        }

        public TagEntity tagRenamed(TagRenamedEvent tagRenamedEvent)
        {
            TagEntity tag = next.tagRenamed(tagRenamedEvent);
            lastProcessedEventDateTime().set(tagRenamedEvent.creationDate().get());
            return tag;
        }

    }

}
