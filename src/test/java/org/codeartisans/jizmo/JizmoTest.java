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
package org.codeartisans.jizmo;

import java.util.Arrays;
import junit.framework.Assert;
import org.codeartisans.jizmo.JizmoStructure.DomainModules;
import org.codeartisans.jizmo.JizmoStructure.Layers;
import org.codeartisans.jizmo.FixtureBuilder.FixtureSettings;
import org.codeartisans.jizmo.FixtureBuilder.Fixtures;
import org.codeartisans.jizmo.domain.model.things.RootEntity;
import org.codeartisans.jizmo.domain.model.things.TagEntity;
import org.codeartisans.jizmo.domain.model.things.ThingEntity;
import org.codeartisans.jizmo.domain.model.things.ThingFactory;
import org.codeartisans.jizmo.domain.events.DomainEventsFactory;
import org.codeartisans.jizmo.domain.events.modificational.TagRenamedEvent;
import org.codeartisans.jizmo.domain.events.creational.ThingCreatedEvent;
import org.codeartisans.jizmo.domain.model.RootEntityService;
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
 * Graph navigation
 *
 *
 * RootAggregates are the base navigational element, Aggregated Entities subgraphs are foldables.
 * Association and ManyAssociation are vectors between Entities (RootAggregates and Aggregated)
 *
 * Selection vs Filter
 *
 * Choose focus (RootAggregates and then Aggregated Entities)
 * Provide a way to create groupable queries? on Assocations, layout subgraphs in the ui and style them.
 * At start implement RockBottom and TopLevel styles & layout.
 * Links to RootAggregates are thicks and leads to big single nodes, when clicked the viewport move them at its center.
 * Links to Aggregated Entities are thins and leads to
 * A detail view show the current RootAggregate focused in navigation ui
 *
 *
 *
 * Faire des vues de parcours focalisées sur un type d'entité (RootAggregates per BoundedContext ?)
 *
 *
 *
 * @author Paul Merlin <paul@nosphere.org>
 */
