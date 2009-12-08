package org.codeartisans.qidelicious.domain.tag;

import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.property.Property;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public interface TagState extends EntityComposite
{

    Property<String> tag();

}
