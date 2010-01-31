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

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codeartisans.blob.domain.entities.TagEntity;
import org.codeartisans.blob.domain.entities.TagRepository;
import org.codeartisans.blob.domain.entities.ThingEntity;
import org.codeartisans.blob.domain.entities.ThingRepository;
import org.codeartisans.blob.presentation.http.AbstractQi4jResource;
import org.codeartisans.blob.presentation.http.ResourceSerializer;
import org.codeartisans.blob.presentation.http.ResourceURIsBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class TagThingsResource
        extends AbstractQi4jResource<TagThingsResource>
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TagThingsResource.class );
    @Service
    private ThingRepository thingRepos;
    @Service
    private ResourceSerializer serializer;
    private String name;

    public TagThingsResource withName( String name )
    {
        this.name = name;
        return this;
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response tagThings()
    {
        try {
            UnitOfWork uow = uowf.newUnitOfWork();
            JSONArray jsonArray = serializer.thingsAsJson( thingRepos.findByTag( name ), new ResourceURIsBuilder<ThingEntity>()
            {

                public Map<String, URI> buildURIs( ThingEntity resource )
                {
                    Map<String, URI> uris = new LinkedHashMap<String, URI>();
                    uris.put( "uri", uriInfo.getBaseUriBuilder().path( "things" ).path( resource.identity().get() ).build() );
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
