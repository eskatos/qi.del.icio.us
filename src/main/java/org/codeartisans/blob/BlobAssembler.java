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

import org.codeartisans.blob.domain.RootEntityService;
import org.codeartisans.blob.domain.things.IlkEntity;
import org.codeartisans.blob.domain.things.RootEntity;
import org.codeartisans.blob.domain.things.SetOfTagsEntity;
import org.codeartisans.blob.domain.things.TagEntity;
import org.codeartisans.blob.domain.things.TagRepository;
import org.codeartisans.blob.domain.things.ThingEntity;
import org.codeartisans.blob.domain.things.ThingFactory;
import org.codeartisans.blob.domain.things.ThingRepository;
import org.codeartisans.blob.domain.values.PayloadValue;
import org.codeartisans.blob.events.DomainEventsFactory;
import org.codeartisans.blob.events.DomainEventsRepository;
import org.codeartisans.blob.events.TagRenamedEvent;
import org.codeartisans.blob.events.ThingCreatedEvent;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

/**
 * See Qi4j Extensions - REST // Unit tests // Main.java & MainAssembler.java
 *
 * TODO Two modules, one for DomainEvents, another one for DomainEntities. The first will contain a persistent
 *      EntityStore, the latter a MemoryEntityStore and a RDF indexer.
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
public class BlobAssembler
        implements Assembler
{

    @Override
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        // Domain Events
        module.addEntities( ThingCreatedEvent.class,
                            TagRenamedEvent.class );
        module.addServices( DomainEventsFactory.class,
                            DomainEventsRepository.class,
                            RootEntityService.class );

        // Entities
        module.addEntities( RootEntity.class,
                            ThingEntity.class,
                            IlkEntity.class,
                            TagEntity.class,
                            SetOfTagsEntity.class );
        module.addValues( PayloadValue.class );
        module.addServices( RootEntityService.class ).instantiateOnStartup();
        module.addServices( ThingFactory.class,
                            ThingRepository.class,
                            TagRepository.class );

        // Infrastructure Services
        module.addServices( MemoryEntityStoreService.class,
                            UuidIdentityGeneratorService.class );
        new RdfMemoryStoreAssembler().assemble( module );

    }

}
