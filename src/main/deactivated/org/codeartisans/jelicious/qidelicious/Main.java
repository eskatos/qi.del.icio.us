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
package org.codeartisans.qidelicious;

import del.icio.us.Delicious;
import del.icio.us.beans.Bundle;
import del.icio.us.beans.Post;
import del.icio.us.beans.Tag;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Grmbl.
 * Jersey, Restlet etc... c'est relou
 * delicious-java y'a de l'idée et déjà bcp de boulot de fait sur l'utilisation d'httpclient mais l'api est à chier
 * et en plus elle ne respecte pas la limite de 1 requête/seconde ....
 * faire un service delicious utilisant un micro domaine le tout utilisable dans guice et qi4j
 *
 * @author Paul Merlin <paul@nosphere.org>
 */
public final class Main
{

    static String USERNAME = "USERNAME";
    static String PASSWORD = "PASSWORD";
    static String API = "api.delicious.com/v1/";

    public static void main(String[] args)
            throws MalformedURLException, URISyntaxException
    {
        Delicious d = new Delicious(USERNAME, PASSWORD, "https://" + API);

        System.out.println("// Fetching all Posts");
        List<Post> posts = d.getAllPosts();

        System.out.println("// Fetching all Tag Bundles");
        List<Bundle> tagBundles = d.getBundles();

        System.out.println("// Fetching all Tags");
        List<Tag> tags = d.getTags();
        for (Tag eachTag : tags) {
            System.out.println(eachTag.getTag() + " tag has been used " + eachTag.getCount() + " time(s).");
        }

    }

}
