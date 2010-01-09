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
package org.codeartisans.blob.domain;

import org.codeartisans.blob.CoolBlobStructure;
import org.codeartisans.blob.domain.entities.RootEntity;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.Activatable;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins(RootEntityService.Mixin.class)
public interface RootEntityService
        extends Activatable, ServiceComposite
{

    abstract class Mixin
            implements RootEntityService
    {

        @Structure
        private UnitOfWorkFactory uowf;

        public void activate()
                throws Exception
        {
            UnitOfWork uow = uowf.newUnitOfWork();
            try {
                RootEntity root = uow.get(RootEntity.class, CoolBlobStructure.ROOT_ENTITY_IDENTITY);
                System.out.println("RootEntityService::activate, will use RootEntity: " + root.identity().get());
            } catch (NoSuchEntityException ex) {
                EntityBuilder<RootEntity> builder = uow.newEntityBuilder(RootEntity.class, CoolBlobStructure.ROOT_ENTITY_IDENTITY);
                RootEntity root = builder.newInstance();
                System.out.println("RootEntityService::activate, created new RootEntity: " + root.identity().get());
            }
            uow.complete();
        }

        public void passivate()
                throws Exception
        {
            UnitOfWork uow = uowf.newUnitOfWork();
            try {
                RootEntity root = uow.get(RootEntity.class, CoolBlobStructure.ROOT_ENTITY_IDENTITY);
                System.out.println("RootEntityService::passivate, existing RootEntity: " + root.identity().get());
            } catch (NoSuchEntityException ex) {
                System.out.println("RootEntityService::passivate, no RootEntity");
            }
            uow.complete();
        }

    }

}
