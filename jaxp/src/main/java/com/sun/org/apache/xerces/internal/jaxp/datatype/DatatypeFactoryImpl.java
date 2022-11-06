/*
 * reserved comment block
 * DO NOT REMOVE OR ALTER!
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

package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Factory that creates new <code>javax.xml.datatype</code> <code>Object</code>s that map XML
 * to/from Java <code>Object</code>s.
 *
 * <p id="DatatypeFactory.newInstance">{@link #newInstance()} is used to create a new <code>
 * DatatypeFactory</code>. The following implementation resolution mechanisms are used in the
 * following order:
 *
 * <ol>
 *   <li>If the system property specified by {@link #DATATYPEFACTORY_PROPERTY}, "<code>
 *       javax.xml.datatype.DatatypeFactory</code>", exists, a class with the name of the property's
 *       value is instantiated. Any Exception thrown during the instantiation process is wrapped as
 *       a {@link DatatypeConfigurationException}.
 *   <li>If the file ${JAVA_HOME}/conf/jaxp.properties exists, it is loaded in a {@link
 *       java.util.Properties} <code>Object</code>. The <code>Properties</code> <code>Object </code>
 *       is then queried for the property as documented in the prior step and processed as
 *       documented in the prior step.
 *   <li>The services resolution mechanism is used, e.g. <code>
 *       META-INF/services/java.xml.datatype.DatatypeFactory</code>. Any Exception thrown during the
 *       instantiation process is wrapped as a {@link DatatypeConfigurationException}.
 *   <li>The final mechanism is to attempt to instantiate the <code>Class</code> specified by {@link
 *       #DATATYPEFACTORY_IMPLEMENTATION_CLASS}, "<code>javax.xml.datatype.DatatypeFactoryImpl
 *       </code>". Any Exception thrown during the instantiation process is wrapped as a {@link
 *       DatatypeConfigurationException}.
 * </ol>
 *
 * @author Joseph Fialli
 * @author Jeff Suttor
 */
public class DatatypeFactoryImpl extends DatatypeFactory {

    /**
     * Public constructor is empty..
     *
     * <p>Use {@link DatatypeFactory#newInstance()} to create a <code>DatatypeFactory</code>.
     */
    public DatatypeFactoryImpl() {}

    /**
     * Obtain a new instance of a <code>Duration</code> specifying the <code>Duration</code> as its
     * string representation, "PnYnMnDTnHnMnS", as defined in XML Schema 1.0 section 3.2.6.1.
     *
     * <p>XML Schema Part 2: Datatypes, 3.2.6 duration, defines <code>duration</code> as:
     *
     * <blockquote>
     *
     * duration represents a duration of time. The value space of duration is a six-dimensional
     * space where the coordinates designate the Gregorian year, month, day, hour, minute, and
     * second components defined in Section 5.5.3.2 of [ISO 8601], respectively. These components
     * are ordered in their significance by their order of appearance i.e. as year, month, day,
     * hour, minute, and second.
     *
     * </blockquote>
     *
     * <p>All six values are set and availabe from the created {@link Duration}
     *
     * <p>The XML Schema specification states that values can be of an arbitrary size.
     * Implementations may chose not to or be incapable of supporting arbitrarily large and/or small
     * values. An {@link UnsupportedOperationException} will be thrown with a message indicating
     * implementation limits if implementation capacities are exceeded.
     *
     * @param lexicalRepresentation <code>String</code> representation of a <code>Duration</code>.
     * @return New <code>Duration</code> created from parsing the <code>lexicalRepresentation</code>
     *     .
     * @throws IllegalArgumentException If <code>lexicalRepresentation</code> is not a valid
     *     representation of a <code>Duration</code>.
     * @throws UnsupportedOperationException If implementation cannot support requested values.
     * @throws NullPointerException if <code>lexicalRepresentation</code> is <code>null</code>.
     */
    public Duration newDuration(final String lexicalRepresentation) {

        return new DurationImpl(lexicalRepresentation);
    }

