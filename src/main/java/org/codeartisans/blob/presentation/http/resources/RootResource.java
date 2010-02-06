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
package org.codeartisans.blob.presentation.http.resources;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.codeartisans.blob.CoolBlobStructure;
import org.codeartisans.blob.presentation.http.Constants.Qi4jContext;
import org.qi4j.api.composite.TransientBuilderFactory;
import org.qi4j.api.structure.Application;
import org.qi4j.api.structure.Module;

/**
 * TODO Add @UnitOfWorkConcern and @UnitOfWorkPropagation to child resources, see example in qi4j-auth
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
@Path( "/" )
public class RootResource
{

    @Context
    private ServletContext context;
    @Context
    private UriInfo uriInfo;
    private TransientBuilderFactory httpTransientBuilderFactory;

    @PostConstruct
    public void postConstruct()
    {
        Application application = ( Application ) context.getAttribute( Qi4jContext.APPLICATION );
        Module httpModule = application.findModule( CoolBlobStructure.Layers.PRESENTATION, CoolBlobStructure.PresentationModules.HTTP );
        httpTransientBuilderFactory = httpModule.transientBuilderFactory();
    }

    @GET
    @Produces( "text/html" )
    public String getApiDoc()
    {
        return "api doc";
    }

    @Path( "/tags/" )
    public TagsResource tags()
    {
        TagsResource tags = httpTransientBuilderFactory.newTransient( TagsResource.class );
        tags.withUriInfo( uriInfo );
        return tags;
    }

    @Path( "/things/" )
    public ThingsResource things()
    {
        ThingsResource tags = httpTransientBuilderFactory.newTransient( ThingsResource.class );
        tags.withUriInfo( uriInfo );
        return tags;
    }

    // QUID Do not work with HTTP Basic Authentication
    @Path( "/logout/" )
    public LogoutResource logout()
    {
        return httpTransientBuilderFactory.newTransient( LogoutResource.class );
    }

}
