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

import java.net.URI;
import java.util.Map;
import org.codeartisans.blob.domain.entities.TagEntity;
import org.codeartisans.blob.domain.entities.ThingEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins( ResourceSerializer.Mixin.class )
public interface ResourceSerializer
        extends ServiceComposite
{

    JSONObject tagAsJson( TagEntity tag, Map<String, URI> uri )
            throws JSONException;

    JSONArray tagsAsJson( Iterable<TagEntity> tags, ResourceURIsBuilder<TagEntity> tagURIBuilder )
            throws JSONException;

    JSONObject thingAsJson( ThingEntity thing, Map<String, URI> uris )
            throws JSONException;

    JSONArray thingsAsJson( Iterable<ThingEntity> things, ResourceURIsBuilder<ThingEntity> thingURIBuilder )
            throws JSONException;

    abstract class Mixin
            implements ResourceSerializer
    {

        private static final Logger LOGGER = LoggerFactory.getLogger( ResourceSerializer.Mixin.class );

        public JSONObject tagAsJson( TagEntity tag, Map<String, URI> uris )
                throws JSONException
        {
            JSONObject json = new JSONObject();
            json.put( "name", tag.name().get() );
            json.put( "count", tag.count().get() );
            for ( Map.Entry<String, URI> eachUri : uris.entrySet() ) {
                json.put( eachUri.getKey(), eachUri.getValue().toASCIIString() );
            }
            return json;
        }

        public JSONArray tagsAsJson( Iterable<TagEntity> tags, ResourceURIsBuilder<TagEntity> tagURIBuilder )
                throws JSONException
        {
            JSONArray json = new JSONArray();
            for ( TagEntity tag : tags ) {
                json.put( tagAsJson( tag, tagURIBuilder.buildURIs( tag ) ) );
            }
            return json;
        }

        public JSONObject thingAsJson( ThingEntity thing, Map<String, URI> uris )
                throws JSONException
        {
            JSONObject json = new JSONObject();
            json.put( "name", thing.name().get() );
            json.put( "description", thing.description().get() );
            for ( Map.Entry<String, URI> eachUri : uris.entrySet() ) {
                json.put( eachUri.getKey(), eachUri.getValue().toASCIIString() );
            }
            LOGGER.debug( "thingAsJson: " + json.toString() );
            return json;
        }

        public JSONArray thingsAsJson( Iterable<ThingEntity> things, ResourceURIsBuilder<ThingEntity> thingURIBuilder )
                throws JSONException
        {
            JSONArray json = new JSONArray();
            for ( ThingEntity thing : things ) {
                json.put( thingAsJson( thing, thingURIBuilder.buildURIs( thing ) ) );
            }
            LOGGER.debug( "thingsAsJson: " + json.toString() );
            return json;
        }

    }

}