    /**
     * Obtain a new instance of a <code>Duration</code> specifying the <code>Duration</code> as
     * milliseconds.
     *
     * <p>XML Schema Part 2: Datatypes, 3.2.6 duration, defines <code>duration</code> as:
     *
     * <blockquote>
     *
     * duration represents a duration of time. The value space of duration is a six-dimensional
     * space where the coordinates designate the Gregorian year, month, day, hour, minute, and
     * second components defined in Section 5.5.3.2 of [ISO 8601], respectively. These components
     * are ordered in their significance by their order of appearance i.e. as year, month, day,
     * hour, minute, and second.
     *
     * </blockquote>
     *
     * <p>All six values are set by computing their values from the specified milliseconds and are
     * availabe using the <code>get</code> methods of the created {@link Duration}. The values
     * conform to and are defined by:
     *
     * <ul>
     *   <li>ISO 8601:2000(E) Section 5.5.3.2 Alternative format
     *   <li><a href="http://www.w3.org/TR/xmlschema-2/#isoformats">W3C XML Schema 1.0 Part 2,
     *       Appendix D, ISO 8601 Date and Time Formats</a>
     *   <li>{@link XMLGregorianCalendar} Date/Time Datatype Field Mapping Between XML Schema 1.0
     *       and Java Representation
     * </ul>
     *
     * <p>The default start instance is defined by {@link GregorianCalendar}'s use of the start of
     * the epoch: i.e., {@link java.util.Calendar#YEAR} = 1970, {@link java.util.Calendar#MONTH} =
     * {@link java.util.Calendar#JANUARY}, {@link java.util.Calendar#DATE} = 1, etc. This is
     * important as there are variations in the Gregorian Calendar, e.g. leap years have different
     * days in the month = {@link java.util.Calendar#FEBRUARY} so the result of {@link
     * Duration#getMonths()} and {@link Duration#getDays()} can be influenced.
     *
     * @param durationInMilliseconds Duration in milliseconds to create.
     * @return New <code>Duration</code> representing <code>durationInMilliseconds</code>.
     */
    public Duration newDuration(final long durationInMilliseconds) {

        return new DurationImpl(durationInMilliseconds);
    }

    /**
     * Obtain a new instance of a <code>Duration</code> specifying the <code>Duration</code> as
     * isPositive, years, months, days, hours, minutes, seconds.
     *
     * <p>The XML Schema specification states that values can be of an arbitrary size.
     * Implementations may chose not to or be incapable of supporting arbitrarily large and/or small
     * values. An {@link UnsupportedOperationException} will be thrown with a message indicating
     * implementation limits if implementation capacities are exceeded.
     *
     * @param isPositive Set to <code>false</code> to create a negative duration. When the length of
     *     the duration is zero, this parameter will be ignored.
     * @param years of this <code>Duration</code>
     * @param months of this <code>Duration</code>
     * @param days of this <code>Duration</code>
     * @param hours of this <code>Duration</code>
     * @param minutes of this <code>Duration</code>
     * @param seconds of this <code>Duration</code>
     * @return New <code>Duration</code> created from the specified values.
     * @throws IllegalArgumentException If values are not a valid representation of a <code>Duration
     *     </code>.
     * @throws UnsupportedOperationException If implementation cannot support requested values.
     * @throws NullPointerException If any values are <code>null</code>.
     * @see #newDuration(boolean isPositive, BigInteger years, BigInteger months, BigInteger days,
     *     BigInteger hours, BigInteger minutes, BigDecimal seconds)
     */
    public Duration newDuration(
            final boolean isPositive,
            final BigInteger years,
            final BigInteger months,
            final BigInteger days,
            final BigInteger hours,
            final BigInteger minutes,
            final BigDecimal seconds) {

        return new DurationImpl(isPositive, years, months, days, hours, minutes, seconds);
    }
    /**
     * Create a <code>Duration</code> of type <code>xdt:yearMonthDuration</code> using the specified
     * <code>year</code> and <code>month</code> as defined in <a
     * href="http://www.w3.org/TR/xpath-datamodel#yearMonthDuration">XQuery 1.0 and XPath 2.0 Data
     * Model, xdt:yearMonthDuration</a>.
     *
     * <p>The XML Schema specification states that values can be of an arbitrary size.
     * Implementations may chose not to or be incapable of supporting arbitrarily large and/or small
     * values. An {@link UnsupportedOperationException} will be thrown with a message indicating
     * implementation limits if implementation capacities are exceeded.
     *
     * <p>A <code>null</code> value indicates that field is not set.
     *
     * @param isPositive Set to <code>false</code> to create a negative duration. When the length of
     *     the duration is zero, this parameter will be ignored.
     * @param year Year of <code>Duration</code>.
     * @param month Month of <code>Duration</code>.
     * @return New <code>Duration</code> created using the specified <code>year</code> and <code>
     *     month</code>.
     * @throws IllegalArgumentException If the values are not a valid representation of a <code>
     *     Duration</code>: if all of the fields (year, month) are null or if any of the fields is
     *     negative.
     * @throws UnsupportedOperationException If implementation cannot support requested values.
     */
    public Duration newDurationYearMonth(
            final boolean isPositive, final BigInteger year, final BigInteger month) {

        return new DurationYearMonthImpl(isPositive, year, month);
    }
    /**
     * Create a <code>Duration</code> of type <code>xdt:yearMonthDuration</code> using the specified
     * <code>year</code> and <code>month</code> as defined in <a
     * href="http://www.w3.org/TR/xpath-datamodel#yearMonthDuration">XQuery 1.0 and XPath 2.0 Data
     * Model, xdt:yearMonthDuration</a>.
     *
     * <p>A {@link DatatypeConstants#FIELD_UNDEFINED} value indicates that field is not set.
     *
     * @param isPositive Set to <code>false</code> to create a negative duration. When the length of
     *     the duration is zero, this parameter will be ignored.
     * @param year Year of <code>Duration</code>.
     * @param month Month of <code>Duration</code>.
     * @return New <code>Duration</code> created using the specified <code>year</code> and <code>
     *     month</code>.
     * @throws IllegalArgumentException If the values are not a valid representation of a <code>
     *     Duration</code>: if any of the fields (year, month) is negative.
     */
    @Override
    public Duration newDurationYearMonth(
            final boolean isPositive, final int year, final int month) {

        return new DurationYearMonthImpl(isPositive, year, month);
    }

