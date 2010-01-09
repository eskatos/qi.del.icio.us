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

import org.codeartisans.blob.domain.entities.RootEntity;
import org.codeartisans.blob.domain.entities.SetOfTagsEntity;
import org.codeartisans.blob.domain.entities.TagEntity;
import org.codeartisans.blob.domain.entities.TagRepository;
import org.codeartisans.blob.domain.entities.ThingEntity;
import org.codeartisans.blob.events.DomainEventsFactory;
import org.codeartisans.blob.events.TagRenamedEvent;
import org.codeartisans.blob.events.ThingCreatedEvent;
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
public class CoolBlobAssembler
        implements ApplicationAssembler
{

    public ApplicationAssembly assemble(ApplicationAssemblyFactory applicationFactory)
            throws AssemblyException
    {
        ApplicationAssembly app = applicationFactory.newApplicationAssembly();
        app.setMode(Application.Mode.development);
        app.setVersion("0.1-testing");
        app.setName("CoolBlob");
        LayerAssembly domain = app.layerAssembly(CoolBlobStructure.Layers.DOMAIN);
        ModuleAssembly domainEvents = domain.moduleAssembly(CoolBlobStructure.DomainModules.EVENTS);
        ModuleAssembly domainModel = domain.moduleAssembly(CoolBlobStructure.DomainModules.MODEL);
        {
            // Domain Events
            domainEvents.addEntities(ThingCreatedEvent.class,
                                     TagRenamedEvent.class);
            domainEvents.addServices(DomainEventsFactory.class);

            // Infrastructure Services
            domainEvents.addServices(MemoryEntityStoreService.class,
                                     UuidIdentityGeneratorService.class);
            new RdfMemoryStoreAssembler().assemble(domainEvents);
        }
        {
            // Entities
            domainModel.addEntities(RootEntity.class,
                                    ThingEntity.class,
                                    TagEntity.class,
                                    SetOfTagsEntity.class);
            domainModel.addServices(TagRepository.class);

            // Infrastructure Services
            domainModel.addServices(MemoryEntityStoreService.class,
                                    UuidIdentityGeneratorService.class);
            new RdfMemoryStoreAssembler().assemble(domainModel);
        }
        return app;
    }

}
