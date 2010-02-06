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
package org.codeartisans.blob.presentation.http;

import org.codeartisans.blob.CoolBlobAssembler;
import org.qi4j.api.structure.Application;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Here we can set arbitrary Objects as attributes on the ServletContext so they are bound to the webapp lifecycle.
 *
 * Servlet specification states:
 *
 *      In cases where the container is distributed over many virtual machines, a Web application will have an
 *      instance of the ServletContext for each JVM.
 *
 *      Context attributes are local to the JVM in which they were created. This prevents ServletContext attributes
 *      from being a shared memory store in a distributed container. When information needs to be shared between
 *      servlets running in a distributed environment, the information should be placed into a session, stored in a
 *      database, or set in an Enterprise JavaBeans component.
 *
 * @author Paul Merlin <paul@nosphere.org>
 */
public class HttpLifecycleListener
        extends AbstractQi4jServletBootstrap
{

    private static final Logger LOGGER = LoggerFactory.getLogger( HttpLifecycleListener.class );

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory applicationFactory )
            throws AssemblyException
    {
        ApplicationAssembly assembly = new CoolBlobAssembler().assemble( applicationFactory );
        assembly.setMode( Application.Mode.test );
        return assembly;
    }

    @Override
    protected void beforeApplicationActivation( Application app )
            throws Exception
    {
        LOGGER.info( "CoolBlob Application " + application.version() + " successfully assembled" );
    }

    @Override
    protected void afterApplicationActivation( Application application )
            throws Exception
    {
        LOGGER.info( "CoolBlob Application " + application.version() + " successfully activated" );
    }

}
