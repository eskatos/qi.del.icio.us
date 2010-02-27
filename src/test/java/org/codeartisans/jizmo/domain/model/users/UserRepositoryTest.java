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

import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.qi4j.library.shiro.domain.Role;
import org.qi4j.library.shiro.domain.RoleAssignment;
import org.qi4j.library.shiro.domain.ShiroDomainAssembler;
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
        new ShiroDomainAssembler().assemble( module );
        module.addEntities( UserEntity.class );
        module.addServices( UserRepository.class,
                            UserFactory.class );
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

        ServiceReference<UserFactory> userFactoryRef = serviceLocator.findService( UserFactory.class );
        UserFactory userFactory = userFactoryRef.get();

        UserEntity eskatos = userFactory.create( "eskatos", "eskatos@n0pe.org", "secret".toCharArray() );
        UserEntity tigrou = userFactory.create( "tigrou", "tigrou@n0pe.org", "secret".toCharArray() );

        EntityBuilder<Role> roleBuilder = uow.newEntityBuilder( Role.class );
        Role adminRole = roleBuilder.instance();
        adminRole.name().set( "admin" );
        adminRole = roleBuilder.newInstance();

        roleBuilder = uow.newEntityBuilder( Role.class );
        Role contribRole = roleBuilder.instance();
        contribRole.name().set( "contributor" );
        contribRole = roleBuilder.newInstance();

        EntityBuilder<RoleAssignment> roleAssignmentBuilder = uow.newEntityBuilder( RoleAssignment.class );
        RoleAssignment eskatosAdminRole = roleAssignmentBuilder.instance();
        eskatosAdminRole.assignee().set( eskatos );
        eskatosAdminRole.role().set( adminRole );
        eskatos.roleAssignments().add( eskatosAdminRole );
        eskatosAdminRole = roleAssignmentBuilder.newInstance();

        roleAssignmentBuilder = uow.newEntityBuilder( RoleAssignment.class );
        RoleAssignment eskatosContribRole = roleAssignmentBuilder.instance();
        eskatosContribRole.assignee().set( eskatos );
        eskatosContribRole.role().set( contribRole );
        eskatos.roleAssignments().add( eskatosContribRole );
        eskatosContribRole = roleAssignmentBuilder.newInstance();

        roleAssignmentBuilder = uow.newEntityBuilder( RoleAssignment.class );
        RoleAssignment rabbitContribRole = roleAssignmentBuilder.instance();
        rabbitContribRole.assignee().set( tigrou );
        rabbitContribRole.role().set( contribRole );
        tigrou.roleAssignments().add( rabbitContribRole );
        rabbitContribRole = roleAssignmentBuilder.newInstance();

        uow.complete();

        uow = unitOfWorkFactory.newUnitOfWork();

        ServiceReference<UserRepository> userReposRef = serviceLocator.findService( UserRepository.class );
        UserRepository userRepos = userReposRef.get();

        eskatos = uow.get( eskatos );
        tigrou = uow.get( tigrou );

        System.out.println( "=============================================================================" );
        for ( RoleAssignment eachRoleAssignment : userRepos.findRoleAssignmentsByUser( eskatos ) ) {
            System.out.println( "eskatos has role: " + eachRoleAssignment.role().get().name().get() );
        }
        for ( RoleAssignment eachRoleAssignment : userRepos.findRoleAssignmentsByUser( tigrou ) ) {
            System.out.println( "rabbit has role: " + eachRoleAssignment.role().get().name().get() );
        }
        System.out.println( "=============================================================================" );

        uow.complete();
    }

}
