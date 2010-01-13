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
import org.codeartisans.blob.CoolBlobStructure.DomainModules;
import org.codeartisans.blob.CoolBlobStructure.Layers;
import org.codeartisans.blob.FixtureBuilder.FixtureSettings;
import org.codeartisans.blob.FixtureBuilder.Fixtures;
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
import org.qi4j.api.service.ServiceFinder;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.LayerAssembly;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.envisage.Envisage;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class CoolBlobTest
        extends AbstractCoolBlobTest
{

    @Ignore
    @Test
    public void envisage()
            throws InterruptedException
    {
        new Envisage().run(applicationModel);
        Thread.sleep(1113000);
    }

    @Override
    public ApplicationAssembly assemble(ApplicationAssemblyFactory applicationFactory)
            throws AssemblyException
    {
        ApplicationAssembly assembly = super.assemble(applicationFactory);
        LayerAssembly domain = assembly.layerAssembly(Layers.DOMAIN);
        ModuleAssembly events = domain.moduleAssembly(DomainModules.EVENTS);
        events.addObjects(FixtureBuilder.class);
        return assembly;
    }

    @Test // TODO Unit test FixtureBuilder itself asserting settings are strictly followed.
    public void testFixtureBuilder()
            throws UnitOfWorkCompletionException
    {
        Module eventsModule = application.findModule(Layers.DOMAIN, DomainModules.EVENTS);
        FixtureBuilder builder = eventsModule.objectBuilderFactory().newObject(FixtureBuilder.class);
        FixtureSettings settings = builder.settingsPrototype();
        settings.thingsNumber(100);
        settings.tagsNumber(100);
        Fixtures fixtures = builder.populateEventsStore();

        UnitOfWork uow = eventsModule.unitOfWorkFactory().newUnitOfWork();

        uow.complete();
    }

    @Ignore
    @Test
    public void test()
            throws InterruptedException, UnitOfWorkCompletionException
    {

        ThingCreatedEvent thingCreatedEvent;
        TagRenamedEvent tagRenamedEvent;

        Module eventsModule = application.findModule(Layers.DOMAIN, DomainModules.EVENTS);
        Module modelModule = application.findModule(Layers.DOMAIN, DomainModules.MODEL);
        UnitOfWorkFactory modelUowf = modelModule.unitOfWorkFactory();
        ServiceFinder modelServiceFinder = modelModule.serviceFinder();
        UnitOfWorkFactory eventsUowf = eventsModule.unitOfWorkFactory();
        ServiceFinder eventsServiceFinder = eventsModule.serviceFinder();

        // Checking RootEntity
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            try {
                RootEntity root = uow.get(RootEntity.class, CoolBlobStructure.ROOT_ENTITY_IDENTITY);
                Assert.assertEquals(CoolBlobStructure.ROOT_ENTITY_IDENTITY, root.identity().get());
                Assert.assertNull(root.lastProcessedEventDateTime().get());

            } catch (NoSuchEntityException ex) {
                ex.printStackTrace();
                Assert.fail("Should work");
            }

            uow.complete();
            Thread.sleep(100);
        }

        // Creating a DomainEvent : ThingCreatedEvent
        {
            UnitOfWork uow = eventsUowf.newUnitOfWork();

            ServiceReference<DomainEventsFactory> defRef = eventsServiceFinder.findService(DomainEventsFactory.class);
            DomainEventsFactory eventsFactory = defRef.get();

            thingCreatedEvent = eventsFactory.newThingCreatedEvent("Blob", "This is a blob",
                                                                   Arrays.asList("qi4j", "cop", "ddd"));

            uow.complete();
            Thread.sleep(100);
        }

        // Applying ThingCreatedEvent
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            thingCreatedEvent = uow.get(thingCreatedEvent);

            RootEntity root = uow.get(RootEntity.class, CoolBlobStructure.ROOT_ENTITY_IDENTITY);
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

            RootEntity root = uow.get(RootEntity.class, CoolBlobStructure.ROOT_ENTITY_IDENTITY);
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
