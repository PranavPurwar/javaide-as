/*
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;

/**
 * @author Morten Jorgensen
 */
public class ErrorMessages_sv extends ListResourceBundle {

    /*
     * XSLTC compile-time error messages.
     *
     * General notes to translators and definitions:
     *
     *   1) XSLTC is the name of the product.  It is an acronym for "XSLT Compiler".
     *      XSLT is an acronym for "XML Stylesheet Language: Transformations".
     *
     *   2) A stylesheet is a description of how to transform an input XML document
     *      into a resultant XML document (or HTML document or text).  The
     *      stylesheet itself is described in the form of an XML document.
     *
     *   3) A template is a component of a stylesheet that is used to match a
     *      particular portion of an input document and specifies the form of the
     *      corresponding portion of the output document.
     *
     *   4) An axis is a particular "dimension" in a tree representation of an XML
     *      document; the nodes in the tree are divided along different axes.
     *      Traversing the "child" axis, for instance, means that the program
     *      would visit each child of a particular node; traversing the "descendant"
     *      axis means that the program would visit the child nodes of a particular
     *      node, their children, and so on until the leaf nodes of the tree are
     *      reached.
     *
     *   5) An iterator is an object that traverses nodes in a tree along a
     *      particular axis, one at a time.
     *
     *   6) An element is a mark-up tag in an XML document; an attribute is a
     *      modifier on the tag.  For example, in <elem attr='val' attr2='val2'>
     *      "elem" is an element name, "attr" and "attr2" are attribute names with
     *      the values "val" and "val2", respectively.
     *
     *   7) A namespace declaration is a special attribute that is used to associate
     *      a prefix with a URI (the namespace).  The meanings of element names and
     *      attribute names that use that prefix are defined with respect to that
     *      namespace.
     *
     *   8) DOM is an acronym for Document Object Model.  It is a tree
     *      representation of an XML document.
     *
     *      SAX is an acronym for the Simple API for XML processing.  It is an API
     *      used inform an XML processor (in this case XSLTC) of the structure and
     *      content of an XML document.
     *
     *      Input to the stylesheet processor can come from an XML parser in the
     *      form of a DOM tree or through the SAX API.
     *
     *   9) DTD is a document type declaration.  It is a way of specifying the
     *      grammar for an XML file, the names and types of elements, attributes,
     *      etc.
     *
     *  10) XPath is a specification that describes a notation for identifying
     *      nodes in a tree-structured representation of an XML document.  An
     *      instance of that notation is referred to as an XPath expression.
     *
     *  11) Translet is an invented term that refers to the class file that contains
     *      the compiled form of a stylesheet.
     */

