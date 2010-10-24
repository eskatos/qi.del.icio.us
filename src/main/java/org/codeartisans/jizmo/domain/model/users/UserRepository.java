/*
 * Copyright (c) 2010 Paul Merlin <paul@nosphere.org>
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
package org.codeartisans.jizmo.domain.model.users;

import org.codeartisans.java.toolbox.CollectionUtils;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryBuilderFactory;
import static org.qi4j.api.query.QueryExpressions.*;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.library.shiro.domain.permissions.RoleAssignment;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( UserRepository.Mixin.class )
public interface UserRepository
        extends ServiceComposite
{

    UserEntity findByUsername( String username );

    UserEntity findByEmail( String email );

    Query<RoleAssignment> findRoleAssignmentsByUser( UserEntity user );

    abstract class Mixin
            implements UserRepository
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Structure
        private QueryBuilderFactory qbf;

        @Override
        public UserEntity findByUsername( String username )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<UserEntity> queryBuilder = qbf.newQueryBuilder( UserEntity.class );
            UserEntity template = templateFor( UserEntity.class );
            queryBuilder = queryBuilder.where( eq( template.username(), username ) );
            return CollectionUtils.firstElementOrNull( queryBuilder.newQuery( uow ).maxResults( 1 ) );
        }

        @Override
        public UserEntity findByEmail( String email )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<UserEntity> queryBuilder = qbf.newQueryBuilder( UserEntity.class );
            UserEntity template = templateFor( UserEntity.class );
            queryBuilder = queryBuilder.where( eq( template.email(), email ) );
            return CollectionUtils.firstElementOrNull( queryBuilder.newQuery( uow ).maxResults( 1 ) );
        }

        @Override
        public Query<RoleAssignment> findRoleAssignmentsByUser( UserEntity user )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<RoleAssignment> queryBuilder = qbf.newQueryBuilder( RoleAssignment.class );
            RoleAssignment roleAssignmentTemplate = templateFor( RoleAssignment.class );
            queryBuilder = queryBuilder.where( eq( roleAssignmentTemplate.assignee(), user ) );
            return queryBuilder.newQuery( uow );
        }

    }

}
