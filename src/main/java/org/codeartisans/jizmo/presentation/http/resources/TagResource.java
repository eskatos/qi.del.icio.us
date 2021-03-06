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
package org.codeartisans.jizmo.presentation.http.resources;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codeartisans.jizmo.domain.model.things.TagEntity;
import org.codeartisans.jizmo.domain.model.things.TagRepository;
import org.codeartisans.jizmo.presentation.http.AbstractQi4jResource;
import org.codeartisans.jizmo.presentation.http.Qi4jResource;
import org.codeartisans.jizmo.presentation.http.ResourceSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.qi4j.api.composite.TransientComposite;
import org.qi4j.api.concern.Concerns;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.library.shiro.concerns.RequiresRoles;
import org.qi4j.library.shiro.concerns.SecurityConcern;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( TagResource.Mixin.class )
@Concerns( SecurityConcern.class )
public interface TagResource
        extends Qi4jResource, TransientComposite
{

    void withName( String name );

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @RequiresRoles( "admin" )
    Response tag();

    @POST
    @Consumes( MediaType.TEXT_PLAIN )
    Response renameTag( String newName );

    abstract class Mixin
            extends AbstractQi4jResource
            implements TagResource
    {

        @Service
        private TagRepository tagRepos;
        @Service
        private ResourceSerializer serializer;
        private String name;

        @Override
        public void withName( String name )
        {
            this.name = name;
        }

        @Override
        public Response tag()
        {
            try {
                UnitOfWork uow = uowf.newUnitOfWork();
                TagEntity tag = tagRepos.findByName( name );
                if ( tag == null ) {
                    return Response.status( Response.Status.NOT_FOUND ).build();
                }
                Map<String, URI> uris = new LinkedHashMap<String, URI>();
                uris.put( "uri", uriInfo.getRequestUri() );
                uris.put( "things-uri", uriInfo.getRequestUriBuilder().path( "things" ).build() );
                JSONObject jsonTag = serializer.tagAsJson( tag, uris );
                uow.discard();
                return Response.ok().type( MediaType.APPLICATION_JSON ).entity( jsonTag.toString( 2 ) ).build();
            } catch ( JSONException ex ) {
                throw new RuntimeException( ex );
            }
        }

        // TODO Set request param name and change accordingly for URI building
        // TODO Use DomainEvents !
        // TODO Sanitize input ! (DomainEvent responsibility)
        @Override
        public Response renameTag( String newName )
        {
            try {
                UnitOfWork uow = uowf.newUnitOfWork();
                TagEntity tag = tagRepos.findByName( name );
                tag.name().set( newName );
                uow.complete();
                URI uri = uriInfo.getRequestUriBuilder().replaceMatrixParam( "rename", newName ).build();
                return Response.seeOther( uri ).build();
            } catch ( UnitOfWorkCompletionException ex ) {
                throw new RuntimeException( ex );
            }
        }

    }

}
