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
package org.codeartisans.jizmo.domain.model.things;

import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( ThingFactory.Mixin.class )
public interface ThingFactory
        extends ServiceComposite
{

    ThingEntity newThingInstance( String name, String description, Iterable<String> tags );

    abstract class Mixin
            implements ThingFactory
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Service
        private TagRepository tagRepos;

        @Override
        public ThingEntity newThingInstance( String name, String description, Iterable<String> tags )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            ThingEntity thing = uow.newEntity( ThingEntity.class );
            thing.name().set( name );
            thing.description().set( description );
            if ( tags != null ) {
                for ( String eachTag : tags ) {
                    TagEntity tagEntity = tagRepos.findByName( eachTag );
                    if ( tagEntity == null ) {
                        tagEntity = uow.newEntity( TagEntity.class );
                        tagEntity.name().set( eachTag );
                    }
                    tagEntity.incrementCount();
                    thing.tags().add( tagEntity );
                }
            }
            return thing;
        }

    }

}
