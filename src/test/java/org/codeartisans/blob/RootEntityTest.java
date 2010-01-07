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
package org.codeartisans.blob;

import java.util.Arrays;
import org.codeartisans.blob.domain.entities.RootEntity;
import org.codeartisans.blob.domain.entities.TagEntity;
import org.codeartisans.blob.domain.entities.TagRepository;
import org.codeartisans.blob.domain.entities.ThingEntity;
import org.codeartisans.blob.events.DomainEventsFactory;
import org.codeartisans.blob.events.TagRenamedEvent;
import org.codeartisans.blob.events.ThingCreatedEvent;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

/**
 * TODO Write a test creating a serie of DomainEvents and then playing them ordered by creationDate.
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
public class RootEntityTest
        extends AbstractQi4jTest
{

    public void assemble(ModuleAssembly module)
            throws AssemblyException
    {
        new BlobAssembler().assemble(module);
    }

    @Test
    public void test()
            throws UnitOfWorkCompletionException, InterruptedException
    {
        final String rootIdentity = "FUCK_A_DUCK";
        ThingCreatedEvent thingCreatedEvent;
        TagRenamedEvent tagRenamedEvent;

        // Creating RootEntity
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            try {
                uow.get(RootEntity.class, rootIdentity);
                Assert.fail("Should not work.");
            } catch (NoSuchEntityException ex) {
            }
            EntityBuilder<RootEntity> builder = uow.newEntityBuilder(RootEntity.class, rootIdentity);
            RootEntity root = builder.newInstance();

            Assert.assertNull(root.lastProcessedEventDateTime().get());

            uow.complete();
        }
        Thread.sleep(100);

        // Creating a DomainEvent : ThingCreatedEvent
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            UuidIdentityGeneratorService uuidGenerator = serviceLocator.<UuidIdentityGeneratorService>findService(UuidIdentityGeneratorService.class).get();
            DomainEventsFactory eventsFactory = serviceLocator.<DomainEventsFactory>findService(DomainEventsFactory.class).get();

            thingCreatedEvent = eventsFactory.newThingCreatedEvent(uuidGenerator.generate(ThingEntity.class),
                                                                   "Blob", "This is a blob",
                                                                   Arrays.asList("qi4j", "cop", "ddd"));

            uow.complete();
        }
        Thread.sleep(100);

        // Applying ThingCreatedEvent
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            thingCreatedEvent = uow.get(thingCreatedEvent);

            RootEntity root = uow.get(RootEntity.class, rootIdentity);
            ThingEntity thing = root.newThingCreated(thingCreatedEvent);
            Assert.assertEquals("Blob", thing.name().get());

            DateTime eventCreation = thingCreatedEvent.creationDate().get();
            DateTime lastProcessedEvent = root.lastProcessedEventDateTime().get();
            DateTime now = new DateTime();
            System.out.println("Event created at: " + eventCreation);
            System.out.println("Last processed event datetime: " + lastProcessedEvent);
            System.out.println("Now is: " + now);
            Assert.assertEquals(lastProcessedEvent, eventCreation);
            Assert.assertTrue(now.isAfter(lastProcessedEvent));

            uow.complete();
        }

        // Creating a DomainEvent : TagRenamedEvent
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            UuidIdentityGeneratorService uuidGenerator = serviceLocator.<UuidIdentityGeneratorService>findService(UuidIdentityGeneratorService.class).get();
            DomainEventsFactory eventsFactory = serviceLocator.<DomainEventsFactory>findService(DomainEventsFactory.class).get();
            TagRepository tagRepos = serviceLocator.<TagRepository>findService(TagRepository.class).get();


            tagRenamedEvent = eventsFactory.newTagRenamedEvent(tagRepos.findByName("ddd").identity().get(),
                                                               "dddd");


            uow.complete();
        }

        // Applying TagRenamedEvent
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            tagRenamedEvent = uow.get(tagRenamedEvent);

            RootEntity root = uow.get(RootEntity.class, rootIdentity);
            TagEntity renamedTag = root.tagRenamed(tagRenamedEvent);
            Assert.assertEquals("dddd", renamedTag.name().get());

            uow.complete();
        }
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            uow.complete();
        }
    }

}
