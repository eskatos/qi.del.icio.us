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
package org.codeartisans.jizmo;

import static org.codeartisans.jizmo.PlaceholderTextBuilder.TextLanguage.*;
import static org.codeartisans.jizmo.PlaceholderTextBuilder.TextUnit.*;
import org.junit.Test;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class PlaceholderTextBuilderTest
{

    @Test
    public void test()
    {
        PlaceholderTextBuilder builder = new PlaceholderTextBuilder();

        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( builder.build( 5, paragraphs ).in( pseudoLatin ).toString() );
        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( builder.build( 5, paragraphs ).in( english ).toString() );
        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( builder.build( 7, sentences ).in( pseudoLatin ).toString() );
        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( builder.build( 7, sentences ).in( english ).toString() );
        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( builder.build( 30, words ).in( pseudoLatin ).toString() );
        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( builder.build( 30, words ).in( english ).toString() );
        System.out.println( "----------------------------------------------------------------------------------" );
    }

}
