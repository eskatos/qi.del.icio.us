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
package org.codeartisans.jizmo;

import org.codeartisans.jizmo.domain.model.things.TagEntity;
import org.codeartisans.jizmo.domain.model.things.ThingEntity;
import org.codeartisans.java.toolbox.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryExpressions;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class BlobTest
        extends AbstractQi4jTest
{

    @Override
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        new BlobAssembler().assemble( module );
        module.addObjects( FixtureBuilder.class );
    }

    @Test
    public void givenThingWithTagsWhenQueriedByIdentityNameOrTagThenIsFound()
            throws UnitOfWorkCompletionException
    {
        ThingEntity thing;
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            EntityBuilder<TagEntity> tagBuilder = uow.newEntityBuilder( TagEntity.class );
            EntityBuilder<ThingEntity> thingBuilder = uow.newEntityBuilder( ThingEntity.class );

            TagEntity tagProto = tagBuilder.instance();
            tagProto.name().set( "qi4j" );

            TagEntity tag = tagBuilder.newInstance();

            ThingEntity thingProto = thingBuilder.instance();
            thingProto.name().set( "Blob" );
            thingProto.description().set( "A short blob" );
            thingProto.tags().add( tag );

            thing = thingBuilder.newInstance();

            System.out.println( "================================================" );
            System.out.println( "tag: " + tag.toString() );
            System.out.println( "  is named: " + tag.name().get() );
            System.out.println( "" );
            System.out.println( "thing: " + thing.toString() );
            System.out.println( "  is named: " + thing.name().get() );
            System.out.println( "  has shortdesc: " + thing.description().get() );
            System.out.println( "  has tags: " + thing.tags().toSet() );
            System.out.println( "================================================" );

            uow.complete();
        }

        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            ThingEntity fetchedThing = uow.get( thing );

            checkThing( "fetchedByIdentity", fetchedThing );

            uow.complete();
        }

        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            QueryBuilder<ThingEntity> queryBuilder = queryBuilderFactory.newQueryBuilder( ThingEntity.class );
            ThingEntity thingTemplate = QueryExpressions.templateFor( ThingEntity.class );
            queryBuilder.where( QueryExpressions.eq( thingTemplate.name(), "Blob" ) );

            Query<ThingEntity> query = queryBuilder.newQuery( uow );
            query.maxResults( 1 );

            ThingEntity thingFoundByName = CollectionUtils.firstElementOrNull( query );

            checkThing( "foundByName", thingFoundByName );

            uow.complete();
        }

        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            QueryBuilder<ThingEntity> queryBuilder = queryBuilderFactory.newQueryBuilder( ThingEntity.class );
            ThingEntity thingTemplate = QueryExpressions.templateFor( ThingEntity.class );
            TagEntity tagTemplate = QueryExpressions.templateFor( TagEntity.class );
            queryBuilder.where( QueryExpressions.and(
                    QueryExpressions.eq( tagTemplate.name(), "qi4j" ),
                    QueryExpressions.contains( thingTemplate.tags(), tagTemplate ) ) );

            Query<ThingEntity> query = queryBuilder.newQuery( uow );
            query.maxResults( 1 );

            ThingEntity thingFoundByTag = CollectionUtils.firstElementOrNull( query );

            checkThing( "foundByTag", thingFoundByTag );

            uow.complete();
        }
    }

    private void checkThing( String name, ThingEntity thing )
    {
        System.out.println( "================================================" );
        System.out.println( name + ": " + thing.toString() );
        System.out.println( "  is named: " + thing.name().get() );
        System.out.println( "  has shortdesc: " + thing.description().get() );
        System.out.println( "  has tag: " + thing.tags().get( 0 ).name().get() );
        System.out.println( "================================================" );

        Assert.assertEquals( "Blob", thing.name().get() );
        Assert.assertEquals( "A short blob", thing.description().get() );
        Assert.assertEquals( "qi4j", thing.tags().get( 0 ).name().get() );
    }

}