    /**
     * Create a <code>Duration</code> of type <code>xdt:yearMonthDuration</code> by parsing its
     * <code>String</code> representation, "<em>PnYnM</em>", <a
     * href="http://www.w3.org/TR/xpath-datamodel#yearMonthDuration">XQuery 1.0 and XPath 2.0 Data
     * Model, xdt:yearMonthDuration</a>.
     *
     * <p>The datatype <code>xdt:yearMonthDuration</code> is a subtype of <code>xs:duration</code>
     * whose lexical representation contains only year and month components. This datatype resides
     * in the namespace {@link javax.xml.XMLConstants#W3C_XPATH_DATATYPE_NS_URI}.
     *
     * <p>Both values are set and availabe from the created {@link Duration}
     *
     * <p>The XML Schema specification states that values can be of an arbitrary size.
     * Implementations may chose not to or be incapable of supporting arbitrarily large and/or small
     * values. An {@link UnsupportedOperationException} will be thrown with a message indicating
     * implementation limits if implementation capacities are exceeded.
     *
     * @param lexicalRepresentation Lexical representation of a duration.
     * @return New <code>Duration</code> created using the specified <code>lexicalRepresentation
     *     </code>.
     * @throws IllegalArgumentException If <code>lexicalRepresentation</code> is not a valid
     *     representation of a <code>Duration</code> expressed only in terms of years and months.
     * @throws UnsupportedOperationException If implementation cannot support requested values.
     * @throws NullPointerException If <code>lexicalRepresentation</code> is <code>null</code>.
     */
    public Duration newDurationYearMonth(final String lexicalRepresentation) {

        return new DurationYearMonthImpl(lexicalRepresentation);
    }
    /**
     * Create a <code>Duration</code> of type <code>xdt:yearMonthDuration</code> using the specified
     * milliseconds as defined in <a href="http://www.w3.org/TR/xpath-datamodel#yearMonthDuration">
     * XQuery 1.0 and XPath 2.0 Data Model, xdt:yearMonthDuration</a>.
     *
     * <p>The datatype <code>xdt:yearMonthDuration</code> is a subtype of <code>xs:duration</code>
     * whose lexical representation contains only year and month components. This datatype resides
     * in the namespace {@link javax.xml.XMLConstants#W3C_XPATH_DATATYPE_NS_URI}.
     *
     * <p>Both values are set by computing their values from the specified milliseconds and are
     * availabe using the <code>get</code> methods of the created {@link Duration}. The values
     * conform to and are defined by:
     *
     * <ul>
     *   <li>ISO 8601:2000(E) Section 5.5.3.2 Alternative format
     *   <li><a href="http://www.w3.org/TR/xmlschema-2/#isoformats">W3C XML Schema 1.0 Part 2,
     *       Appendix D, ISO 8601 Date and Time Formats</a>
     *   <li>{@link XMLGregorianCalendar} Date/Time Datatype Field Mapping Between XML Schema 1.0
     *       and Java Representation
     * </ul>
     *
     * <p>The default start instance is defined by {@link GregorianCalendar}'s use of the start of
     * the epoch: i.e., {@link java.util.Calendar#YEAR} = 1970, {@link java.util.Calendar#MONTH} =
     * {@link java.util.Calendar#JANUARY}, {@link java.util.Calendar#DATE} = 1, etc. This is
     * important as there are variations in the Gregorian Calendar, e.g. leap years have different
     * days in the month = {@link java.util.Calendar#FEBRUARY} so the result of {@link
     * Duration#getMonths()} can be influenced.
     *
     * <p>Any remaining milliseconds after determining the year and month are discarded.
     *
     * @param durationInMilliseconds Milliseconds of <code>Duration</code> to create.
     * @return New <code>Duration</code> created using the specified <code>durationInMilliseconds
     *     </code>.
     */
    public Duration newDurationYearMonth(final long durationInMilliseconds) {

        return new DurationYearMonthImpl(durationInMilliseconds);
    }

