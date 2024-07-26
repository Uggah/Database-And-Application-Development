package de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading;

/*-
 * #%L
 * DBWarp
 * %%
 * Copyright (C) 2024 Kay Knöpfle, Lucca Greschner and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 * Factory class for creating XML syntax validators. This class provides a method to create a
 * {@link Validator} instance configured with a specific XML Schema Definition (XSD) for validating
 * XML syntaxes.
 */
public class SyntaxValidatorFactory {

  /**
   * Creates a new {@link Validator} instance configured with the XML Schema located at
   * "/syntaxes/syntax.xsd" within the classpath.
   *
   * @return A {@link Validator} instance for validating XML documents against the specified XSD.
   * @throws SAXException If any SAX errors occur during the creation of the validator.
   */
  public Validator createValidator() throws SAXException {
    final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

    Source schemaFile = new StreamSource(getClass().getResourceAsStream("/syntaxes/schema.xsd"));
    Schema schema = factory.newSchema(schemaFile);

    return schema.newValidator();
  }

}
