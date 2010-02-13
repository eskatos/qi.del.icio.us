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

import org.codeartisans.jizmo.domain.model.ModelLifecycleService;
import org.codeartisans.jizmo.domain.model.RootEntityService;
import org.codeartisans.jizmo.domain.model.things.IlkEntity;
import org.codeartisans.jizmo.domain.model.things.RootEntity;
import org.codeartisans.jizmo.domain.model.things.SetOfTagsEntity;
import org.codeartisans.jizmo.domain.model.things.TagEntity;
import org.codeartisans.jizmo.domain.model.things.TagRepository;
import org.codeartisans.jizmo.domain.model.things.ThingEntity;
import org.codeartisans.jizmo.domain.model.things.ThingFactory;
import org.codeartisans.jizmo.domain.model.things.ThingRepository;
import org.codeartisans.jizmo.domain.model.users.RoleAssignmentEntity;
import org.codeartisans.jizmo.domain.model.users.RoleEntity;
import org.codeartisans.jizmo.domain.model.users.UserEntity;
import org.codeartisans.jizmo.domain.model.users.UserRepository;
import org.codeartisans.jizmo.domain.model.values.PayloadValue;
import org.codeartisans.jizmo.domain.events.DomainEventsFactory;
import org.codeartisans.jizmo.domain.events.DomainEventsRepository;
import org.codeartisans.jizmo.domain.events.modificational.TagRenamedEvent;
import org.codeartisans.jizmo.domain.events.creational.ThingCreatedEvent;
import org.codeartisans.jizmo.presentation.http.ResourceSerializer;
import org.codeartisans.jizmo.presentation.http.ResourcesAssembler;
import org.qi4j.api.common.Visibility;
import org.qi4j.api.structure.Application;
import org.qi4j.bootstrap.ApplicationAssembler;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.LayerAssembly;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class JizmoAssembler
        implements ApplicationAssembler
{

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory applicationFactory )
            throws AssemblyException
    {
        ApplicationAssembly app = applicationFactory.newApplicationAssembly();
        app.setMode( Application.Mode.development );
        app.setVersion( "0.1-testing" );
        app.setName( "CoolBlob" );

        LayerAssembly domain = app.layerAssembly( JizmoStructure.Layers.DOMAIN );
        ModuleAssembly domainEvents = domain.moduleAssembly( JizmoStructure.DomainModules.EVENTS );
        {
            // Domain Events
            domainEvents.addEntities( ThingCreatedEvent.class,
                                      TagRenamedEvent.class );
            domainEvents.addServices( DomainEventsFactory.class,
                                      DomainEventsRepository.class ).
                    visibleIn( Visibility.layer );

            // Infrastructure Services
            domainEvents.addServices( MemoryEntityStoreService.class,
                                      UuidIdentityGeneratorService.class ).
                    visibleIn( Visibility.module );
            new RdfMemoryStoreAssembler( null, Visibility.module, Visibility.module ).assemble( domainEvents );
        }
        ModuleAssembly domainModel = domain.moduleAssembly( JizmoStructure.DomainModules.MODEL );
        {
            // Users
            domainModel.addEntities( UserEntity.class,
                                     RoleEntity.class,
                                     RoleAssignmentEntity.class );
            domainModel.addServices( UserRepository.class ).
                    visibleIn( Visibility.application );

            // Things
            domainModel.addEntities( RootEntity.class,
                                     ThingEntity.class,
                                     IlkEntity.class,
                                     TagEntity.class,
                                     SetOfTagsEntity.class );
            domainModel.addValues( PayloadValue.class );
            domainModel.addServices( RootEntityService.class ).
                    visibleIn( Visibility.layer ).instantiateOnStartup();
            domainModel.addServices( ThingFactory.class );
            domainModel.addServices( ThingRepository.class,
                                     TagRepository.class ).
                    visibleIn( Visibility.application );

            domainModel.addServices( ModelLifecycleService.class ).instantiateOnStartup();

            // Infrastructure Services
            domainModel.addServices( MemoryEntityStoreService.class,
                                     UuidIdentityGeneratorService.class );
            new RdfMemoryStoreAssembler().assemble( domainModel );
        }

        LayerAssembly presentation = app.layerAssembly( JizmoStructure.Layers.PRESENTATION );
        ModuleAssembly http = presentation.moduleAssembly( JizmoStructure.PresentationModules.HTTP );
        {
            http.addServices( ResourceSerializer.class );
            new ResourcesAssembler().assemble( http );
        }

        presentation.uses( domain );

        return app;
    }

}