    /**
     * Create a <code>Duration</code> of type <code>xdt:dayTimeDuration</code> by parsing its <code>
     * String</code> representation, "<em>PnDTnHnMnS</em>", <a
     * href="http://www.w3.org/TR/xpath-datamodel#dayTimeDuration">XQuery 1.0 and XPath 2.0 Data
     * Model, xdt:dayTimeDuration</a>.
     *
     * <p>The datatype <code>xdt:dayTimeDuration</code> is a subtype of <code>xs:duration</code>
     * whose lexical representation contains only day, hour, minute, and second components. This
     * datatype resides in the namespace <code>http://www.w3.org/2003/11/xpath-datatypes</code>.
     *
     * <p>All four values are set and availabe from the created {@link Duration}
     *
     * <p>The XML Schema specification states that values can be of an arbitrary size.
     * Implementations may chose not to or be incapable of supporting arbitrarily large and/or small
     * values. An {@link UnsupportedOperationException} will be thrown with a message indicating
     * implementation limits if implementation capacities are exceeded.
     *
     * @param lexicalRepresentation Lexical representation of a duration.
     * @return New <code>Duration</code> created using the specified <code>lexicalRepresentation
     *     </code>.
     * @throws IllegalArgumentException If <code>lexicalRepresentation</code> is not a valid
     *     representation of a <code>Duration</code> expressed only in terms of days and time.
     * @throws UnsupportedOperationException If implementation cannot support requested values.
     * @throws NullPointerException If <code>lexicalRepresentation</code> is <code>null</code>.
     */
    public Duration newDurationDayTime(final String lexicalRepresentation) {
        // lexicalRepresentation must be non-null
        if (lexicalRepresentation == null) {
            throw new NullPointerException(
                    "Trying to create an xdt:dayTimeDuration with an invalid"
                            + " lexical representation of \"null\"");
        }

        return new DurationDayTimeImpl(lexicalRepresentation);
    }

