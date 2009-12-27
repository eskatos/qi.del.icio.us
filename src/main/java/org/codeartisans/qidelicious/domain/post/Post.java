package org.codeartisans.qidelicious.domain.post;

import java.util.Date;
import org.codeartisans.qidelicious.domain.tag.Tag;
import org.qi4j.api.entity.association.ManyAssociation;
import org.qi4j.api.property.Property;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public interface Post
{

    Property<String> href();

    Property<String> description();

    Property<String> extendedDescription(); // extended

    Property<String> deliciousIdentity(); // hash

    Property<String> hash(); // meta

    ManyAssociation<Tag> tags(); // tag

    Property<Date> date(); // time

    Property<Boolean> shared();

}
