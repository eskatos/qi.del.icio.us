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
package org.codeartisans.blob;

import static org.codeartisans.blob.PlaceholderTextBuilder.TextLanguage.*;
import static org.codeartisans.blob.PlaceholderTextBuilder.TextUnit.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import org.codeartisans.blob.events.DomainEventsFactory;
import org.codeartisans.blob.events.ThingCreatedEvent;
import org.codeartisans.java.toolbox.io.IterableBufferedReader;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class FixtureBuilder
{

    interface FixtureInvariants
    {

        static String[] thingsNames = new String[]{ "foo", "bar" };
        static String[] tagsNames = new String[]{ "qi4j cop ddd" };
    }

    public class FixtureSettings
    {

        private Integer thingsNumber = 10;
        private Integer tagsNumber = 10;

        private FixtureSettings()
        {
        }

        void thingsNumber( Integer count )
        {
            thingsNumber = count;
        }

        void tagsNumber( Integer count )
        {
            tagsNumber = count;
        }

    }

    public class Fixtures
    {
    }

    private static ArrayList<String> candidateTags;
    @Structure
    private UnitOfWorkFactory uowf;
    @Service
    private DomainEventsFactory eventsFactory;
    private FixtureSettings settings = new FixtureSettings();

    public FixtureBuilder()
    {
    }

    public FixtureSettings settingsPrototype()
    {
        return settings;
    }

    public Fixtures populateEventsStore()
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = uowf.newUnitOfWork();

        int candidatesCount = ensureCandidateTags().size();
        if ( settings.tagsNumber > candidatesCount ) {
            throw new IllegalArgumentException( "Asked for " + settings.tagsNumber + " tags but there's only " + candidatesCount + " candidates." );
        }

        Random rand = new Random();
        PlaceholderTextBuilder textBuilder = new PlaceholderTextBuilder();

        ArrayList<String> thingsNames = new ArrayList<String>( settings.thingsNumber );
        for ( int i = 0; i < settings.thingsNumber; i++ ) {
            thingsNames.add( textBuilder.build( 5, words ).in( english ).toString() );
        }

        ArrayList<String> tagsNames = new ArrayList<String>( settings.tagsNumber );
        // TODO Use FixturesInvariants
        while ( tagsNames.size() < settings.tagsNumber ) {
            tagsNames.add( candidateTags.get( rand.nextInt( candidatesCount ) ) );
        }

        for ( String eachThingName : thingsNames ) {

            ArrayList<String> eachThingTags = tagsNames; // TODO Randomize tags per thing

            ThingCreatedEvent evt = eventsFactory.newThingCreatedEvent( eachThingName,
                                                                        textBuilder.build( 15 + rand.nextInt( 10 ), words ).in( english ).toString(),
                                                                        eachThingTags );

            System.out.println( "ThingCreatedEvent::" + evt.identity().get() );
            System.out.println( "\t" + evt.thingIdentity().get() );
            System.out.println( "\t" + evt.name().get() );
            System.out.println( "\t" + evt.shortdesc().get() );
            System.out.println( "\t" + evt.tags() );
            System.out.println( "" );
        }
        uow.complete();
        return null;
    }

    private static ArrayList<String> ensureCandidateTags()
    {
        if ( candidateTags == null ) {
            candidateTags = new ArrayList<String>();
            BufferedReader tagsReader = new BufferedReader( new InputStreamReader( FixtureBuilder.class.getResourceAsStream( "tags.txt" ) ) );
            for ( String eachTag : new IterableBufferedReader( tagsReader ) ) {
                candidateTags.add( eachTag );
            }
        }
        return candidateTags;
    }

}