public class JizmoTest
        extends AbstractJizmoTest
{

    @Ignore
    @Test
    public void envisage()
            throws InterruptedException
    {
        new Envisage().run( applicationModel );
        Thread.sleep( 1113000 );
    }

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory applicationFactory )
            throws AssemblyException
    {
        ApplicationAssembly assembly = super.assemble( applicationFactory );
        LayerAssembly domain = assembly.layerAssembly( Layers.DOMAIN );
        ModuleAssembly events = domain.moduleAssembly( DomainModules.EVENTS );
        events.addObjects( FixtureBuilder.class );
        return assembly;
    }

    @Ignore
    @Test // TODO Unit test FixtureBuilder itself asserting settings are strictly followed.
    public void testFixtureBuilder()
            throws UnitOfWorkCompletionException
    {
        Module eventsModule = application.findModule( Layers.DOMAIN, DomainModules.EVENTS );
        FixtureBuilder builder = eventsModule.objectBuilderFactory().newObject( FixtureBuilder.class );
        FixtureSettings settings = builder.settingsPrototype();
        settings.thingsNumber( 100 );
        settings.tagsNumber( 100 );
        Fixtures fixtures = builder.populateEventsStore();

        UnitOfWork uow = eventsModule.unitOfWorkFactory().newUnitOfWork();

        uow.complete();
    }

    @Test
    public void test()
            throws UnitOfWorkCompletionException
    {
        Module modelModule = application.findModule( Layers.DOMAIN, DomainModules.MODEL );
        ServiceReference<ThingFactory> ref = modelModule.serviceFinder().findService( ThingFactory.class );

        ThingFactory thingFactory = ref.get();
        UnitOfWorkFactory modelUowf = modelModule.unitOfWorkFactory();

        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            ThingEntity truc = thingFactory.newThingInstance( "Truc", "Mega truc", Arrays.asList( "foo", "bar" ) );
            ThingEntity machin = thingFactory.newThingInstance( "Machin", "Super machin", Arrays.asList( "zoo", "jar" ) );

            uow.apply(); // WARNING !!

            ThingEntity bidule = thingFactory.newThingInstance( "Bidule", "Uber bidule", Arrays.asList( "zoo", "bar" ) );

            uow.complete();
        }
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            ThingEntity ersatz = thingFactory.newThingInstance( "Ersatz", "Terrible ersatz", Arrays.asList( "foo", "jar" ) );

            uow.complete();
        }
    }

    @Test
    public void testDomainEvents()
            throws UnitOfWorkCompletionException
    {

        ThingCreatedEvent thingCreatedEvent;
        TagRenamedEvent tagRenamedEvent;

        Module eventsModule = application.findModule( Layers.DOMAIN, DomainModules.EVENTS );
        Module modelModule = application.findModule( Layers.DOMAIN, DomainModules.MODEL );
        UnitOfWorkFactory modelUowf = modelModule.unitOfWorkFactory();
        UnitOfWorkFactory eventsUowf = eventsModule.unitOfWorkFactory();
        ServiceFinder eventsServiceFinder = eventsModule.serviceFinder();
        RootEntityService rootEntityService = eventsServiceFinder.<RootEntityService>findService( RootEntityService.class ).get();

        // Checking RootEntity
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            try {
                RootEntity root = rootEntityService.rootEntity();
                Assert.assertEquals( RootEntity.IDENTITY, root.identity().get() );
                // Assert.assertNull( root.lastProcessedEventDateTime().get() ); WARN Commented because of DomainLifecycleService creating test data upon activation

            } catch ( NoSuchEntityException ex ) {
                ex.printStackTrace();
                Assert.fail( "Should work" );
            }

            uow.complete();
        }

        // Creating a DomainEvent : ThingCreatedEvent
        {
            UnitOfWork uow = eventsUowf.newUnitOfWork();

            ServiceReference<DomainEventsFactory> defRef = eventsServiceFinder.findService( DomainEventsFactory.class );
            DomainEventsFactory eventsFactory = defRef.get();

            thingCreatedEvent = eventsFactory.newThingCreatedEvent( "Blob", "This is a blob",
                                                                    Arrays.asList( "qi4j", "cop", "ddd" ) );

            uow.complete();
        }

        // Applying ThingCreatedEvent
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            thingCreatedEvent = uow.get( thingCreatedEvent );

            System.out.println( "ThingCreatedEvent: " + thingCreatedEvent.eventNumber().get() );

            RootEntity root = rootEntityService.rootEntity();
            ThingEntity thing = root.newThingCreated( thingCreatedEvent );
            Assert.assertEquals( "Blob", thing.name().get() );

            Long eventNumber = thingCreatedEvent.eventNumber().get();
            Long lastProcessedEventNumber = root.lastProcessedEventNumber().get();
            System.out.println( "Event number: " + eventNumber );
            System.out.println( "Last processed event number: " + lastProcessedEventNumber );
            Assert.assertEquals( lastProcessedEventNumber, eventNumber );

            uow.complete();
        }

        // Creating a DomainEvent : TagRenamedEvent
        {
            UnitOfWork uow = eventsUowf.newUnitOfWork();

            ServiceReference<DomainEventsFactory> defRef = eventsServiceFinder.findService( DomainEventsFactory.class );
            DomainEventsFactory eventsFactory = defRef.get();

            tagRenamedEvent = eventsFactory.newTagRenamedEvent( "ddd", "dddd" );

            uow.complete();
        }

        // Applying TagRenamedEvent
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            tagRenamedEvent = uow.get( tagRenamedEvent );

            RootEntity root = rootEntityService.rootEntity();
            TagEntity renamedTag = root.tagRenamed( tagRenamedEvent );
            Assert.assertNotNull( renamedTag );
            Assert.assertNotNull( renamedTag.name() );
            Assert.assertEquals( "dddd", renamedTag.name().get() );

            uow.complete();
        }
        {
            UnitOfWork uow = modelUowf.newUnitOfWork();

            uow.complete();
        }
    }

}
