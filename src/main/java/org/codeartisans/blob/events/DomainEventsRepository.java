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
package org.codeartisans.blob.events;

import org.codeartisans.java.toolbox.CollectionUtils;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryBuilderFactory;
import static org.qi4j.api.query.QueryExpressions.*;
import org.qi4j.api.query.grammar.OrderBy.Order;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.library.constraints.annotation.NotEmpty;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( DomainEventsRepository.Mixin.class )
public interface DomainEventsRepository
        extends ServiceComposite
{

    DomainEvent findLastRecordedEvent();

    Query<DomainEvent> findAll();

    Query<DomainEvent> findWithNumberGreaterThan( @NotEmpty Long eventNumber );

    abstract class Mixin
            implements DomainEventsRepository
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Structure
        private QueryBuilderFactory qbf;

        @Override
        public DomainEvent findLastRecordedEvent()
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<DomainEvent> queryBuilder = qbf.newQueryBuilder( DomainEvent.class );
            DomainEvent template = templateFor( DomainEvent.class );
            Query<DomainEvent> query = queryBuilder.newQuery( uow ).orderBy( orderBy( template.eventNumber(), Order.DESCENDING ) ).maxResults( 1 );
            return CollectionUtils.firstElementOrNull( query );
        }

        @Override
        public Query<DomainEvent> findAll()
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<DomainEvent> queryBuilder = qbf.newQueryBuilder( DomainEvent.class );
            DomainEvent template = templateFor( DomainEvent.class );
            return queryBuilder.newQuery( uow ).orderBy( orderBy( template.eventNumber(), Order.ASCENDING ) );
        }

        @Override
        public Query<DomainEvent> findWithNumberGreaterThan( Long eventNumber )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<DomainEvent> queryBuilder = qbf.newQueryBuilder( DomainEvent.class );
            DomainEvent template = templateFor( DomainEvent.class );
            queryBuilder = queryBuilder.where( gt( template.eventNumber(), eventNumber ) );
            return queryBuilder.newQuery( uow ).orderBy( orderBy( template.eventNumber(), Order.ASCENDING ) );
        }

    }

}
