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
package org.codeartisans.jizmo.presentation.http;

import org.codeartisans.jizmo.presentation.http.resources.LogoutResource;
import org.codeartisans.jizmo.presentation.http.resources.TagResource;
import org.codeartisans.jizmo.presentation.http.resources.TagThingsResource;
import org.codeartisans.jizmo.presentation.http.resources.TagsResource;
import org.codeartisans.jizmo.presentation.http.resources.ThingResource;
import org.codeartisans.jizmo.presentation.http.resources.ThingsResource;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class ResourcesAssembler
        implements Assembler
{

    @Override
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.addTransients( LogoutResource.class,
                              TagsResource.class,
                              TagResource.class,
                              TagThingsResource.class,
                              ThingsResource.class,
                              ThingResource.class );
    }

}
