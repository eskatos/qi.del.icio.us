package org.codeartisans.qidelicious;

import org.codeartisans.qidelicious.domain.tag.TagState;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public final class QiDeliciousAssembler implements Assembler {

    public void assemble(ModuleAssembly module) throws AssemblyException {
        module.addEntities(TagState.class);
    }
}
