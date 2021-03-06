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

import org.codeartisans.jizmo.domain.model.values.PayloadValue;
import org.codeartisans.jizmo.domain.model.fragments.Name;
import org.codeartisans.jizmo.domain.model.fragments.Text;
import org.qi4j.api.common.Optional;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.association.Association;
import org.qi4j.api.entity.association.ManyAssociation;
import org.qi4j.api.mixin.Mixins;

/**
 * The Thing :)
 *
 * It seems Entities extending this type will be AggregateRoots.
 * Relations to aggregated Entities shall be annotated with @Aggregated in the RootEntities.
 *
 * See AggregatedTest in qi4j-runtime
 * See DeeplyAggregatedTest in this project
 * See http://www.jroller.com/niclas/entry/entity_aggregates
 * See http://lists.ops4j.org/pipermail/qi4j-dev/2008-September/003391.html (long thread)
 *
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( ThingEntity.Mixin.class )
public interface ThingEntity
        extends EntityComposite
{

    @Optional
    Name name();

    @Optional
    Text description();

    @Optional
    Association<IlkEntity> ilk();

    ManyAssociation<PayloadValue> payloads();

    ManyAssociation<TagEntity> tags();

    void nameChanged( String name );

    abstract class Mixin
            implements ThingEntity
    {

        @Override
        public void nameChanged( String name )
        {
            name().set( name );
        }

    }

}
