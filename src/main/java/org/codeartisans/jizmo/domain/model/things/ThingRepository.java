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
package org.codeartisans.jizmo.domain.model.things;

import java.util.ArrayList;
import java.util.List;
import org.codeartisans.jizmo.domain.model.values.PayloadValue;
import static org.qi4j.api.query.QueryExpressions.*;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryBuilderFactory;
import org.qi4j.api.query.grammar.BooleanExpression;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.library.constraints.annotation.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Test each method!
 * 
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( ThingRepository.Mixin.class )
public interface ThingRepository
        extends ServiceComposite
{

    ThingEntity findByIdentity( String identity );

    Query<ThingEntity> findAll();

    Query<ThingEntity> findByIlk( @NotEmpty String ilk );

    Query<ThingEntity> findByIlks( @NotEmpty String... ilks ); // OR

    Query<ThingEntity> findByTag( @NotEmpty String tag );

    Query<ThingEntity> findByTags( @NotEmpty String... tags ); // AND

    Query<ThingEntity> findByPayloadMimetype( @NotEmpty String mimetype );

    abstract class Mixin
            implements ThingRepository
    {

        private static final Logger LOGGER = LoggerFactory.getLogger( ThingRepository.Mixin.class );
        @Structure
        private UnitOfWorkFactory uowf;
        @Structure
        private QueryBuilderFactory qbf;

        @Override
        public ThingEntity findByIdentity( String identity )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            return uow.get( ThingEntity.class, identity );
        }

        @Override
        public Query<ThingEntity> findAll()
        {
            LOGGER.debug( "findByAll()" );
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            return queryBuilder.newQuery( uow );
        }

        @Override
        public Query<ThingEntity> findByIlk( String ilk )
        {
            LOGGER.debug( "findByIlk(" + ilk + ")" );
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            IlkEntity ilkTemplate = templateFor( IlkEntity.class );
            queryBuilder = queryBuilder.where(
                    and( eq( ilkTemplate.name(), ilk ),
                         eq( templateFor( ThingEntity.class ).ilk(), ilkTemplate ) ) );
            return queryBuilder.newQuery( uow );
        }

        @Override
        public Query<ThingEntity> findByIlks( String... ilks )
        {
            LOGGER.debug( "findByIlks(" + ilks + ")" );
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            ThingEntity thingTemplate = templateFor( ThingEntity.class );
            List<BooleanExpression> bools = new ArrayList<BooleanExpression>();
            for ( String eachIlk : ilks ) {
                IlkEntity ilkTemplate = templateFor( IlkEntity.class );
                bools.add( and( eq( ilkTemplate.name(), eachIlk ),
                                eq( thingTemplate.ilk(), ilkTemplate ) ) );
            }
            BooleanExpression first = bools.remove( 0 );
            BooleanExpression second = bools.remove( 0 );
            queryBuilder = queryBuilder.where( or( first, second, bools.toArray( new BooleanExpression[ bools.size() ] ) ) );
            return queryBuilder.newQuery( uow );
        }

        @Override
        public Query<ThingEntity> findByTag( String tag )
        {
            LOGGER.debug( "findByTag(" + tag + ")" );
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            TagEntity tagTemplate = templateFor( TagEntity.class );
            queryBuilder = queryBuilder.where(
                    and( eq( tagTemplate.name(), tag ),
                         contains( templateFor( ThingEntity.class ).tags(), tagTemplate ) ) );
            return queryBuilder.newQuery( uow );
        }

        @Override
        public Query<ThingEntity> findByTags( String... tags )
        {
            LOGGER.debug( "findByTags(" + tags + ")" );
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            for ( String eachTag : tags ) {
                TagEntity tagTemplate = templateFor( TagEntity.class );
                queryBuilder = queryBuilder.where(
                        and( eq( tagTemplate.name(), eachTag ),
                             contains( templateFor( ThingEntity.class ).tags(), tagTemplate ) ) );
            }
            return queryBuilder.newQuery( uow );
        }

        @Override
        public Query<ThingEntity> findByPayloadMimetype( String mimetype )
        {
            LOGGER.debug( "findByPayloadMimetype(" + mimetype + ")" );
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            PayloadValue payloadTemplate = templateFor( PayloadValue.class );
            queryBuilder = queryBuilder.where(
                    and( eq( payloadTemplate.mimeType(), mimetype ),
                         contains( templateFor( ThingEntity.class ).payloads(), payloadTemplate ) ) );
            return queryBuilder.newQuery( uow );
        }

    }

}
