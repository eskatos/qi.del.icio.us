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
package org.codeartisans.blob.domain.users;

import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class UserRepositoryTest
        extends AbstractQi4jTest
{

    @Override
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.addEntities( UserEntity.class,
                            RoleEntity.class,
                            //RoleAssignee.class,
                            RoleAssignmentEntity.class );
        module.addServices( UserRepository.class );

        module.addServices( MemoryEntityStoreService.class,
                            UuidIdentityGeneratorService.class );
        new RdfMemoryStoreAssembler().assemble( module );


    }

    @Test
    public void test()
            throws UnitOfWorkCompletionException
    {

        UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();
        // TODO test user/roleassignment/role query


        EntityBuilder<UserEntity> userBuilder = uow.newEntityBuilder( UserEntity.class );
        UserEntity eskatos = userBuilder.instance();
        eskatos.nickname().set( "eskatos" );
        eskatos.email().set( "eskatos@n0pe.org" );
        eskatos = userBuilder.newInstance();

        userBuilder = uow.newEntityBuilder( UserEntity.class );
        UserEntity rabbit = userBuilder.instance();
        rabbit.nickname().set( "rabbit" );
        rabbit.email().set( "rabbit@n0pe.org" );
        rabbit = userBuilder.newInstance();

        EntityBuilder<RoleEntity> roleBuilder = uow.newEntityBuilder( RoleEntity.class );
        RoleEntity adminRole = roleBuilder.instance();
        adminRole.name().set( "admin" );
        adminRole = roleBuilder.newInstance();

        roleBuilder = uow.newEntityBuilder( RoleEntity.class );
        RoleEntity contribRole = roleBuilder.instance();
        contribRole.name().set( "contributor" );
        contribRole = roleBuilder.newInstance();

        EntityBuilder<RoleAssignmentEntity> roleAssignmentBuilder = uow.newEntityBuilder( RoleAssignmentEntity.class );
        RoleAssignmentEntity eskatosAdminRole = roleAssignmentBuilder.instance();
        eskatosAdminRole.assignee().set( eskatos );
        eskatosAdminRole.role().set( adminRole );
        eskatosAdminRole = roleAssignmentBuilder.newInstance();
        // eskatos.roleAssignments().add( eskatosAdminRole );

        roleAssignmentBuilder = uow.newEntityBuilder( RoleAssignmentEntity.class );
        RoleAssignmentEntity eskatosContribRole = roleAssignmentBuilder.instance();
        eskatosContribRole.assignee().set( eskatos );
        eskatosContribRole.role().set( contribRole );
        eskatosContribRole = roleAssignmentBuilder.newInstance();
        // eskatos.roleAssignments().add( eskatosContribRole );

        roleAssignmentBuilder = uow.newEntityBuilder( RoleAssignmentEntity.class );
        RoleAssignmentEntity rabbitContribRole = roleAssignmentBuilder.instance();
        rabbitContribRole.assignee().set( rabbit );
        rabbitContribRole.role().set( contribRole );
        rabbitContribRole = roleAssignmentBuilder.newInstance();
        // rabbit.roleAssignments().add( rabbitContribRole );

        uow.complete();

        uow = unitOfWorkFactory.newUnitOfWork();

        ServiceReference<UserRepository> userReposRef = serviceLocator.findService( UserRepository.class );
        UserRepository userRepos = userReposRef.get();

        eskatos = uow.get( eskatos );
        rabbit = uow.get( rabbit );

        System.out.println( "=============================================================================" );
        for ( RoleAssignmentEntity eachRoleAssignment : userRepos.findByUser( eskatos ) ) {
            System.out.println( "eskatos has role: " + eachRoleAssignment.role().get().name().get() );
        }
        for ( RoleAssignmentEntity eachRoleAssignment : userRepos.findByUser( rabbit ) ) {
            System.out.println( "rabbit has role: " + eachRoleAssignment.role().get().name().get() );
        }
        System.out.println( "=============================================================================" );

        uow.complete();
    }

}
