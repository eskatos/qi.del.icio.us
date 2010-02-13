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

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.codeartisans.jizmo.domain.model.users.UserEntity;
import org.codeartisans.jizmo.domain.model.users.UserRepository;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
public class JizmoRealm
        extends AuthorizingRealm
{

    private final UserRepository userRepository;

    public JizmoRealm( UserRepository userRepository )
    {
        super();
        setCachingEnabled( false );
        setName( "CoolBlobRealm" );
        this.userRepository = userRepository;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo( AuthenticationToken token )
            throws AuthenticationException
    {
        // FIXME State on what kind of principal we will use
        String email = ( String ) token.getPrincipal();
        UserEntity user = userRepository.findByEmail( email );
        if ( user == null ) {
            return null;
        }
        // TODO Real implementation
        SimpleAuthenticationInfo authInfo = new SimpleAuthenticationInfo( email, "chewchew", getName() );
        return authInfo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo( PrincipalCollection principals )
    {
        // FIXME State on what kind of principal we will use
        String email = ( String ) principals.getPrimaryPrincipal();
        UserEntity user = userRepository.findByEmail( email );
        if ( user == null ) {
            return null;
        }
        Set<String> roles = new LinkedHashSet<String>();
        roles.add( "admin" );
        return new SimpleAuthorizationInfo( roles );
    }

}
