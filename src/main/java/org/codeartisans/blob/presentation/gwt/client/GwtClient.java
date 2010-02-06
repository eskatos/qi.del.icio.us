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
package org.codeartisans.blob.presentation.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public final class GwtClient
        implements EntryPoint
{

    @Override
    public void onModuleLoad()
    {
        try {
            RootPanel.get().add( new Label( "Wow freaking cool stuff!" ) );
            // List tags
            //String encodedUrl = "http://localhost:8888/api/tags/qi4j";
            String encodedUrl = "http://localhost:8888/api/tags";
            RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, encodedUrl );
            builder.setHeader( "Accept", "application/json" );
            builder.sendRequest( null, new RequestCallback()
            {

                @Override
                public void onResponseReceived( Request request, Response response )
                {
                    RootPanel.get().add( new Label( "RESPONSE: " + response.getText() ) );

//                    Tag tag = TagJSO.fromJSON( response.getText() );
//                    RootPanel.get().add( new Label( "TAG: " + tag.name() ) );

                    JsArray<TagJSO> tags = TagJSO.fromJSONArray( response.getText() );
                    RootPanel.get().add( new Label( "TAGS: " + tags.length() ) );

                }

                @Override
                public void onError( Request request, Throwable ex )
                {
                    RootPanel.get().add( new Label( "FUCK FAILED: " + ex.getMessage() ) );
                }

            } );
        } catch ( RequestException ex ) {
            RootPanel.get().add( new Label( "FUCK FAILED: " + ex.getMessage() ) );
        }
    }

}
