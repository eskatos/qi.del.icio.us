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

import org.codeartisans.qidelicious.remote.RemoteDelicious;
import org.codeartisans.qidelicious.remote.RemoteDeliciousService;
import org.codeartisans.qidelicious.sync.SyncService;
import org.junit.Before;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public abstract class AbstractQiDeliciousTest
        extends AbstractQi4jTest
{

    protected RemoteDelicious remoteDelicious;
    protected SyncService syncService;

    public void assemble(ModuleAssembly module) throws AssemblyException
    {
        new QiDeliciousAssembler().assemble(module);
    }

    @Before
    public void before()
    {
        ServiceReference<RemoteDelicious> remoteRef = serviceLocator.findService(RemoteDelicious.class);
        remoteDelicious = remoteRef.get();
        ServiceReference<SyncService> syncRef = serviceLocator.findService(SyncService.class);
        syncService = syncRef.get();
    }

    // Qi4j runtime needs inner on assembly mixins to be public and static
    static abstract class AssemblyTimeInnerMixinExample_NotUsedAnymore
            implements SyncService
    {

        public void updateWorkingCopy()
        {
        }

    }

}
