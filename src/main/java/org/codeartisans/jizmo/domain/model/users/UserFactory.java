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
package org.codeartisans.jizmo.domain.model.users;

import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.library.shiro.domain.securehash.SecureHashFactory;

/**
 * @author Paul Merlin <p.merlin@nosphere.org>
 */
@Mixins( UserFactory.Mixin.class )
public interface UserFactory
        extends ServiceComposite
{

    UserEntity create( String nickname, String email, char[] password );

    abstract class Mixin
            implements UserFactory
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Service
        private SecureHashFactory secureHashFactory;

        @Override
        public UserEntity create( String username, String email, char[] password )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<UserEntity> userBuilder = uow.newEntityBuilder( UserEntity.class );
            UserEntity user = userBuilder.instance();
            user.username().set( username );
            user.email().set( email );
            user.secureHash().set( secureHashFactory.create( password ) );
            user = userBuilder.newInstance();
            return user;
        }

    }

}
