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
package org.codeartisans.jizmo.domain.model;

import org.codeartisans.jizmo.domain.model.things.RootEntity;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.Activatable;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( RootEntityService.Mixin.class )
public interface RootEntityService
        extends Activatable, ServiceComposite
{

    RootEntity rootEntity();

    abstract class Mixin
            implements RootEntityService
    {

        private static final Logger LOGGER = LoggerFactory.getLogger( RootEntityService.Mixin.class );
        @Structure
        private UnitOfWorkFactory uowf;

        @Override
        public RootEntity rootEntity()
        {
            return uowf.currentUnitOfWork().get( RootEntity.class, RootEntity.IDENTITY );
        }

        @Override
        public void activate()
                throws Exception
        {
            UnitOfWork uow = uowf.newUnitOfWork();
            try {
                RootEntity root = rootEntity();
                LOGGER.info( "Will use RootEntity: " + root.identity().get() );
            } catch ( NoSuchEntityException ex ) {
                EntityBuilder<RootEntity> builder = uow.newEntityBuilder( RootEntity.class, RootEntity.IDENTITY );
                RootEntity root = builder.newInstance();
                LOGGER.info( "Created new RootEntity: " + root.identity().get() );
            }
            uow.complete();
        }

        @Override
        public void passivate()
                throws Exception
        {
            UnitOfWork uow = uowf.newUnitOfWork();
            try {
                RootEntity root = rootEntity();
                LOGGER.info( "Existing RootEntity: " + root.identity().get() );
            } catch ( NoSuchEntityException ex ) {
                LOGGER.info( "No RootEntity" );
            }
            uow.complete();
        }

    }

}
