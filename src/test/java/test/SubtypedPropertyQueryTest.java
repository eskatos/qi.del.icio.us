package test;

import org.codeartisans.java.toolbox.CollectionUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.property.Property;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryExpressions;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

public class SubtypedPropertyQueryTest
        extends AbstractQi4jTest
{

    public void assemble(ModuleAssembly module)
            throws AssemblyException
    {
        module.addEntities(FlatEntity.class, WoupsEntity.class);
        module.addServices(MemoryEntityStoreService.class, UuidIdentityGeneratorService.class);
        new RdfMemoryStoreAssembler().assemble(module);
    }

    interface FlatEntity extends EntityComposite
    {
        Property<String> name();
    }

    interface WoupsEntity extends EntityComposite
    {
        Name name();
    }

    interface Name extends Property<String> { }

    @Test
    public void givenAnEntityWithSimplePropertyWhenQueriedOnPropertyThenJustWork()
            throws UnitOfWorkCompletionException
    {
        FlatEntity test;
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();
            EntityBuilder<FlatEntity> builder = uow.newEntityBuilder(FlatEntity.class);
            test = builder.instance();
            test.name().set("Bob");
            test = builder.newInstance();
            uow.complete();
        }
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            QueryBuilder<FlatEntity> queryBuilder = queryBuilderFactory.newQueryBuilder(FlatEntity.class);
            FlatEntity thingTemplate = QueryExpressions.templateFor(FlatEntity.class);
            queryBuilder.where(QueryExpressions.eq(thingTemplate.name(), "Bob"));

            Query<FlatEntity> query = queryBuilder.newQuery(uow);
            query.maxResults(1);

            FlatEntity foundByName = CollectionUtils.firstElementOrNull(query);
            Assert.assertEquals("Bob", foundByName.name().get());

            uow.complete();
        }
    }

    // FIXME : This one do not work.
    @Ignore
    @Test
    public void givenAnEntityWithSubtypedPropertyWhenQueriedOnPropertyThenJustWork()
            throws UnitOfWorkCompletionException
    {
        WoupsEntity test;
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();
            EntityBuilder<WoupsEntity> builder = uow.newEntityBuilder(WoupsEntity.class);
            test = builder.instance();
            test.name().set("Bob");
            test = builder.newInstance();
            uow.complete();
        }
        {
            UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();

            QueryBuilder<WoupsEntity> queryBuilder = queryBuilderFactory.newQueryBuilder(WoupsEntity.class);
            WoupsEntity thingTemplate = QueryExpressions.templateFor(WoupsEntity.class);
            queryBuilder.where(QueryExpressions.eq(thingTemplate.name(), "Bob"));

            Query<WoupsEntity> query = queryBuilder.newQuery(uow);
            query.maxResults(1);

            WoupsEntity foundByName = CollectionUtils.firstElementOrNull(query);
            Assert.assertEquals("Bob", foundByName.name().get());

            uow.complete();
        }
    }

}
