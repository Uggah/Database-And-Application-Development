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

import de.hdm_stuttgart.mi.dbad.dbwarp.config.Configuration;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.exception.SyntaxLoadException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ObjectFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Provides functionality to load database syntax configurations from XML files.
 * This class encapsulates the process of loading and validating syntax definitions for different database types,
 * ensuring that the syntax is securely parsed to prevent XML External Entity (XXE) attacks.
 */
public class SyntaxLoader {

  /**
   * Loads and returns the syntax for a specified database type.
   * This method first validates the syntax file against the XML schema
   * and then parses it to construct a Syntax object.
   *
   * @param databaseType The type of the database for which the syntax is to be loaded.
   * @return The loaded Syntax object representing the database syntax.
   * @throws SyntaxLoadException If there is an error in loading or validating the syntax file.
   */
  public Syntax loadSyntax(String databaseType) {
    final String resourcePath = String.format("/syntaxes/%s.xml", databaseType.toLowerCase());

    parseSecure(resourcePath);

    try (final InputStream inputStream = getSyntaxInputStream(databaseType)) {
      new SyntaxValidatorFactory().createValidator().validate(new StreamSource(inputStream));
    } catch (IOException | SAXException e) {
      throw new SyntaxLoadException("Could not validate syntax!", e);
    }

    try (final InputStream inputStream = getSyntaxInputStream(databaseType)) {

      final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
      final Unmarshaller unmarshaller = context.createUnmarshaller();

      @SuppressWarnings("unchecked") final JAXBElement<Syntax> syntaxJAXBElement = (JAXBElement<Syntax>) unmarshaller.unmarshal(
          inputStream);
      return syntaxJAXBElement.getValue();
    } catch (JAXBException | IOException e) {
      throw new SyntaxLoadException("Could not load syntax!", e);
    }
  }

  /**
   * Parse the given input stream with secure settings. The output is not used, but the method will
   * throw an exception if the input file is not secure. See: <a
   * href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html">OWASP:
   * XXE Prevention Cheat Sheet</a>
   *
   * @param resourcePath The path to the resource
   */
  private void parseSecure(final String resourcePath) {
    try (final InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
      SAXParserFactory spf = SAXParserFactory.newInstance();

      spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      spf.setXIncludeAware(false);

      new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(inputStream));
    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new SyntaxLoadException("Could not load syntax", e);
    }
  }

  /**
   * Retrieves an input stream for the syntax file of a given database type. This method first
   * attempts to find the syntax file path from the application's configuration. If the path is not
   * specified in the configuration, it defaults to loading the syntax file from the classpath. This
   * allows for flexible syntax file sourcing, either from external files or embedded resources.
   *
   * @param databaseType The type of the database for which the syntax file input stream is to be
   *                     retrieved.
   * @return An InputStream of the syntax file for the specified database type.
   * @throws SyntaxLoadException If the syntax file cannot be loaded due to an IOException.
   */
  private InputStream getSyntaxInputStream(final String databaseType) {
    final Configuration configuration = Configuration.getInstance();

    final Map<String, String> syntaxFileMap = configuration.getMap("syntax");

    String syntaxFilePath = null;

    if (syntaxFileMap != null) {
      syntaxFilePath = syntaxFileMap.get(databaseType.toLowerCase());
    }

    if (syntaxFilePath == null) {
      return getClass().getResourceAsStream(
          String.format("/syntaxes/%s.xml", databaseType.toLowerCase()));
    }

    final File syntaxFile = new File(syntaxFilePath);

    try {
      return new FileInputStream(syntaxFile);
    } catch (IOException e) {
      throw new SyntaxLoadException("Could not load syntax", e);
    }
  }


}
