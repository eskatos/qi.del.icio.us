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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Convenient placeholder text generator.
 * 
 * See http://en.wikipedia.org/wiki/Lorem_ipsum
 *
 * <h4>Implementation details</h4>
 *
 * QUID     Build text resources eagerly or lazily ?
 * SURE     All text resources are static.
 * FIXME    Implementing lazily for now without much thinking.
 * FIXME    Precompile all regexes.
 * 
 * @author Paul Merlin <paul@nosphere.org>
 */
public final class PlaceholderTextBuilder
{

    // The standard Lorem Ipsum passage, used since the 1500s
    private static final String LOREM_IPSUM_original = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    // Section 1.10.32 of "de Finibus Bonorum et Malorum", written by Cicero in 45 BC
    private static final String DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32 = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";
    // 1914 translation by H. Rackham
    private static final String DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32_EN = "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?";
    // Section 1.10.33 of "de Finibus Bonorum et Malorum", written by Cicero in 45 BC
    private static final String DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33 = "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.";
    // 1914 translation by H. Rackham
    private static final String DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33_EN = "On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.";
    // All pseudo latin sentences
    private static final List<String> PSEUDO_LATIN_SENTENCES = new ArrayList<String>();
    // All pseudo latin words, minimizeds
    private static final List<String> PSEUDO_LATIN_WORDS = new ArrayList<String>();
    // All english sentences
    private static final List<String> ENGLISH_SENTENCES = new ArrayList<String>();
    // All english words, minimizeds
    private static final List<String> ENGLISH_WORDS = new ArrayList<String>();
    // Builder instance variables
    private final Random rand = new Random();
    private TextLanguage language = TextLanguage.pseudoLatin;
    private TextUnit unit = TextUnit.words;
    private Integer count = 1;

    public static enum TextLanguage
    {

        pseudoLatin, english
    }

    public static enum TextUnit
    {

        words, sentences, paragraphs
    }

    public PlaceholderTextBuilder()
    {
        preparePseudoLatinSentences();
        preparePseudoLatinWords();
        prepareEnglishSentences();
        prepareEnglishWords();
    }

    private void preparePseudoLatinSentences()
    {
        if ( PSEUDO_LATIN_SENTENCES.isEmpty() ) {
            for ( String eachLoremIpsumSentence : extractSentences( LOREM_IPSUM_original ) ) {
                PSEUDO_LATIN_SENTENCES.add( eachLoremIpsumSentence.trim() );
            }
            for ( String eachDeFinibus32Sentence : extractSentences( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32 ) ) {
                PSEUDO_LATIN_SENTENCES.add( eachDeFinibus32Sentence.trim() );
            }
            for ( String eachDeFinibus33Sentence : extractSentences( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33 ) ) {
                PSEUDO_LATIN_SENTENCES.add( eachDeFinibus33Sentence.trim() );
            }
        }
    }

    private void preparePseudoLatinWords()
    {
        if ( PSEUDO_LATIN_WORDS.isEmpty() ) {
            for ( String eachLoremIpsumWord : extractWords( LOREM_IPSUM_original ) ) {
                PSEUDO_LATIN_WORDS.add( eachLoremIpsumWord.trim() );
            }
            for ( String eachDeFinibus32Word : extractWords( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32 ) ) {
                PSEUDO_LATIN_WORDS.add( eachDeFinibus32Word.trim() );
            }
            for ( String eachDeFinibus33Word : extractWords( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33 ) ) {
                PSEUDO_LATIN_WORDS.add( eachDeFinibus33Word.trim() );
            }
        }
    }

    private void prepareEnglishSentences()
    {
        if ( ENGLISH_SENTENCES.isEmpty() ) {
            for ( String eachDeFinibus32Sentence : extractSentences( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32_EN ) ) {
                ENGLISH_SENTENCES.add( eachDeFinibus32Sentence.trim() );
            }
            for ( String eachDeFinibus33Sentence : extractSentences( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33_EN ) ) {
                ENGLISH_SENTENCES.add( eachDeFinibus33Sentence.trim() );
            }
        }
    }

    private void prepareEnglishWords()
    {
        if ( ENGLISH_WORDS.isEmpty() ) {
            for ( String eachDeFinibus32Word : extractWords( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32_EN ) ) {
                ENGLISH_WORDS.add( eachDeFinibus32Word.trim() );
            }
            for ( String eachDeFinibus33Word : extractWords( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33_EN ) ) {
                ENGLISH_WORDS.add( eachDeFinibus33Word.trim() );
            }
        }
    }

    private String[] extractSentences( String text )
    {
        return text.split( "\\." );
    }

    private String[] extractWords( String text )
    {
        return text.toLowerCase( Locale.ENGLISH ).replaceAll( "[,.;:?!'\"\n\t\r]", " " ).split( " " );
    }

    public PlaceholderTextBuilder build( int count, TextUnit unit )
    {
        this.count = count;
        this.unit = unit;
        return this;
    }

    public PlaceholderTextBuilder in( TextLanguage language )
    {
        this.language = language;
        return this;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        Iterable<String> strings;
        String separator;
        switch ( unit ) {
            case paragraphs:
                strings = buildParagraphs();
                separator = "\n\n";
                break;
            case sentences:
                strings = buildSentences();
                separator = " ";
                break;
            case words:
                strings = buildWords();
                separator = " ";
                break;
            default:
                return RandomStringUtils.randomAlphanumeric( count * 5 );
        }
        Iterator<String> it = strings.iterator();
        while ( it.hasNext() ) {
            sb.append( it.next() );
            if ( it.hasNext() ) {
                sb.append( separator );
            }
        }
        return sb.toString();

    }

    private Iterable<String> buildParagraphs()
    {
        List<String> paragraphs = new LinkedList<String>();
        for ( int idx = 1; idx <= count; idx++ ) {
            switch ( language ) {
                case pseudoLatin:
                    switch ( rand.nextInt( 3 ) ) {
                        case 0:
                            paragraphs.add( LOREM_IPSUM_original );
                            break;
                        case 1:
                            paragraphs.add( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32 );
                            break;
                        case 2:
                            paragraphs.add( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33 );
                            break;
                    }
                    break;
                case english:
                    switch ( rand.nextInt( 2 ) ) {
                        case 0:
                            paragraphs.add( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_32_EN );
                            break;
                        case 1:
                            paragraphs.add( DE_FINIBUS_BONORUM_ET_MALORUM_1_10_33_EN );
                            break;
                    }
                    break;
            }
        }
        return paragraphs;
    }

    private Iterable<String> buildSentences()
    {
        List<String> sentences = new LinkedList<String>();
        for ( int idx = 1; idx <= count; idx++ ) {
            switch ( language ) {
                case pseudoLatin:
                    sentences.add( PSEUDO_LATIN_SENTENCES.get( rand.nextInt( PSEUDO_LATIN_SENTENCES.size() ) ) );
                    break;
                case english:
                    sentences.add( ENGLISH_SENTENCES.get( rand.nextInt( ENGLISH_SENTENCES.size() ) ) );
                    break;
            }
        }
        return sentences;
    }

    private Iterable<String> buildWords()
    {
        List<String> words = new LinkedList<String>();
        for ( int idx = 0; idx <= count; idx++ ) {
            switch ( language ) {
                case pseudoLatin:
                    words.add( PSEUDO_LATIN_WORDS.get( rand.nextInt( PSEUDO_LATIN_WORDS.size() ) ) );
                    break;
                case english:
                    words.add( ENGLISH_WORDS.get( rand.nextInt( ENGLISH_WORDS.size() ) ) );
                    break;
            }
        }
        return words;
    }

}
