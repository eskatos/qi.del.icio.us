/*
 * Copyright (c) 2010 Paul Merlin <paul@nosphere.org>
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
package org.codeartisans.jizmo.security;

import java.util.Arrays;
import java.util.Collection;
import org.apache.shiro.realm.Realm;
import org.codeartisans.jizmo.JizmoStructure;
import org.qi4j.api.object.ObjectBuilderFactory;
import org.qi4j.api.structure.Module;
import org.qi4j.library.shiro.authc.SecureHashCredentialsMatcher;
import org.qi4j.library.shiro.realms.AbstractQi4jRealmFactory;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class JizmoRealmFactory
        extends AbstractQi4jRealmFactory
{

    @Override
    public Collection<Realm> getRealms()
    {
        Module domainModule = application.findModule( JizmoStructure.Layers.APPLICATION, JizmoStructure.ApplicationModules.SECURITY );
        ObjectBuilderFactory obf = domainModule.objectBuilderFactory();
        JizmoRealm jizmoRealm = obf.newObject( JizmoRealm.class );
        jizmoRealm.setCredentialsMatcher( new SecureHashCredentialsMatcher() );
        return Arrays.asList( new Realm[]{ jizmoRealm } );
    }

}
