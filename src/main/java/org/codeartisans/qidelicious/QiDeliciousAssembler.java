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
package org.codeartisans.qidelicious;

import org.codeartisans.qidelicious.core.Day;
import org.codeartisans.qidelicious.core.Post;
import org.codeartisans.qidelicious.sync.SyncService;
import org.codeartisans.qidelicious.core.Tag;
import org.codeartisans.qidelicious.core.TagBundle;
import org.codeartisans.qidelicious.remote.RemoteConfiguration;
import org.codeartisans.qidelicious.remote.RemoteDelicious;
import org.codeartisans.qidelicious.remote.RemoteTag;
import org.codeartisans.qidelicious.sync.SyncState;
import org.qi4j.api.common.Visibility;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public class QiDeliciousAssembler
        implements Assembler
{

    public void assemble(ModuleAssembly module) throws AssemblyException
    {
        // Public Entities
        module.addEntities(Tag.class,
                           TagBundle.class,
                           Day.class,
                           Post.class).
                visibleIn(Visibility.application);

        // Internal Services
        module.addServices(SyncService.class,
                           RemoteDelicious.class);

        // Internal Configuration Entities
        module.addEntities(SyncState.class,
                           RemoteConfiguration.class);

        // Internal Transients
        module.addTransients(RemoteTag.class);

        // Infrastructure Services
        module.addServices(MemoryEntityStoreService.class,
                           UuidIdentityGeneratorService.class);
    }

}
