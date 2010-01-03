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
package org.codeartisans.blob;

import org.codeartisans.blob.domain.model.Tag;
import org.codeartisans.blob.domain.model.Thing;
import org.codeartisans.blob.domain.composites.entities.ThingEntityComposite;
import org.codeartisans.java.toolbox.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryExpressions;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class BlobTest
        extends AbstractQi4jTest
{

    public void assemble(ModuleAssembly module)
            throws AssemblyException
    {
        new BlobAssembler().assemble(module);
    }

    @Test
    public void createThingWithTags()
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

        EntityBuilder<Tag> tagBuilder = uow.newEntityBuilder(Tag.class);
        EntityBuilder<ThingEntityComposite> thingBuilder = uow.newEntityBuilder(ThingEntityComposite.class);

        Tag tagProto = tagBuilder.instance();
        tagProto.name().set("qi4j");

        Tag tag = tagBuilder.newInstance();

        Thing thingProto = thingBuilder.instance();
        thingProto.mimeType().set("application/x-blob");
        thingProto.name().set("Blob");
        thingProto.shortdesc().set("A short blob");
        thingProto.tags().add(tag);

        ThingEntityComposite thingEntity = thingBuilder.newInstance();
        String thingIdentity = thingEntity.identity().get();
        Thing thing = thingEntity;

        System.out.println("================================================");
        System.out.println("tag: " + tag.toString());
        System.out.println("  is named: " + tag.name().get());
        System.out.println("");
        System.out.println("thing: " + thing.toString());
        System.out.println("  is named: " + thing.name().get());
        System.out.println("  has shortdesc: " + thing.shortdesc().get());
        System.out.println("  has mimetype: " + thing.mimeType().get());
        System.out.println("  has tags: " + thing.tags().toSet());
        System.out.println("================================================");

        uow.complete();

        /* ************************************************************ */

        uow = unitOfWorkFactory.newUnitOfWork();

        Thing fetchedThing = uow.get(Thing.class, thingIdentity);

        checkThing("fetchedByIdentity", fetchedThing);

        uow.discard();

        /* ************************************************************ */

        uow = unitOfWorkFactory.newUnitOfWork();

        QueryBuilder<Thing> queryBuilder = queryBuilderFactory.newQueryBuilder(Thing.class);
        Thing thingTemplate = QueryExpressions.templateFor(Thing.class);
        queryBuilder.where(QueryExpressions.eq(thingTemplate.name(), "Blob"));

        Query<Thing> query = queryBuilder.newQuery(uow);
        query.maxResults(1);

        Thing thingFoundByName = CollectionUtils.firstElementOrNull(query);

        checkThing("foundByName", thingFoundByName);

        uow.discard();

        /* ************************************************************ */

        uow = unitOfWorkFactory.newUnitOfWork();

        queryBuilder = queryBuilderFactory.newQueryBuilder(Thing.class);
        thingTemplate = QueryExpressions.templateFor(Thing.class);
        Tag tagTemplate = QueryExpressions.templateFor(Tag.class);
        queryBuilder.where(QueryExpressions.and(
                QueryExpressions.eq(tagTemplate.name(), "qi4j"),
                QueryExpressions.contains(thingTemplate.tags(), tagTemplate)));

        query = queryBuilder.newQuery(uow);
        query.maxResults(1);

        Thing thingFoundByTag = CollectionUtils.firstElementOrNull(query);

        checkThing("foundByTag", thingFoundByTag);

        uow.discard();

    }

    private void checkThing(String name, Thing thing)
    {
        System.out.println("================================================");
        System.out.println(name + ": " + thing.toString());
        System.out.println("  is named: " + thing.name().get());
        System.out.println("  has shortdesc: " + thing.shortdesc().get());
        System.out.println("  has mimetype: " + thing.mimeType().get());
        System.out.println("  has tag: " + thing.tags().get(0).name().get());
        System.out.println("================================================");

        Assert.assertEquals("Blob", thing.name().get());
        Assert.assertEquals("A short blob", thing.shortdesc().get());
        Assert.assertEquals("application/x-blob", thing.mimeType().get());
        Assert.assertEquals("qi4j", thing.tags().get(0).name().get());
    }

}
