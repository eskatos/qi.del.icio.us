/*
 * Copyright (c) 2010 Paul Merlin <paul@nosphere.org>
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codeartisans.jizmo.domain.model.things.ThingEntity;
import org.codeartisans.jizmo.domain.model.things.ThingRepository;
import org.codeartisans.jizmo.presentation.http.AbstractQi4jResource;
import org.codeartisans.jizmo.presentation.http.Qi4jResource;
import org.codeartisans.jizmo.presentation.http.ResourceSerializer;
import org.codeartisans.jizmo.presentation.http.ResourceURIsBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.qi4j.api.composite.TransientComposite;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.unitofwork.UnitOfWork;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( ThingsResource.Mixin.class )
public interface ThingsResource
        extends Qi4jResource, TransientComposite
{

    @Path( "{identity}/" )
    ThingResource thing( @PathParam( "identity" ) String identity );

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    Response things();

    abstract class Mixin
            extends AbstractQi4jResource
            implements ThingsResource
    {

        @Service
        private ThingRepository thingRepos;
        @Service
        private ResourceSerializer serializer;

        @Override
        public ThingResource thing( String identity )
        {
            ThingResource thing = tbf.newTransient( ThingResource.class );
            thing.withUriInfo( uriInfo );
            thing.withIdentity( identity );
            return thing;
        }

        @Override
        public Response things()
        {
            try {
                UnitOfWork uow = uowf.newUnitOfWork();
                JSONArray jsonArray = serializer.thingsAsJson( thingRepos.findAll(), new ResourceURIsBuilder<ThingEntity>()
                {

                    @Override
                    public Map<String, URI> buildURIs( ThingEntity resource )
                    {
                        Map<String, URI> uris = new LinkedHashMap<String, URI>();
                        uris.put( "uri", uriInfo.getAbsolutePathBuilder().path( resource.identity().get() ).build() );
                        return uris;
                    }

                } );
                uow.discard();
                return Response.ok().type( MediaType.APPLICATION_JSON ).entity( jsonArray.toString( 2 ) ).build();
            } catch ( JSONException ex ) {
                throw new RuntimeException( ex );
            }
        }

    }

}
