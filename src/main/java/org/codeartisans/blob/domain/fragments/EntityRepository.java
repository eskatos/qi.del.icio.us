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
package org.codeartisans.blob.domain.fragments;

import java.lang.reflect.ParameterizedType;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryBuilderFactory;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( EntityRepository.Mixin.class )
public interface EntityRepository<T extends EntityComposite>
{

    T findByIdentity( String identity );

    Query<T> findAll();

    abstract class Mixin<T extends EntityComposite>
            implements EntityRepository<T>
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Structure
        private QueryBuilderFactory qbf;

        public Query<T> findAll()
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            Class specializedClass = ( Class<T> ) ( ( ParameterizedType ) this.getClass().getGenericSuperclass() ).getActualTypeArguments()[0];
            QueryBuilder<T> queryBuilder = qbf.newQueryBuilder( specializedClass );
            return queryBuilder.newQuery( uow );
        }

    }

}
