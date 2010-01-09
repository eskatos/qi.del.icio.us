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
import junit.framework.Assert;
import org.codeartisans.blob.domain.entities.RootEntity;
import org.codeartisans.blob.domain.entities.TagEntity;
import org.codeartisans.blob.domain.entities.TagRepository;
import org.codeartisans.blob.domain.entities.ThingEntity;
import org.codeartisans.blob.events.DomainEventsFactory;
import org.codeartisans.blob.events.TagRenamedEvent;
import org.codeartisans.blob.events.ThingCreatedEvent;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.service.ServiceFinder;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.structure.Application;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.envisage.Envisage;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class CoolBlobTest
        extends AbstractQi4jApplicationTest
{

    public ApplicationAssembly assemble(ApplicationAssemblyFactory applicationFactory)
            throws AssemblyException
    {
        ApplicationAssembly assembly = new CoolBlobAssembler().assemble(applicationFactory);
        assembly.setMode(Application.Mode.test);
        return assembly;
    }

    @Ignore
    @Test
    public void envisage()
            throws InterruptedException
    {
        System.out.println("Testing Qi4j :)");
        new Envisage().run(applicationModel);
        Thread.sleep(1113000);
    }

    @Test
    public void test()
            throws InterruptedException, UnitOfWorkCompletionException
    {

        final String rootIdentity = "FUCK_A_DUCK";
        ThingCreatedEvent thingCreatedEvent;
        TagRenamedEvent tagRenamedEvent;

        Module eventsModule = application.findModule(CoolBlobStructure.Layers.DOMAIN, CoolBlobStructure.DomainModules.EVENTS);
        Module modelModule = application.findModule(CoolBlobStructure.Layers.DOMAIN, CoolBlobStructure.DomainModules.MODEL);
        UnitOfWorkFactory modelUowf = modelModule.unitOfWorkFactory();
        ServiceFinder modelServiceFinder = modelModule.serviceFinder();
        UnitOfWorkFactory eventsUowf = eventsModule.unitOfWorkFactory();
        ServiceFinder eventsServiceFinder = eventsModule.serviceFinder();

        // Creating RootEntity
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

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
            UnitOfWork uow = eventsUowf.newUnitOfWork();

            ServiceReference<UuidIdentityGeneratorService> uuidRef = eventsServiceFinder.findService(UuidIdentityGeneratorService.class);
            ServiceReference<DomainEventsFactory> defRef = eventsServiceFinder.findService(DomainEventsFactory.class);
            UuidIdentityGeneratorService uuidGenerator = uuidRef.get();
            DomainEventsFactory eventsFactory = defRef.get();

            thingCreatedEvent = eventsFactory.newThingCreatedEvent(uuidGenerator.generate(ThingEntity.class),
                                                                   "Blob", "This is a blob",
                                                                   Arrays.asList("qi4j", "cop", "ddd"));

            uow.complete();
        }
        Thread.sleep(100);

        // Applying ThingCreatedEvent
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

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
            UnitOfWork uow = eventsUowf.newUnitOfWork();

            ServiceReference<DomainEventsFactory> defRef = eventsServiceFinder.findService(DomainEventsFactory.class);
            ServiceReference<TagRepository> trRef = modelServiceFinder.findService(TagRepository.class);
            DomainEventsFactory eventsFactory = defRef.get();
            TagRepository tagRepos = trRef.get();


            tagRenamedEvent = eventsFactory.newTagRenamedEvent(tagRepos.findByName("ddd").identity().get(),
                                                               "dddd");


            uow.complete();
        }

        // Applying TagRenamedEvent
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            tagRenamedEvent = uow.get(tagRenamedEvent);

            RootEntity root = uow.get(RootEntity.class, rootIdentity);
            TagEntity renamedTag = root.tagRenamed(tagRenamedEvent);
            Assert.assertEquals("dddd", renamedTag.name().get());

            uow.complete();
        }
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            uow.complete();
        }
    }

}
