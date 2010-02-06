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
import org.codeartisans.java.toolbox.CollectionUtils;
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
@Mixins( TagRepository.Mixin.class )
public interface TagRepository
        extends ServiceComposite
{

    Query<TagEntity> findAll();

    TagEntity findByName( @NotEmpty String name );

    abstract class Mixin
            implements TagRepository
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Structure
        private QueryBuilderFactory qbf;

        @Override
        public Query<TagEntity> findAll()
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<TagEntity> queryBuilder = qbf.newQueryBuilder( TagEntity.class );
            return queryBuilder.newQuery( uow );
        }

        @Override
        public TagEntity findByName( String name )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<TagEntity> queryBuilder = qbf.newQueryBuilder( TagEntity.class );
            TagEntity template = templateFor( TagEntity.class );
            queryBuilder = queryBuilder.where( eq( template.name(), name ) );
            return CollectionUtils.firstElementOrNull( queryBuilder.newQuery( uow ).maxResults( 1 ) );
        }

    }

}
