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
import org.apache.shiro.realm.RealmFactory;
import org.codeartisans.jizmo.JizmoStructure;
import org.codeartisans.jizmo.domain.model.users.UserRepository;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.structure.Application;
import org.qi4j.api.structure.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class JizmoRealmFactory
        implements RealmFactory
{

    private static final Logger LOGGER = LoggerFactory.getLogger( JizmoRealmFactory.class );
    private static Application application;

    public static void setQi4jApplication( Application application )
    {
        JizmoRealmFactory.application = application;
    }

    public JizmoRealmFactory()
    {
        LOGGER.info( "==============================================> BLOB REALM FACTORY INSTANCIATED !!!!!!!!!!!!!!" );
    }

    @Override
    public Collection<Realm> getRealms()
    {
        LOGGER.info( "==============================================> GET REALMS !!!!!!!!!!!!!!" );
        Module domainModule = application.findModule( JizmoStructure.Layers.DOMAIN, JizmoStructure.DomainModules.MODEL );
        ServiceReference<UserRepository> userReposRef = domainModule.serviceFinder().findService( UserRepository.class );
        UserRepository userRepository = userReposRef.get();
        JizmoRealm realm = new JizmoRealm( userRepository );
        return Arrays.asList( new Realm[]{ realm } );
    }

}
