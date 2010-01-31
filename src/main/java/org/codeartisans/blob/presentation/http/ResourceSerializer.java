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
import org.codeartisans.blob.domain.entities.TagEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
@Mixins( ResourceSerializer.Mixin.class )
public interface ResourceSerializer
        extends ServiceComposite
{

    JSONObject representJson( TagEntity tag, URI uri )
            throws JSONException;

    JSONArray representJson( Iterable<TagEntity> tags, ResourceURIBuilder<TagEntity> tagURIBuilder )
            throws JSONException;

    abstract class Mixin
            implements ResourceSerializer
    {

        public JSONObject representJson( TagEntity tag, URI uri )
                throws JSONException
        {
            JSONObject jsonTag = new JSONObject();
            jsonTag.put( "name", tag.name().get() );
            jsonTag.put( "count", tag.count().get() );
            jsonTag.put( "uri", uri.toASCIIString() );
            return jsonTag;
        }

        public JSONArray representJson( Iterable<TagEntity> tags, ResourceURIBuilder<TagEntity> tagURIBuilder )
                throws JSONException
        {
            JSONArray json = new JSONArray();
            for ( TagEntity tag : tags ) {
                json.put( representJson( tag, tagURIBuilder.buildURI( tag ) ) );
            }
            return json;
        }

    }

}