    /**
     * Create a <code>Duration</code> of type <code>xdt:dayTimeDuration</code> using the specified
     * milliseconds as defined in <a href="http://www.w3.org/TR/xpath-datamodel#dayTimeDuration">
     * XQuery 1.0 and XPath 2.0 Data Model, xdt:dayTimeDuration</a>.
     *
     * <p>The datatype <code>xdt:dayTimeDuration</code> is a subtype of <code>xs:duration</code>
     * whose lexical representation contains only day, hour, minute, and second components. This
     * datatype resides in the namespace <code>http://www.w3.org/2003/11/xpath-datatypes</code>.
     *
     * <p>All four values are set by computing their values from the specified milliseconds and are
     * availabe using the <code>get</code> methods of the created {@link Duration}. The values
     * conform to and are defined by:
     *
     * <ul>
     *   <li>ISO 8601:2000(E) Section 5.5.3.2 Alternative format
     *   <li><a href="http://www.w3.org/TR/xmlschema-2/#isoformats">W3C XML Schema 1.0 Part 2,
     *       Appendix D, ISO 8601 Date and Time Formats</a>
     *   <li>{@link XMLGregorianCalendar} Date/Time Datatype Field Mapping Between XML Schema 1.0
     *       and Java Representation
     * </ul>
     *
     * <p>The default start instance is defined by {@link GregorianCalendar}'s use of the start of
     * the epoch: i.e., {@link java.util.Calendar#YEAR} = 1970, {@link java.util.Calendar#MONTH} =
     * {@link java.util.Calendar#JANUARY}, {@link java.util.Calendar#DATE} = 1, etc. This is
     * important as there are variations in the Gregorian Calendar, e.g. leap years have different
     * days in the month = {@link java.util.Calendar#FEBRUARY} so the result of {@link
     * Duration#getDays()} can be influenced.
     *
     * <p>Any remaining milliseconds after determining the day, hour, minute and second are
     * discarded.
     *
     * @param durationInMilliseconds Milliseconds of <code>Duration</code> to create.
     * @return New <code>Duration</code> created with the specified <code>durationInMilliseconds
     *     </code>.
     * @see <a href="http://www.w3.org/TR/xpath-datamodel#dayTimeDuration">XQuery 1.0 and XPath 2.0
     *     Data Model, xdt:dayTimeDuration</a>
     */
    public Duration newDurationDayTime(final long durationInMilliseconds) {

        return new DurationDayTimeImpl(durationInMilliseconds);
    }

    /**
     * Create a <code>Duration</code> of type <code>xdt:dayTimeDuration</code> using the specified
     * <code>day</code>, <code>hour</code>, <code>minute</code> and <code>second</code> as defined
     * in <a href="http://www.w3.org/TR/xpath-datamodel#dayTimeDuration">XQuery 1.0 and XPath 2.0
     * Data Model, xdt:dayTimeDuration</a>.
     *
     * <p>The datatype <code>xdt:dayTimeDuration</code> is a subtype of <code>xs:duration</code>
     * whose lexical representation contains only day, hour, minute, and second components. This
     * datatype resides in the namespace <code>http://www.w3.org/2003/11/xpath-datatypes</code>.
     *
     * <p>The XML Schema specification states that values can be of an arbitrary size.
     * Implementations may chose not to or be incapable of supporting arbitrarily large and/or small
     * values. An {@link UnsupportedOperationException} will be thrown with a message indicating
     * implementation limits if implementation capacities are exceeded.
     *
     * <p>A <code>null</code> value indicates that field is not set.
     *
     * @param isPositive Set to <code>false</code> to create a negative duration. When the length of
     *     the duration is zero, this parameter will be ignored.
     * @param day Day of <code>Duration</code>.
     * @param hour Hour of <code>Duration</code>.
     * @param minute Minute of <code>Duration</code>.
     * @param second Second of <code>Duration</code>.
     * @return New <code>Duration</code> created with the specified <code>day</code>, <code>hour
     *     </code>, <code>minute</code> and <code>second</code>.
     * @throws IllegalArgumentException If the values are not a valid representation of a <code>
     *     Duration</code>: if all the fields (day, hour, ...) are null or if any of the fields is
     *     negative.
     * @throws UnsupportedOperationException If implementation cannot support requested values.
     */
    public Duration newDurationDayTime(
            final boolean isPositive,
            final BigInteger day,
            final BigInteger hour,
            final BigInteger minute,
            final BigInteger second) {

        return new DurationDayTimeImpl(
                isPositive, day, hour, minute, (second != null) ? new BigDecimal(second) : null);
    }

