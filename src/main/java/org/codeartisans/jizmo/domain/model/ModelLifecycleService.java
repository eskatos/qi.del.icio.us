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
package org.codeartisans.jizmo.domain.model;

import java.util.Arrays;
import org.codeartisans.jizmo.domain.model.things.RootEntity;
import org.codeartisans.jizmo.domain.events.DomainEvent;
import org.codeartisans.jizmo.domain.events.DomainEventsFactory;
import org.codeartisans.jizmo.domain.events.DomainEventsRepository;
import org.codeartisans.jizmo.domain.events.creational.ThingCreatedEvent;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.Activatable;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( ModelLifecycleService.Mixin.class )
public interface ModelLifecycleService
        extends Activatable, ServiceComposite
{

    abstract class Mixin
            implements ModelLifecycleService
    {

        private static final Logger LOGGER = LoggerFactory.getLogger( ModelLifecycleService.Mixin.class );
        @Structure
        private UnitOfWorkFactory uowf;
        @Service
        private RootEntityService rootEntityService;
        @Service
        private DomainEventsRepository domainEventsRepos;

        @Override
        public void activate()
                throws Exception
        {
            LOGGER.info( "Activating Domain Layer" );
            populateTestData();
            UnitOfWork uow = uowf.newUnitOfWork();
            RootEntity root = rootEntityService.rootEntity();
            Iterable<DomainEvent> toProcessEvents;
            if ( root.lastProcessedEventNumber().get() == null ) {
                toProcessEvents = domainEventsRepos.findAll();
            } else {
                toProcessEvents = domainEventsRepos.findWithNumberGreaterThan( root.lastProcessedEventNumber().get() );
            }
            if ( toProcessEvents != null ) {
                for ( DomainEvent eachEvent : toProcessEvents ) {
                    root.processDomainEvent( eachEvent );
                }
            }
            uow.complete();
        }

        @Override
        public void passivate()
                throws Exception
        {
            LOGGER.info( "Passivating Domain Layer" );
        }

        @Service
        private DomainEventsFactory eventsFactory;

        private void populateTestData()
                throws UnitOfWorkCompletionException
        {
            UnitOfWork uow = uowf.newUnitOfWork();
            ThingCreatedEvent thingCreatedEvent = eventsFactory.newThingCreatedEvent( "Blob", "This is a blob", Arrays.asList( "qi4j", "cop", "ddd" ) );
            uow.complete();

        }

    }

}
