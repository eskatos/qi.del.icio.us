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
package org.codeartisans.blob.domain.things;

import org.codeartisans.blob.domain.fragments.Name;
import org.qi4j.api.common.Optional;
import org.qi4j.api.common.UseDefaults;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import org.qi4j.library.constraints.annotation.GreaterThan;

/**
 * TODO Taggee, Taggable !?!
 * 
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( TagEntity.Mixin.class )
public interface TagEntity
        extends EntityComposite
{

    @Optional
    Name name();

    @UseDefaults
    @GreaterThan( -1 )
    Property<Integer> count();

    void incrementCount();

    void decrementCount();

    abstract class Mixin
            implements TagEntity
    {

        @This
        private TagEntity state;

        @Override
        public void incrementCount()
        {
            state.count().set( state.count().get() + 1 );
        }

        @Override
        public void decrementCount()
        {
            if ( state.count().get() > 0 ) {
                state.count().set( state.count().get() - 1 );
            }
        }

    }

}