    /**
     * Create a <code>Duration</code> of type <code>xdt:dayTimeDuration</code> using the specified
     * <code>day</code>, <code>hour</code>, <code>minute</code> and <code>second</code> as defined
     * in <a href="http://www.w3.org/TR/xpath-datamodel#dayTimeDuration">XQuery 1.0 and XPath 2.0
     * Data Model, xdt:dayTimeDuration</a>.
     *
     * <p>The datatype <code>xdt:dayTimeDuration</code> is a subtype of <code>xs:duration</code>
     * whose lexical representation contains only day, hour, minute, and second components. This
     * datatype resides in the namespace <code>http://www.w3.org/2003/11/xpath-datatypes</code>.
     *
     * <p>A {@link DatatypeConstants#FIELD_UNDEFINED} value indicates that field is not set.
     *
     * @param isPositive Set to <code>false</code> to create a negative duration. When the length of
     *     the duration is zero, this parameter will be ignored.
     * @param day Day of <code>Duration</code>.
     * @param hour Hour of <code>Duration</code>.
     * @param minute Minute of <code>Duration</code>.
     * @param second Second of <code>Duration</code>.
     * @return New <code>Duration</code> created with the specified <code>day</code>, <code>hour
     *     </code>, <code>minute</code> and <code>second</code>.
     * @throws IllegalArgumentException If the values are not a valid representation of a <code>
     *     Duration</code>: if any of the fields (day, hour, ...) is negative.
     */
    public Duration newDurationDayTime(
            final boolean isPositive,
            final int day,
            final int hour,
            final int minute,
            final int second) {

        return new DurationDayTimeImpl(isPositive, day, hour, minute, second);
    }

    /**
     * Create a new instance of an <code>XMLGregorianCalendar</code>.
     *
     * <p>All date/time datatype fields set to {@link DatatypeConstants#FIELD_UNDEFINED} or null.
     *
     * @return New <code>XMLGregorianCalendar</code> with all date/time datatype fields set to
     *     {@link DatatypeConstants#FIELD_UNDEFINED} or null.
     */
    public XMLGregorianCalendar newXMLGregorianCalendar() {

        return new XMLGregorianCalendarImpl();
    }

    /**
     * Create a new XMLGregorianCalendar by parsing the String as a lexical representation.
     *
     * <p>Parsing the lexical string representation is defined in <a
     * href="http://www.w3.org/TR/xmlschema-2/#dateTime-order">XML Schema 1.0 Part 2, Section
     * 3.2.[7-14].1, <em>Lexical Representation</em>.</a>
     *
     * <p>The string representation may not have any leading and trailing whitespaces.
     *
     * <p>The parsing is done field by field so that the following holds for any lexically correct
     * String x:
     *
     * <pre>
     * newXMLGregorianCalendar(x).toXMLFormat().equals(x)
     * </pre>
     *
     * <p>Except for the noted lexical/canonical representation mismatches listed in <a
     * href="http://www.w3.org/2001/05/xmlschema-errata#e2-45">XML Schema 1.0 errata, Section
     * 3.2.7.2</a>.
     *
     * @param lexicalRepresentation Lexical representation of one the eight XML Schema date/time
     *     datatypes.
     * @return <code>XMLGregorianCalendar</code> created from the <code>lexicalRepresentation</code>
     *     .
     * @throws IllegalArgumentException If the <code>lexicalRepresentation</code> is not a valid
     *     <code>XMLGregorianCalendar</code>.
     * @throws NullPointerException If <code>lexicalRepresentation</code> is <code>null</code>.
     */
    public XMLGregorianCalendar newXMLGregorianCalendar(final String lexicalRepresentation) {

        return new XMLGregorianCalendarImpl(lexicalRepresentation);
    }

