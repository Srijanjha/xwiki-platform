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
 */
package com.xpn.xwiki.internal.model.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.model.EntityType;
import org.xwiki.model.internal.DefaultModelConfiguration;
import org.xwiki.model.internal.reference.DefaultSymbolScheme;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceResolver;
import org.xwiki.test.annotation.ComponentList;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.test.MockitoOldcoreRule;

/**
 * Unit tests for {@link CurrentStringEntityReferenceResolver}.
 * 
 * @version $Id$
 */
@ComponentList(value = {
    CurrentEntityReferenceProvider.class,
    CurrentStringEntityReferenceResolver.class,
    DefaultModelConfiguration.class,
    DefaultSymbolScheme.class
})
public class CurrentStringEntityReferenceResolverTest
{
    @Rule
    public MockitoOldcoreRule oldcore = new MockitoOldcoreRule();

    private static final String CURRENT_WIKI = "currentwiki";

    private static final String CURRENT_SPACE = "currentspace";

    private static final String CURRENT_PAGE = "currentpage";

    private static final String CURRENTDOC_SPACE = "currentdocspace";

    private static final String CURRENTDOC_PAGE = "currentdocpage";

    private EntityReferenceResolver<String> resolver;

    @Before
    public void before() throws Exception
    {
        this.oldcore.getXWikiContext().setWikiId(CURRENT_WIKI);

        this.resolver = this.oldcore.getMocker().getInstance(EntityReferenceResolver.TYPE_STRING, "current");
    }

    @Test
    public void testResolveDocumentReferenceWhenNoContextWiki() throws Exception
    {
        this.oldcore.getXWikiContext().setWikiId(null);

        EntityReference reference = resolver.resolve("", EntityType.DOCUMENT);

        Assert.assertEquals("xwiki", reference.extractReference(EntityType.WIKI).getName());
        Assert.assertEquals("Main", reference.extractReference(EntityType.SPACE).getName());
        Assert.assertEquals("WebHome", reference.getName());
    }

    @Test
    public void testResolveDocumentReferenceWhenNoContextDocument() throws Exception
    {
        this.oldcore.getXWikiContext().setWikiId(null);
        this.oldcore.getXWikiContext().setDoc(null);

        EntityReference reference = resolver.resolve("", EntityType.DOCUMENT);

        Assert.assertEquals("xwiki", reference.extractReference(EntityType.WIKI).getName());
        Assert.assertEquals("Main", reference.extractReference(EntityType.SPACE).getName());
        Assert.assertEquals("WebHome", reference.getName());
    }

    @Test
    public void testResolveDocumentReferenceWhenContextDocument() throws Exception
    {
        this.oldcore.getXWikiContext().setDoc(
            new XWikiDocument(new DocumentReference(CURRENT_WIKI, CURRENTDOC_SPACE, CURRENTDOC_PAGE)));

        EntityReference reference = resolver.resolve("", EntityType.DOCUMENT);

        Assert.assertEquals(CURRENT_WIKI, reference.extractReference(EntityType.WIKI).getName());
        Assert.assertEquals(CURRENTDOC_SPACE, reference.extractReference(EntityType.SPACE).getName());
        Assert.assertEquals(CURRENTDOC_PAGE, reference.getName());
    }

    @Test
    public void testResolveAttachmentReference() throws Exception
    {
        this.oldcore.getXWikiContext().setDoc(
            new XWikiDocument(new DocumentReference(CURRENT_WIKI, CURRENTDOC_SPACE, CURRENTDOC_PAGE)));

        EntityReference reference = resolver.resolve("", EntityType.ATTACHMENT);

        Assert.assertEquals(CURRENT_WIKI, reference.extractReference(EntityType.WIKI).getName());
        Assert.assertEquals(CURRENTDOC_SPACE, reference.extractReference(EntityType.SPACE).getName());
        Assert.assertEquals(CURRENTDOC_PAGE, reference.extractReference(EntityType.DOCUMENT).getName());
        Assert.assertEquals("filename", reference.getName());
    }

    @Test
    public void testResolveAttachmentReferenceWhenMissingParentsAndNoContextDocument()
    {
        EntityReference reference = resolver.resolve("filename", EntityType.ATTACHMENT);

        Assert.assertEquals("WebHome", reference.getParent().getName());
        Assert.assertEquals(EntityType.DOCUMENT, reference.getParent().getType());
        Assert.assertEquals("Main", reference.getParent().getParent().getName());
        Assert.assertEquals(EntityType.SPACE, reference.getParent().getParent().getType());
        Assert.assertEquals(CURRENT_WIKI, reference.getParent().getParent().getParent().getName());
        Assert.assertEquals(EntityType.WIKI, reference.getParent().getParent().getParent().getType());
    }

    @Test
    public void testResolveAttachmentReferenceWhenMissingParentsAndContextDocument()
    {
        this.oldcore.getXWikiContext().setWikiId(CURRENT_WIKI);
        this.oldcore.getXWikiContext().setDoc(
            new XWikiDocument(new DocumentReference("docwiki", CURRENT_SPACE, CURRENT_PAGE)));

        EntityReference reference = resolver.resolve("filename", EntityType.ATTACHMENT);

        Assert.assertEquals(CURRENT_PAGE, reference.getParent().getName());
        Assert.assertEquals(EntityType.DOCUMENT, reference.getParent().getType());
        Assert.assertEquals(CURRENT_SPACE, reference.getParent().getParent().getName());
        Assert.assertEquals(EntityType.SPACE, reference.getParent().getParent().getType());
        Assert.assertEquals(CURRENT_WIKI, reference.getParent().getParent().getParent().getName());
        Assert.assertEquals(EntityType.WIKI, reference.getParent().getParent().getParent().getType());
    }
}
