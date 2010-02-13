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
package org.qi4j.tests;

import static org.junit.Assert.*;
import org.junit.Test;
import org.qi4j.api.entity.Aggregated;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.association.Association;
import org.qi4j.api.entity.association.ManyAssociation;
import org.qi4j.api.property.Property;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

public class DeeplyAggregatedTest
        extends AbstractQi4jTest
{

    @Override
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.addEntities( GroupEntity.class, CompanyEntity.class, EmployeeEntity.class, PersonEntity.class );

        module.addServices( MemoryEntityStoreService.class, UuidIdentityGeneratorService.class );

        module.addObjects( getClass() );
    }

    @Test
    public void givenTwoLevelAggregatedEntitiesWhenAggregateRootIsDeletedThenDeleteTwoLevelAggregatedEntities()
            throws Exception
    {
        GroupEntity groupEntity;
        CompanyEntity firstCompany, secondCompany;
        PersonEntity personEntity1, personEntity2;
        EmployeeEntity employeeEntity, employeeEntity2, employeeEntity3, employeeEntity4;
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                {
                    EntityBuilder<PersonEntity> builder = unitOfWork.newEntityBuilder( PersonEntity.class );
                    personEntity1 = builder.instance();
                    personEntity1.name().set( "John" );
                    personEntity1 = builder.newInstance();
                }

                {
                    EntityBuilder<PersonEntity> builder = unitOfWork.newEntityBuilder( PersonEntity.class );
                    personEntity2 = builder.instance();
                    personEntity2.name().set( "Bob" );
                    builder.newInstance();
                }

                {
                    EntityBuilder<EmployeeEntity> builder = unitOfWork.newEntityBuilder( EmployeeEntity.class );
                    employeeEntity = builder.instance();
                    employeeEntity.person().set( personEntity1 );
                    employeeEntity.salary().set( 50000 );
                    employeeEntity.title().set( "Director" );
                    employeeEntity = builder.newInstance();
                }

                {
                    EntityBuilder<EmployeeEntity> builder = unitOfWork.newEntityBuilder( EmployeeEntity.class );
                    employeeEntity2 = builder.instance();
                    employeeEntity2.person().set( personEntity2 );
                    employeeEntity2.salary().set( 40000 );
                    employeeEntity2.title().set( "Developer" );
                    employeeEntity2 = builder.newInstance();
                }

                {
                    EntityBuilder<EmployeeEntity> builder = unitOfWork.newEntityBuilder( EmployeeEntity.class );
                    employeeEntity3 = builder.instance();
                    employeeEntity3.person().set( personEntity2 );
                    employeeEntity3.salary().set( 50000 );
                    employeeEntity3.title().set( "Director" );
                    employeeEntity3 = builder.newInstance();
                }

                {
                    EntityBuilder<EmployeeEntity> builder = unitOfWork.newEntityBuilder( EmployeeEntity.class );
                    employeeEntity4 = builder.instance();
                    employeeEntity4.person().set( personEntity1 );
                    employeeEntity4.salary().set( 40000 );
                    employeeEntity4.title().set( "Developer" );
                    employeeEntity4 = builder.newInstance();
                }

                {
                    EntityBuilder<CompanyEntity> builder = unitOfWork.newEntityBuilder( CompanyEntity.class );
                    firstCompany = builder.instance();
                    firstCompany.director().set( employeeEntity );
                    firstCompany.employees().add( 0, employeeEntity );
                    firstCompany.employees().add( 0, employeeEntity2 );
                    firstCompany = builder.newInstance();
                }

                {
                    EntityBuilder<CompanyEntity> builder = unitOfWork.newEntityBuilder( CompanyEntity.class );
                    secondCompany = builder.instance();
                    secondCompany.director().set( employeeEntity3 );
                    secondCompany.employees().add( 0, employeeEntity3 );
                    secondCompany.employees().add( 0, employeeEntity4 );
                    secondCompany = builder.newInstance();
                }

                {
                    EntityBuilder<GroupEntity> builder = unitOfWork.newEntityBuilder( GroupEntity.class );
                    groupEntity = builder.instance();
                    groupEntity.companies().add( 0, firstCompany );
                    groupEntity.companies().add( 0, secondCompany );
                    groupEntity = builder.newInstance();
                }

                System.out.println( "EntitiesIdentities::" );
                System.out.println( "    groupEntity:     " + groupEntity.identity().get() );
                System.out.println( "    firstCompany:    " + firstCompany.identity().get() );
                System.out.println( "    secondCompany:   " + secondCompany.identity().get() );
                System.out.println( "    employeeEntity:  " + employeeEntity.identity().get() );
                System.out.println( "    employeeEntity2: " + employeeEntity2.identity().get() );
                System.out.println( "    employeeEntity3: " + employeeEntity3.identity().get() );
                System.out.println( "    employeeEntity4: " + employeeEntity4.identity().get() );
                System.out.println( "    personEntity1:   " + personEntity1.identity().get() );
                System.out.println( "    personEntity2:   " + personEntity2.identity().get() );

                unitOfWork.complete();
            } catch ( Exception e ) {
                unitOfWork.discard();
                throw e;
            }
        }

        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {

                groupEntity = unitOfWork.get( groupEntity );
                firstCompany = unitOfWork.get( firstCompany );
                groupEntity.companies().remove( firstCompany );
                unitOfWork.remove( firstCompany );

                unitOfWork.complete();
            } catch ( Exception e ) {
                unitOfWork.discard();
                throw e;
            }
        }

        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();

            unitOfWork.get( personEntity1 );
            unitOfWork.get( personEntity2 );

            unitOfWork.complete();
        }

        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                unitOfWork.get( employeeEntity );

                fail( "Should not work" );

                unitOfWork.complete();
            } catch ( NoSuchEntityException e ) {
                unitOfWork.discard();
            }
        }

        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                unitOfWork.get( employeeEntity2 );
                fail( "Should not work" );

                unitOfWork.complete();
            } catch ( NoSuchEntityException e ) {
                unitOfWork.discard();
            }
        }
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                unitOfWork.get( firstCompany );
                fail( "Should not work" );

                unitOfWork.complete();
            } catch ( NoSuchEntityException e ) {
                unitOfWork.discard();
            }
        }
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            unitOfWork.get( secondCompany );
            unitOfWork.complete();
        }

        GroupEntity groupEntity2;
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                EntityBuilder<GroupEntity> builder = unitOfWork.newEntityBuilder( GroupEntity.class );
                groupEntity2 = builder.instance();
                groupEntity2.companies().add( 0, secondCompany );
                groupEntity2 = builder.newInstance();
                unitOfWork.complete();
            } catch ( Exception e ) {
                unitOfWork.discard();
                throw e;
            }
        }
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                groupEntity = unitOfWork.get( groupEntity );
                groupEntity2 = unitOfWork.get( groupEntity2 );

                assertEquals( groupEntity.companies().get( 0 ), groupEntity2.companies().get( 0 ) );

                unitOfWork.complete();
            } catch ( Exception e ) {
                unitOfWork.discard();
                throw e;
            }
        }
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                groupEntity = unitOfWork.get( groupEntity );
                unitOfWork.remove( groupEntity );

                unitOfWork.complete();
            } catch ( Exception e ) {
                unitOfWork.discard();
                throw e;
            }
        }
        {
            UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
            try {
                unitOfWork.get( secondCompany );
                fail( "Should not work" );

                unitOfWork.complete();
            } catch ( NoSuchEntityException e ) {
                unitOfWork.discard();
            }
        }
    }

    public interface GroupEntity
            extends EntityComposite
    {

        @Aggregated
        ManyAssociation<CompanyEntity> companies();

    }

    public interface CompanyEntity
            extends EntityComposite
    {

        @Aggregated
        Association<EmployeeEntity> director();

        @Aggregated
        ManyAssociation<EmployeeEntity> employees();

    }

    public interface EmployeeEntity
            extends EntityComposite
    {

        Property<String> title();

        Property<Integer> salary();

        Association<PersonEntity> person();

    }

    public interface PersonEntity
            extends EntityComposite
    {

        Property<String> name();

    }

}
