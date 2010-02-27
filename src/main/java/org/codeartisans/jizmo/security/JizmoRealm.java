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

import org.codeartisans.jizmo.domain.model.users.UserRepository;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.library.shiro.domain.RoleAssignee;
import org.qi4j.library.shiro.domain.SecureHashSecurable;
import org.qi4j.library.shiro.realms.AbstractSecureHashQi4jRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public class JizmoRealm
        extends AbstractSecureHashQi4jRealm
{

    private static final Logger LOGGER = LoggerFactory.getLogger( JizmoRealm.class );
    @Service
    private UserRepository userRepos;

    public JizmoRealm()
    {
        setName( JizmoRealm.class.getSimpleName() );
    }

    @Override
    protected SecureHashSecurable getSecureHashSecurable( String username )
    {
        return userRepos.findByUsername( username );
    }

    @Override
    protected RoleAssignee getRoleAssignee( String username )
    {
        return userRepos.findByUsername( username );
    }

}