    /**
     * Create an <code>XMLGregorianCalendar</code> from a {@link GregorianCalendar}.
     *
     * <table border="2" rules="all" cellpadding="2">
     *   <thead>
     *     <tr>
     *       <th align="center" colspan="2">
     *          Field by Field Conversion from
     *          {@link GregorianCalendar} to an {@link XMLGregorianCalendar}
     *       </th>
     *     </tr>
     *     <tr>
     *        <th><code>java.util.GregorianCalendar</code> field</th>
     *        <th><code>javax.xml.datatype.XMLGregorianCalendar</code> field</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td><code>ERA == GregorianCalendar.BC ? -YEAR : YEAR</code></td>
     *       <td>{@link XMLGregorianCalendar#setYear(int year)}</td>
     *     </tr>
     *     <tr>
     *       <td><code>MONTH + 1</code></td>
     *       <td>{@link XMLGregorianCalendar#setMonth(int month)}</td>
     *     </tr>
     *     <tr>
     *       <td><code>DAY_OF_MONTH</code></td>
     *       <td>{@link XMLGregorianCalendar#setDay(int day)}</td>
     *     </tr>
     *     <tr>
     *       <td><code>HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND</code></td>
     *       <td>{@link XMLGregorianCalendar#setTime(int hour, int minute, int second, BigDecimal fractional)}</td>
     *     </tr>
     *     <tr>
     *       <td>
     *         <code>(ZONE_OFFSET + DST_OFFSET) / (60*1000)</code><br/>
     *         <em>(in minutes)</em>
     *       </td>
     *       <td>{@link XMLGregorianCalendar#setTimezone(int offset)}<sup><em>*</em></sup>
     *       </td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * <p><em>*</em>conversion loss of information. It is not possible to represent a <code>
     * java.util.GregorianCalendar</code> daylight savings timezone id in the XML Schema 1.0
     * date/time datatype representation.
     *
     * <p>To compute the return value's <code>TimeZone</code> field,
     *
     * <ul>
     *   <li>when <code>this.getTimezone() != FIELD_UNDEFINED</code>, create a <code>
     *       java.util.TimeZone</code> with a custom timezone id using the <code>this.getTimezone()
     *       </code>.
     *   <li>else use the <code>GregorianCalendar</code> default timezone value for the host is
     *       defined as specified by <code>java.util.TimeZone.getDefault()</code>.
     *
     * @param cal <code>java.util.GregorianCalendar</code> used to create <code>XMLGregorianCalendar
     *     </code>
     * @return <code>XMLGregorianCalendar</code> created from <code>java.util.GregorianCalendar
     *     </code>
     * @throws NullPointerException If <code>cal</code> is <code>null</code>.
     */
    public XMLGregorianCalendar newXMLGregorianCalendar(final GregorianCalendar cal) {

        return new XMLGregorianCalendarImpl(cal);
    }

    /**
     * Constructor allowing for complete value spaces allowed by W3C XML Schema 1.0 recommendation
     * for xsd:dateTime and related builtin datatypes. Note that <code>year</code> parameter
     * supports arbitrarily large numbers and fractionalSecond has infinite precision.
     *
     * @param year of <code>XMLGregorianCalendar</code> to be created.
     * @param month of <code>XMLGregorianCalendar</code> to be created.
     * @param day of <code>XMLGregorianCalendar</code> to be created.
     * @param hour of <code>XMLGregorianCalendar</code> to be created.
     * @param minute of <code>XMLGregorianCalendar</code> to be created.
     * @param second of <code>XMLGregorianCalendar</code> to be created.
     * @param fractionalSecond of <code>XMLGregorianCalendar</code> to be created.
     * @param timezone of <code>XMLGregorianCalendar</code> to be created.
     * @return <code>XMLGregorianCalendar</code> created from specified values.
     * @throws IllegalArgumentException If any individual parameter's value is outside the maximum
     *     value constraint for the field as determined by the Date/Time Data Mapping table in
     *     {@link XMLGregorianCalendar} or if the composite values constitute an invalid <code>
     *     XMLGregorianCalendar</code> instance as determined by {@link
     *     XMLGregorianCalendar#isValid()}.
     * @throws NullPointerException If any parameters are <code>null</code>.
     */
    public XMLGregorianCalendar newXMLGregorianCalendar(
            final BigInteger year,
            final int month,
            final int day,
            final int hour,
            final int minute,
            final int second,
            final BigDecimal fractionalSecond,
            final int timezone) {

        return new XMLGregorianCalendarImpl(
                year, month, day, hour, minute, second, fractionalSecond, timezone);
    }
}
