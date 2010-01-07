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

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class FixtureBuilder
{

    interface FixtureInvariants
    {

        static String[] thingsNames = new String[]{"foo", "bar"};
        static String[] tagsNames = new String[]{"qi4j cop ddd"};
    }

    public class FixtureSettings
    {

        private Integer things = 10;
        private Integer tags = 10;

        private FixtureSettings()
        {
        }

        void thingsNumber(Integer count)
        {
            things = count;
        }

        void tagsNumber(Integer count)
        {
            tags = count;
        }

    }

    @Structure
    private UnitOfWorkFactory uowf;

    public class Fixtures
    {
    }

    private FixtureSettings settings = new FixtureSettings();

    public FixtureBuilder()
    {
    }

    public FixtureSettings settingsPrototype()
    {
        return settings;
    }

    public Fixtures populateStore()
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = uowf.newUnitOfWork();

        


        uow.complete();
        return null;
    }

}