    // These message should be read from a locale-specific resource bundle
    /**
     * Get the lookup table for error messages.
     *
     * @return The message lookup table.
     */
    public Object[][] getContents() {
        return new Object[][] {
            {
                ErrorMsg.MULTIPLE_STYLESHEET_ERR,
                "Fler \u00E4n en formatmall har definierats i samma fil."
            },

            /*
             * Note to translators:  The substitution text is the name of a
             * template.  The same name was used on two different templates in the
             * same stylesheet.
             */
            {
                ErrorMsg.TEMPLATE_REDEF_ERR,
                "Mallen ''{0}'' har redan definierats i denna formatmall."
            },

            /*
             * Note to translators:  The substitution text is the name of a
             * template.  A reference to the template name was encountered, but the
             * template is undefined.
             */
            {
                ErrorMsg.TEMPLATE_UNDEF_ERR,
                "Mallen ''{0}'' har inte definierats i denna formatmall."
            },

            /*
             * Note to translators:  The substitution text is the name of a variable
             * that was defined more than once.
             */
            {
                ErrorMsg.VARIABLE_REDEF_ERR,
                "Variabeln ''{0}'' har definierats flera g\u00E5nger i samma omfattning."
            },

            /*
             * Note to translators:  The substitution text is the name of a variable
             * or parameter.  A reference to the variable or parameter was found,
             * but it was never defined.
             */
            {
                ErrorMsg.VARIABLE_UNDEF_ERR,
                "Variabeln eller parametern ''{0}'' har inte definierats."
            },

            /*
             * Note to translators:  The word "class" here refers to a Java class.
             * Processing the stylesheet required a class to be loaded, but it could
             * not be found.  The substitution text is the name of the class.
             */
            {ErrorMsg.CLASS_NOT_FOUND_ERR, "Hittar inte klassen ''{0}''."},

            /*
             * Note to translators:  The word "method" here refers to a Java method.
             * Processing the stylesheet required a reference to the method named by
             * the substitution text, but it could not be found.  "public" is the
             * Java keyword.
             */
            {
                ErrorMsg.METHOD_NOT_FOUND_ERR,
                "Hittar inte den externa metoden ''{0}'' (m\u00E5ste vara allm\u00E4n)."
            },

            /*
             * Note to translators:  The word "method" here refers to a Java method.
             * Processing the stylesheet required a reference to the method named by
             * the substitution text, but no method with the required types of
             * arguments or return type could be found.
             */
            {
                ErrorMsg.ARGUMENT_CONVERSION_ERR,
                "Kan inte konvertera argument/returtyp vid anrop till metoden ''{0}''"
            },

            /*
             * Note to translators:  The file or URI named in the substitution text
             * is missing.
             */
            {ErrorMsg.FILE_NOT_FOUND_ERR, "Fil eller URI ''{0}'' hittades inte."},

            /*
             * Note to translators:  This message is displayed when the URI
             * mentioned in the substitution text is not well-formed syntactically.
             */
            {ErrorMsg.INVALID_URI_ERR, "Ogiltig URI ''{0}''."},

            /*
             * Note to translators:  This message is displayed when the URI
             * mentioned in the substitution text is not well-formed syntactically.
             */
            {
                ErrorMsg.CATALOG_EXCEPTION,
                "JAXP08090001: CatalogResolver \u00E4r aktiverat med katalogen \"{0}\", men ett"
                    + " CatalogException returneras."
            },

            /*
             * Note to translators:  The file or URI named in the substitution text
             * exists but could not be opened.
             */
            {ErrorMsg.FILE_ACCESS_ERR, "Kan inte \u00F6ppna filen eller URI ''{0}''."},

            /*
             * Note to translators: <xsl:stylesheet> and <xsl:transform> are
             * keywords that should not be translated.
             */
            {
                ErrorMsg.MISSING_ROOT_ERR,
                "F\u00F6rv\u00E4ntade <xsl:stylesheet>- eller <xsl:transform>-element."
            },

            /*
             * Note to translators:  The stylesheet contained a reference to a
             * namespace prefix that was undefined.  The value of the substitution
             * text is the name of the prefix.
             */
            {ErrorMsg.NAMESPACE_UNDEF_ERR, "Namnrymdsprefixet ''{0}'' har inte deklarerats."},

            /*
             * Note to translators:  The Java function named in the stylesheet could
             * not be found.
             */
            {ErrorMsg.FUNCTION_RESOLVE_ERR, "Kan inte matcha anrop till funktionen ''{0}''."},

            /*
             * Note to translators:  The substitution text is the name of a
             * function.  A literal string here means a constant string value.
             */
            {
                ErrorMsg.NEED_LITERAL_ERR,
                "Argument till ''{0}'' m\u00E5ste vara en litteral str\u00E4ng."
            },

            /*
             * Note to translators:  This message indicates there was a syntactic
             * error in the form of an XPath expression.  The substitution text is
             * the expression.
             */
            {ErrorMsg.XPATH_PARSER_ERR, "Fel vid tolkning av XPath-uttrycket ''{0}''."},

            /*
             * Note to translators:  An element in the stylesheet requires a
             * particular attribute named by the substitution text, but that
             * attribute was not specified in the stylesheet.
             */
            {ErrorMsg.REQUIRED_ATTR_ERR, "Det obligatoriska attributet ''{0}'' saknas."},

            /*
             * Note to translators:  This message indicates that a character not
             * permitted in an XPath expression was encountered.  The substitution
             * text is the offending character.
             */
            {ErrorMsg.ILLEGAL_CHAR_ERR, "Otill\u00E5tet tecken ''{0}'' i XPath-uttrycket."},

            /*
             * Note to translators:  A processing instruction is a mark-up item in
             * an XML document that request some behaviour of an XML processor.  The
             * form of the name of was invalid in this case, and the substitution
             * text is the name.
             */
            {
                ErrorMsg.ILLEGAL_PI_ERR,
                "''{0}'' \u00E4r ett otill\u00E5tet namn i bearbetningsinstruktion."
            },

            /*
             * Note to translators:  This message is reported if the stylesheet
             * being processed attempted to construct an XML document with an
             * attribute in a place other than on an element.  The substitution text
             * specifies the name of the attribute.
             */
            {ErrorMsg.STRAY_ATTRIBUTE_ERR, "Attributet ''{0}'' finns utanf\u00F6r elementet."},

            /*
             * Note to translators:  An attribute that wasn't recognized was
             * specified on an element in the stylesheet.  The attribute is named
             * by the substitution
             * text.
             */
            {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR, "''{0}'' \u00E4r ett otill\u00E5tet attribut."},

            /*
             * Note to translators:  "import" and "include" are keywords that should
             * not be translated.  This messages indicates that the stylesheet
             * named in the substitution text imported or included itself either
             * directly or indirectly.
             */
            {
                ErrorMsg.CIRCULAR_INCLUDE_ERR,
                "Cirkul\u00E4r import/include. Formatmallen ''{0}'' har redan laddats."
            },

            /*
             * Note to translators:  "xsl:import" and "xsl:include" are keywords that
             * should not be translated.
             */
            {
                ErrorMsg.IMPORT_PRECEDE_OTHERS_ERR,
                "Underordnade till xsl:import-elementet m\u00E5ste komma f\u00F6re alla andra"
                    + " underordnade till element f\u00F6r ett xsl:stylesheet-element, inklusive"
                    + " alla underordnade till xsl:include-elementet."
            },

            /*
             * Note to translators:  A result-tree fragment is a portion of a
             * resulting XML document represented as a tree.  "<xsl:sort>" is a
             * keyword and should not be translated.
             */
            {
                ErrorMsg.RESULT_TREE_SORT_ERR,
                "Resultattr\u00E4dfragment kan inte sorteras (<xsl:sort>-element ignoreras). Du"
                    + " m\u00E5ste sortera noderna n\u00E4r resultattr\u00E4det skapas."
            },

            /*
             * Note to translators:  A name can be given to a particular style to be
             * used to format decimal values.  The substitution text gives the name
             * of such a style for which more than one declaration was encountered.
             */
            {ErrorMsg.SYMBOLS_REDEF_ERR, "Decimalformateringen ''{0}'' har redan definierats."},

            /*
             * Note to translators:  The stylesheet version named in the
             * substitution text is not supported.
             */
            {ErrorMsg.XSL_VERSION_ERR, "XSL-versionen ''{0}'' underst\u00F6ds inte i XSLTC."},

            /*
             * Note to translators:  The definitions of one or more variables or
             * parameters depend on one another.
             */
            {
                ErrorMsg.CIRCULAR_VARIABLE_ERR,
                "Cirkul\u00E4r variabel-/parameterreferens i ''{0}''."
            },

            /*
             * Note to translators:  The operator in an expresion with two operands was
             * not recognized.
             */
            {ErrorMsg.ILLEGAL_BINARY_OP_ERR, "Ok\u00E4nd operator f\u00F6r bin\u00E4rt uttryck."},

            /*
             * Note to translators:  This message is produced if a reference to a
             * function has too many or too few arguments.
             */
            {ErrorMsg.ILLEGAL_ARG_ERR, "Otill\u00E5tna argument f\u00F6r funktionsanrop."},

            /*
             * Note to translators:  "document()" is the name of function and must
             * not be translated.  A node-set is a set of the nodes in the tree
             * representation of an XML document.
             */
            {
                ErrorMsg.DOCUMENT_ARG_ERR,
                "Andra argumentet f\u00F6r document()-funktion m\u00E5ste vara en"
                    + " nodupps\u00E4ttning."
            },

            /*
             * Note to translators:  "<xsl:when>" and "<xsl:choose>" are keywords
             * and should not be translated.  This message describes a syntax error
             * in the stylesheet.
             */
            {ErrorMsg.MISSING_WHEN_ERR, "Minst ett <xsl:when>-element kr\u00E4vs i <xsl:choose>."},

            /*
             * Note to translators:  "<xsl:otherwise>" and "<xsl:choose>" are
             * keywords and should not be translated.  This message describes a
             * syntax error in the stylesheet.
             */
            {
                ErrorMsg.MULTIPLE_OTHERWISE_ERR,
                "Endast ett <xsl:otherwise>-element \u00E4r till\u00E5tet i <xsl:choose>."
            },

            /*
             * Note to translators:  "<xsl:otherwise>" and "<xsl:choose>" are
             * keywords and should not be translated.  This message describes a
             * syntax error in the stylesheet.
             */
            {
                ErrorMsg.STRAY_OTHERWISE_ERR,
                "<xsl:otherwise> anv\u00E4nds endast inom <xsl:choose>."
            },

            /*
             * Note to translators:  "<xsl:when>" and "<xsl:choose>" are keywords
             * and should not be translated.  This message describes a syntax error
             * in the stylesheet.
             */
            {ErrorMsg.STRAY_WHEN_ERR, "<xsl:when> anv\u00E4nds endast inom <xsl:choose>."},

            /*
             * Note to translators:  "<xsl:when>", "<xsl:otherwise>" and
             * "<xsl:choose>" are keywords and should not be translated.  This
             * message describes a syntax error in the stylesheet.
             */
            {
                ErrorMsg.WHEN_ELEMENT_ERR,
                "Endast <xsl:when>- och <xsl:otherwise>-element \u00E4r till\u00E5tna i"
                    + " <xsl:choose>."
            },

            /*
             * Note to translators:  "<xsl:attribute-set>" and "name" are keywords
             * that should not be translated.
             */
            {ErrorMsg.UNNAMED_ATTRIBSET_ERR, "<xsl:attribute-set> saknar 'name'-attribut."},

            /*
             * Note to translators:  An element in the stylesheet contained an
             * element of a type that it was not permitted to contain.
             */
            {ErrorMsg.ILLEGAL_CHILD_ERR, "Otill\u00E5tet underordnat element."},

            /*
             * Note to translators:  The stylesheet tried to create an element with
             * a name that was not a valid XML name.  The substitution text contains
             * the name.
             */
            {ErrorMsg.ILLEGAL_ELEM_NAME_ERR, "Du kan inte anropa elementet ''{0}''"},

            /*
             * Note to translators:  The stylesheet tried to create an attribute
             * with a name that was not a valid XML name.  The substitution text
             * contains the name.
             */
            {ErrorMsg.ILLEGAL_ATTR_NAME_ERR, "Du kan inte anropa attributet ''{0}''"},

            /*
             * Note to translators:  The children of the outermost element of a
             * stylesheet are referred to as top-level elements.  No text should
             * occur within that outermost element unless it is within a top-level
             * element.  This message indicates that that constraint was violated.
             * "<xsl:stylesheet>" is a keyword that should not be translated.
             */
            {
                ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
                "Textdata utanf\u00F6r toppniv\u00E5elementet <xsl:stylesheet>."
            },

            /*
             * Note to translators:  JAXP is an acronym for the Java API for XML
             * Processing.  This message indicates that the XML parser provided to
             * XSLTC to process the XML input document had a configuration problem.
             */
            {ErrorMsg.SAX_PARSER_CONFIG_ERR, "JAXP-parser har inte konfigurerats korrekt"},

            /*
             * Note to translators:  The substitution text names the internal error
             * encountered.
             */
            {ErrorMsg.INTERNAL_ERR, "O\u00E5terkalleligt internt XSLTC-fel: ''{0}''"},

            /*
             * Note to translators:  The stylesheet contained an element that was
             * not recognized as part of the XSL syntax.  The substitution text
             * gives the element name.
             */
            {ErrorMsg.UNSUPPORTED_XSL_ERR, "XSL-elementet ''{0}'' st\u00F6ds inte."},

            /*
             * Note to translators:  The stylesheet referred to an extension to the
             * XSL syntax and indicated that it was defined by XSLTC, but XSTLC does
             * not recognized the particular extension named.  The substitution text
             * gives the extension name.
             */
            {ErrorMsg.UNSUPPORTED_EXT_ERR, "XSLTC-till\u00E4gget ''{0}'' \u00E4r ok\u00E4nt."},

            /*
             * Note to translators:  The XML document given to XSLTC as a stylesheet
             * was not, in fact, a stylesheet.  XSLTC is able to detect that in this
             * case because the outermost element in the stylesheet has to be
             * declared with respect to the XSL namespace URI, but no declaration
             * for that namespace was seen.
             */
            {
                ErrorMsg.MISSING_XSLT_URI_ERR,
                "Indatadokumentet \u00E4r ingen formatmall (XSL-namnrymden har inte deklarerats i"
                    + " rotelementet)."
            },

            /*
             * Note to translators:  XSLTC could not find the stylesheet document
             * with the name specified by the substitution text.
             */
            {ErrorMsg.MISSING_XSLT_TARGET_ERR, "Hittade inte formatmallen ''{0}''."},

            /*
             * Note to translators:  access to the stylesheet target is denied
             */
            {
                ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
                "Kunde inte l\u00E4sa formatmallen ''{0}'', eftersom ''{1}''-\u00E5tkomst inte"
                    + " till\u00E5ts p\u00E5 grund av begr\u00E4nsning som anges av egenskapen"
                    + " accessExternalStylesheet."
            },

            /*
             * Note to translators:  This message represents an internal error in
             * condition in XSLTC.  The substitution text is the class name in XSLTC
             * that is missing some functionality.
             */
            {ErrorMsg.NOT_IMPLEMENTED_ERR, "Inte implementerad: ''{0}''."},

            /*
             * Note to translators:  The XML document given to XSLTC as a stylesheet
             * was not, in fact, a stylesheet.
             */
            {ErrorMsg.NOT_STYLESHEET_ERR, "Indatadokumentet inneh\u00E5ller ingen XSL-formatmall."},

            /*
             * Note to translators:  The element named in the substitution text was
             * encountered in the stylesheet but is not recognized.
             */
            {ErrorMsg.ELEMENT_PARSE_ERR, "Kunde inte tolka elementet ''{0}''"},

            /*
             * Note to translators:  "use", "<key>", "node", "node-set", "string"
             * and "number" are keywords in this context and should not be
             * translated.  This message indicates that the value of the "use"
             * attribute was not one of the permitted values.
             */
            {
                ErrorMsg.KEY_USE_ATTR_ERR,
                "use-attribut f\u00F6r <key> m\u00E5ste vara node, node-set, string eller number."
            },

            /*
             * Note to translators:  An XML document can specify the version of the
             * XML specification to which it adheres.  This message indicates that
             * the version specified for the output document was not valid.
             */
            {ErrorMsg.OUTPUT_VERSION_ERR, "XML-dokumentets utdataversion m\u00E5ste vara 1.0"},

            /*
             * Note to translators:  The operator in a comparison operation was
             * not recognized.
             */
            {ErrorMsg.ILLEGAL_RELAT_OP_ERR, "Ok\u00E4nd operator f\u00F6r relationsuttryck"},

            /*
             * Note to translators:  An attribute set defines as a set of XML
             * attributes that can be added to an element in the output XML document
             * as a group.  This message is reported if the name specified was not
             * used to declare an attribute set.  The substitution text is the name
             * that is in error.
             */
            {
                ErrorMsg.ATTRIBSET_UNDEF_ERR,
                "F\u00F6rs\u00F6ker anv\u00E4nda en icke-befintlig attributupps\u00E4ttning"
                    + " ''{0}''."
            },

            /*
             * Note to translators:  The term "attribute value template" is a term
             * defined by XSLT which describes the value of an attribute that is
             * determined by an XPath expression.  The message indicates that the
             * expression was syntactically incorrect; the substitution text
             * contains the expression that was in error.
             */
            {ErrorMsg.ATTR_VAL_TEMPLATE_ERR, "Kan inte tolka attributv\u00E4rdemallen ''{0}''."},

            /*
             * Note to translators:  ???
             */
            {
                ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
                "Ok\u00E4nd datatyp i signaturen f\u00F6r klassen ''{0}''."
            },

            /*
             * Note to translators:  The substitution text refers to data types.
             * The message is displayed if a value in a particular context needs to
             * be converted to type {1}, but that's not possible for a value of
             * type {0}.
             */
            {ErrorMsg.DATA_CONVERSION_ERR, "Kan inte konvertera datatyp ''{0}'' till ''{1}''."},

            /*
             * Note to translators:  "Templates" is a Java class name that should
             * not be translated.
             */
            {
                ErrorMsg.NO_TRANSLET_CLASS_ERR,
                "Templates inneh\u00E5ller inte n\u00E5gon giltig klassdefinition f\u00F6r"
                    + " translet."
            },

            /*
             * Note to translators:  "Templates" is a Java class name that should
             * not be translated.
             */
            {
                ErrorMsg.NO_MAIN_TRANSLET_ERR,
                "Templates inneh\u00E5ller inte n\u00E5gon klass med namnet ''{0}''."
            },

            /*
             * Note to translators:  The substitution text is the name of a class.
             */
            {ErrorMsg.TRANSLET_CLASS_ERR, "Kunde inte ladda translet-klassen ''{0}''."},
            {
                ErrorMsg.TRANSLET_OBJECT_ERR,
                "Translet-klassen har laddats, men kan inte skapa instans av translet."
            },

            /*
             * Note to translators:  "ErrorListener" is a Java interface name that
             * should not be translated.  The message indicates that the user tried
             * to set an ErrorListener object on object of the class named in the
             * substitution text with "null" Java value.
             */
            {
                ErrorMsg.ERROR_LISTENER_NULL_ERR,
                "F\u00F6rs\u00F6ker st\u00E4lla in ErrorListener f\u00F6r ''{0}'' p\u00E5 null"
            },

            /*
             * Note to translators:  StreamSource, SAXSource and DOMSource are Java
             * interface names that should not be translated.
             */
            {
                ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
                "Endast StreamSource, SAXSource och DOMSource st\u00F6ds av XSLTC"
            },

            /*
             * Note to translators:  "Source" is a Java class name that should not
             * be translated.  The substitution text is the name of Java method.
             */
            {
                ErrorMsg.JAXP_NO_SOURCE_ERR,
                "Source-objektet som \u00F6verf\u00F6rdes till ''{0}'' saknar inneh\u00E5ll."
            },

            /*
             * Note to translators:  The message indicates that XSLTC failed to
             * compile the stylesheet into a translet (class file).
             */
            {ErrorMsg.JAXP_COMPILE_ERR, "Kunde inte kompilera formatmall"},

            /*
             * Note to translators:  "TransformerFactory" is a class name.  In this
             * context, an attribute is a property or setting of the
             * TransformerFactory object.  The substitution text is the name of the
             * unrecognised attribute.  The method used to retrieve the attribute is
             * "getAttribute", so it's not clear whether it would be best to
             * translate the term "attribute".
             */
            {
                ErrorMsg.JAXP_INVALID_ATTR_ERR,
                "TransformerFactory k\u00E4nner inte igen attributet ''{0}''."
            },
            {
                ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
                "Fel v\u00E4rde har angetts f\u00F6r attributet ''{0}''."
            },

            /*
             * Note to translators:  "setResult()" and "startDocument()" are Java
             * method names that should not be translated.
             */
            {
                ErrorMsg.JAXP_SET_RESULT_ERR,
                "setResult() m\u00E5ste anropas f\u00F6re startDocument()."
            },

            /*
             * Note to translators:  "Transformer" is a Java interface name that
             * should not be translated.  A Transformer object should contained a
             * reference to a translet object in order to be used for
             * transformations; this message is produced if that requirement is not
             * met.
             */
            {
                ErrorMsg.JAXP_NO_TRANSLET_ERR,
                "Transformer saknar inkapslat objekt f\u00F6r translet."
            },

            /*
             * Note to translators:  The XML document that results from a
             * transformation needs to be sent to an output handler object; this
             * message is produced if that requirement is not met.
             */
            {
                ErrorMsg.JAXP_NO_HANDLER_ERR,
                "Det finns ingen definierad utdatahanterare f\u00F6r transformeringsresultat."
            },

            /*
             * Note to translators:  "Result" is a Java interface name in this
             * context.  The substitution text is a method name.
             */
            {
                ErrorMsg.JAXP_NO_RESULT_ERR,
                "Result-objekt som \u00F6verf\u00F6rdes till ''{0}'' \u00E4r ogiltigt."
            },

            /*
             * Note to translators:  "Transformer" is a Java interface name.  The
             * user's program attempted to access an unrecognized property with the
             * name specified in the substitution text.  The method used to retrieve
             * the property is "getOutputProperty", so it's not clear whether it
             * would be best to translate the term "property".
             */
            {
                ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
                "F\u00F6rs\u00F6ker f\u00E5 \u00E5tkomst till ogiltig Transformer-egenskap,"
                    + " ''{0}''."
            },

            /*
             * Note to translators:  SAX2DOM is the name of a Java class that should
             * not be translated.  This is an adapter in the sense that it takes a
             * DOM object and converts it to something that uses the SAX API.
             */
            {ErrorMsg.SAX2DOM_ADAPTER_ERR, "Kunde inte skapa SAX2DOM-adapter: ''{0}''."},

            /*
             * Note to translators:  "XSLTCSource.build()" is a Java method name.
             * "systemId" is an XML term that is short for "system identification".
             */
            {ErrorMsg.XSLTC_SOURCE_ERR, "XSLTCSource.build() anropades utan angivet systemId."},
            {ErrorMsg.ER_RESULT_NULL, "Result borde inte vara null"},

            /*
             * Note to translators:  This message indicates that the value argument
             * of setParameter must be a valid Java Object.
             */
            {
                ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
                "Parameterv\u00E4rdet f\u00F6r {0} m\u00E5ste vara giltigt Java-objekt"
            },
            {
                ErrorMsg.COMPILE_STDIN_ERR,
                "Alternativet -i m\u00E5ste anv\u00E4ndas med alternativet -o."
            },

            /*
             * Note to translators:  This message contains usage information for a
             * means of invoking XSLTC from the command-line.  The message is
             * formatted for presentation in English.  The strings <output>,
             * <directory>, etc. indicate user-specified argument values, and can
             * be translated - the argument <package> refers to a Java package, so
             * it should be handled in the same way the term is handled for JDK
             * documentation.
             */
            {
                ErrorMsg.COMPILE_USAGE_STR,
                "SYNOPSIS\n"
                    + "   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o"
                    + " <utdata>]\n"
                    + "      [-d <katalog>] [-j <jarfile>] [-p <paket>]\n"
                    + "      [-n] [-x] [-u] [-v] [-h] { <formatmall> | -i }\n\n"
                    + "ALTERNATIV\n"
                    + "   -o <utdata>    tilldelar namnet <utdata> till genererad\n"
                    + "                  translet. Som standard tas namnet p\u00E5 translet\n"
                    + "                  fr\u00E5n namnet p\u00E5 <formatmallen>. Alternativet\n"
                    + "                  ignoreras vid kompilering av flera formatmallar.\n"
                    + "   -d <katalog> anger en destinationskatalog f\u00F6r translet\n"
                    + "   -j <jarfile>   paketerar transletklasserna i en jar-fil med\n"
                    + "                  namnet <jarfile>\n"
                    + "   -p <paket>   anger ett paketnamnprefix f\u00F6r alla genererade\n"
                    + "                  transletklasser.\n"
                    + "   -n             aktiverar mallinfogning (ger ett b\u00E4ttre"
                    + " genomsnittligt\n"
                    + "                  standardbeteende).\n"
                    + "   -x             ger ytterligare fels\u00F6kningsmeddelanden\n"
                    + "   -u             tolkar argument i <formatmall> som URL:er\n"
                    + "   -i             tvingar kompilatorn att l\u00E4sa formatmallen fr\u00E5n"
                    + " stdin\n"
                    + "   -v             skriver ut kompilatorns versionsnummer\n"
                    + "   -h             skriver ut denna syntaxsats\n"
            },

            /*
             * Note to translators:  This message contains usage information for a
             * means of invoking XSLTC from the command-line.  The message is
             * formatted for presentation in English.  The strings <jarfile>,
             * <document>, etc. indicate user-specified argument values, and can
             * be translated - the argument <class> refers to a Java class, so it
             * should be handled in the same way the term is handled for JDK
             * documentation.
             */
            {
                ErrorMsg.TRANSFORM_USAGE_STR,
                "SYNOPSIS \n"
                    + "   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j"
                    + " <jarfile>]\n"
                    + "      [-x] [-n <iterationer>] {-u <dokument_url> | <dokument>}\n"
                    + "      <klass> [<param1>=<v\u00E4rde1> ...]\n\n"
                    + "   anv\u00E4nder translet <klass> vid transformering av XML-dokument \n"
                    + "   angivna som <dokument>. Translet-<klass> finns antingen i\n"
                    + "   anv\u00E4ndarens CLASSPATH eller i valfritt angiven <jarfile>.\n"
                    + "ALTERNATIV\n"
                    + "   -j <jarfile>    anger en jar-fil varifr\u00E5n translet laddas\n"
                    + "   -x              ger ytterligare fels\u00F6kningsmeddelanden\n"
                    + "   -n <iterationer> k\u00F6r <iterations>-tider vid transformering och\n"
                    + "                   visar profileringsinformation\n"
                    + "   -u <dokument_url> anger XML-indatadokument som URL\n"
            },

            /*
             * Note to translators:  "<xsl:sort>", "<xsl:for-each>" and
             * "<xsl:apply-templates>" are keywords that should not be translated.
             * The message indicates that an xsl:sort element must be a child of
             * one of the other kinds of elements mentioned.
             */
            {
                ErrorMsg.STRAY_SORT_ERR,
                "<xsl:sort> kan anv\u00E4ndas endast i <xsl:for-each> eller <xsl:apply-templates>."
            },

            /*
             * Note to translators:  The message indicates that the encoding
             * requested for the output document was on that requires support that
             * is not available from the Java Virtual Machine being used to execute
             * the program.
             */
            {ErrorMsg.UNSUPPORTED_ENCODING, "Utdatakodning ''{0}'' underst\u00F6ds inte i JVM."},

            /*
             * Note to translators:  The message indicates that the XPath expression
             * named in the substitution text was not well formed syntactically.
             */
            {ErrorMsg.SYNTAX_ERR, "Syntaxfel i ''{0}''."},

            /*
             * Note to translators:  The substitution text is the name of a Java
             * class.  The term "constructor" here is the Java term.  The message is
             * displayed if XSLTC could not find a constructor for the specified
             * class.
             */
            {ErrorMsg.CONSTRUCTOR_NOT_FOUND, "Hittar inte den externa konstruktorn ''{0}''."},

            /*
             * Note to translators:  "static" is the Java keyword.  The substitution
             * text is the name of a function.  The first argument of that function
             * is not of the required type.
             */
            {
                ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
                "Det f\u00F6rsta argumentet f\u00F6r den icke-statiska Java-funktionen ''{0}''"
                    + " \u00E4r inte n\u00E5gon giltig objektreferens."
            },

            /*
             * Note to translators:  An XPath expression was not of the type
             * required in a particular context.  The substitution text is the
             * expression that was in error.
             */
            {ErrorMsg.TYPE_CHECK_ERR, "Fel vid kontroll av typ av uttrycket ''{0}''."},

            /*
             * Note to translators:  An XPath expression was not of the type
             * required in a particular context.  However, the location of the
             * problematic expression is unknown.
             */
            {
                ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
                "Fel vid kontroll av typ av ett uttryck p\u00E5 ok\u00E4nd plats."
            },

            /*
             * Note to translators:  The substitution text is the name of a command-
             * line option that was not recognized.
             */
            {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR, "Ogiltigt kommandoradsalternativ: ''{0}''."},

            /*
             * Note to translators:  The substitution text is the name of a command-
             * line option.
             */
            {
                ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
                "Kommandoradsalternativet ''{0}'' saknar obligatoriskt argument."
            },

            /*
             * Note to translators:  This message is used to indicate the severity
             * of another message.  The substitution text contains two error
             * messages.  The spacing before the second substitution text indents
             * it the same amount as the first in English.
             */
            {ErrorMsg.WARNING_PLUS_WRAPPED_MSG, "WARNING:  ''{0}''\n       :{1}"},

            /*
             * Note to translators:  This message is used to indicate the severity
             * of another message.  The substitution text is an error message.
             */
            {ErrorMsg.WARNING_MSG, "WARNING:  ''{0}''"},

            /*
             * Note to translators:  This message is used to indicate the severity
             * of another message.  The substitution text contains two error
             * messages.  The spacing before the second substitution text indents
             * it the same amount as the first in English.
             */
            {ErrorMsg.FATAL_ERR_PLUS_WRAPPED_MSG, "FATAL ERROR:  ''{0}''\n           :{1}"},

            /*
             * Note to translators:  This message is used to indicate the severity
             * of another message.  The substitution text is an error message.
             */
            {ErrorMsg.FATAL_ERR_MSG, "FATAL ERROR:  ''{0}''"},

            /*
             * Note to translators:  This message is used to indicate the severity
             * of another message.  The substitution text contains two error
             * messages.  The spacing before the second substitution text indents
             * it the same amount as the first in English.
             */
            {ErrorMsg.ERROR_PLUS_WRAPPED_MSG, "ERROR:  ''{0}''\n     :{1}"},

            /*
             * Note to translators:  This message is used to indicate the severity
             * of another message.  The substitution text is an error message.
             */
            {ErrorMsg.ERROR_MSG, "ERROR:  ''{0}''"},

            /*
             * Note to translators:  The substitution text is the name of a class.
             */
            {ErrorMsg.TRANSFORM_WITH_TRANSLET_STR, "Transformering via translet ''{0}'' "},

            /*
             * Note to translators:  The first substitution is the name of a class,
             * while the second substitution is the name of a jar file.
             */
            {
                ErrorMsg.TRANSFORM_WITH_JAR_STR,
                "Transformering via translet ''{0}'' fr\u00E5n jar-filen ''{1}''"
            },

            /*
             * Note to translators:  "TransformerFactory" is the name of a Java
             * interface and must not be translated.  The substitution text is
             * the name of the class that could not be instantiated.
             */
            {
                ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
                "Kunde inte skapa en instans av TransformerFactory-klassen ''{0}''."
            },

            /*
             * Note to translators:  This message is produced when the user
             * specified a name for the translet class that contains characters
             * that are not permitted in a Java class name.  The substitution
             * text "{0}" specifies the name the user requested, while "{1}"
             * specifies the name the processor used instead.
             */
            {
                ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
                "''{0}'' kunde inte anv\u00E4ndas som namn p\u00E5 transletklassen eftersom det"
                    + " inneh\u00E5ller otill\u00E5tna tecken f\u00F6r Java-klassnamn. Namnet"
                    + " ''{1}'' anv\u00E4ndes ist\u00E4llet."
            },

            /*
             * Note to translators:  The following message is used as a header.
             * All the error messages are collected together and displayed beneath
             * this message.
             */
            {ErrorMsg.COMPILER_ERROR_KEY, "Kompileringsfel:"},

            /*
             * Note to translators:  The following message is used as a header.
             * All the warning messages are collected together and displayed
             * beneath this message.
             */
            {ErrorMsg.COMPILER_WARNING_KEY, "Kompileringsvarningar:"},

            /*
             * Note to translators:  The following message is used as a header.
             * All the error messages that are produced when the stylesheet is
             * applied to an input document are collected together and displayed
             * beneath this message.  A 'translet' is the compiled form of a
             * stylesheet (see above).
             */
            {ErrorMsg.RUNTIME_ERROR_KEY, "Transletfel:"},

            /*
             * Note to translators:  An attribute whose value is constrained to
             * be a "QName" or a list of "QNames" had a value that was incorrect.
             * 'QName' is an XML syntactic term that must not be translated.  The
             * substitution text contains the actual value of the attribute.
             */
            {
                ErrorMsg.INVALID_QNAME_ERR,
                "Ett attribut vars v\u00E4rde m\u00E5ste vara ett QName eller en"
                    + " blankteckenavgr\u00E4nsad lista med QNames hade v\u00E4rdet ''{0}''"
            },

            /*
             * Note to translators:  An attribute whose value is required to
             * be an "NCName".
             * 'NCName' is an XML syntactic term that must not be translated.  The
             * substitution text contains the actual value of the attribute.
             */
            {
                ErrorMsg.INVALID_NCNAME_ERR,
                "Ett attribut vars v\u00E4rde m\u00E5ste vara ett NCName hade v\u00E4rdet ''{0}''"
            },

            /*
             * Note to translators:  An attribute with an incorrect value was
             * encountered.  The permitted value is one of the literal values
             * "xml", "html" or "text"; it is also permitted to have the form of
             * a QName that is not also an NCName.  The terms "method",
             * "xsl:output", "xml", "html" and "text" are keywords that must not
             * be translated.  The term "qname-but-not-ncname" is an XML syntactic
             * term.  The substitution text contains the actual value of the
             * attribute.
             */
            {
                ErrorMsg.INVALID_METHOD_IN_OUTPUT,
                "Metodattributet f\u00F6r ett <xsl:output>-element hade v\u00E4rdet ''{0}''. Endast"
                    + " n\u00E5got av f\u00F6ljande v\u00E4rden kan anv\u00E4ndas: ''xml'',"
                    + " ''html'', ''text'' eller qname-but-not-ncname i XML"
            },
            {
                ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
                "Funktionsnamnet kan inte vara null i TransformerFactory.getFeature(namn p\u00E5"
                    + " str\u00E4ng)."
            },
            {
                ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
                "Funktionsnamnet kan inte vara null i TransformerFactory.setFeature(namn p\u00E5"
                    + " str\u00E4ng, booleskt v\u00E4rde)."
            },
            {
                ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
                "Kan inte st\u00E4lla in funktionen ''{0}'' i denna TransformerFactory."
            },
            {
                ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
                "FEATURE_SECURE_PROCESSING: Funktionen kan inte anges till false om"
                    + " s\u00E4kerhetshanteraren anv\u00E4nds."
            },

            /*
             * Note to translators:  This message describes an internal error in the
             * processor.  The term "byte code" is a Java technical term for the
             * executable code in a Java method, and "try-catch-finally block"
             * refers to the Java keywords with those names.  "Outlined" is a
             * technical term internal to XSLTC and should not be translated.
             */
            {
                ErrorMsg.OUTLINE_ERR_TRY_CATCH,
                "Internt XSLTC-fel: den genererade bytekoden inneh\u00E5ller ett"
                    + " try-catch-finally-block och kan inte g\u00F6ras till en disposition."
            },

            /*
             * Note to translators:  This message describes an internal error in the
             * processor.  The terms "OutlineableChunkStart" and
             * "OutlineableChunkEnd" are the names of classes internal to XSLTC and
             * should not be translated.  The message indicates that for every
             * "start" there must be a corresponding "end", and vice versa, and
             * that if one of a pair of "start" and "end" appears between another
             * pair of corresponding "start" and "end", then the other half of the
             * pair must also be between that same enclosing pair.
             */
            {
                ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
                "Internt XSLTC-fel: mark\u00F6rerna OutlineableChunkStart och OutlineableChunkEnd"
                    + " m\u00E5ste vara balanserade och korrekt kapslade."
            },

            /*
             * Note to translators:  This message describes an internal error in the
             * processor.  The term "byte code" is a Java technical term for the
             * executable code in a Java method.  The "method" that is being
             * referred to is a Java method in a translet that XSLTC is generating
             * in processing a stylesheet.  The "instruction" that is being
             * referred to is one of the instrutions in the Java byte code in that
             * method.  "Outlined" is a technical term internal to XSLTC and
             * should not be translated.
             */
            {
                ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
                "Internt XSLTC-fel: originalmetoden refererar fortfarande till en instruktion som"
                    + " var en del av ett bytekodsblock som gjordes till en disposition."
            },

            /*
             * Note to translators:  This message describes an internal error in the
             * processor.  The "method" that is being referred to is a Java method
             * in a translet that XSLTC is generating.
             *
             */
            {
                ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
                "Internt XSLTC-fel: en metod i transleten \u00F6verstiger Java Virtual Machines"
                    + " l\u00E4ngdbegr\u00E4nsning f\u00F6r en metod p\u00E5 64 kilobytes.  Det"
                    + " h\u00E4r orsakas vanligen av mycket stora mallar i en formatmall."
                    + " F\u00F6rs\u00F6k att omstrukturera formatmallen att anv\u00E4nda mindre"
                    + " mallar."
            },
            {
                ErrorMsg.DESERIALIZE_TRANSLET_ERR,
                "N\u00E4r Java-s\u00E4kerheten \u00E4r aktiverad \u00E4r st\u00F6det f\u00F6r"
                    + " avserialisering av TemplatesImpl avaktiverat. Du kan \u00E5sidos\u00E4tta"
                    + " det h\u00E4r genom att st\u00E4lla in systemegenskapen"
                    + " jdk.xml.enableTemplatesImplDeserialization till sant."
            }
        };
    }
}
