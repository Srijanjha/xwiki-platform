/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.xml.html;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.w3c.dom.Document;
import org.xwiki.xml.internal.html.CleaningFilter;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Default implementation for {@link HTMLCleaner} using the
 * <a href="HTML Cleaner framework>http://htmlcleaner.sourceforge.net/</a>.
 * 
 * @version $Id: $
 * @since 1.6M1
 */
public class DefaultHTMLCleaner implements HTMLCleaner
{
    /**
     * List of default cleaning filters to call when cleaning code with HTML Cleaner. This is for cases when
     * there are no <a href="http://htmlcleaner.sourceforge.net/parameters.php">properties</a> defined in HTML
     * Cleaner.
     */
    private List<CleaningFilter> filters;

    public DefaultHTMLCleaner()
    {
        this.filters = new ArrayList<CleaningFilter>();
    }

    /**
     * {@inheritDoc}
     * @see org.xwiki.xml.html.HTMLCleaner#clean(String)
     */
    public Document clean(String originalHtmlContent)
    {
        HtmlCleaner cleaner = new HtmlCleaner();

        CleanerProperties props = cleaner.getProperties();
        props.setOmitUnknownTags(true);

        TagNode cleanedNode;
        try {
            cleanedNode = cleaner.clean(originalHtmlContent);
        } catch (IOException e) {
            // This shouldn't happen since we're not doing any IO... I consider this a flaw in the
            // design of HTML Cleaner.
            throw new RuntimeException("Unhandled error when cleaning HTML [" + originalHtmlContent + "]", e);
        }

        Document document;
        try {
            document = new DomSerializer(props).createDOM(cleanedNode);
        } catch (ParserConfigurationException e) {
            // The XML generated by HTMLCleander should be clean and not generated any exception
            throw new RuntimeException("Failed to generate W3C Document from cleaned HTML", e);
        }

        return document;
    }
}