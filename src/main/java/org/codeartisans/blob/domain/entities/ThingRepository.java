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
package org.codeartisans.blob.domain.entities;

import static org.qi4j.api.query.QueryExpressions.*;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryBuilderFactory;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.library.constraints.annotation.NotEmpty;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( ThingRepository.Mixin.class )
public interface ThingRepository
        extends ServiceComposite
{

    Query<ThingEntity> findAll();

    Query<ThingEntity> findByTag( @NotEmpty String tag );

    abstract class Mixin
            implements ThingRepository
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Structure
        private QueryBuilderFactory qbf;

        public Query<ThingEntity> findAll()
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            return queryBuilder.newQuery( uow );
        }

        public Query<ThingEntity> findByTag( String tag )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<ThingEntity> queryBuilder = qbf.newQueryBuilder( ThingEntity.class );
            TagEntity tagTemplate = templateFor( TagEntity.class );
            queryBuilder = queryBuilder.where(
                    and( eq( tagTemplate.name(), tag ),
                         contains( templateFor( ThingEntity.class ).tags(), tagTemplate ) ) );
            return queryBuilder.newQuery( uow );
        }

    }

}
