package de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.exception.SyntaxLoadException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ObjectFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Loads the syntax for the given database type.
 */
public class SyntaxLoader {

  private static final SyntaxLoader INSTANCE = new SyntaxLoader();

  public static SyntaxLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Load the syntax for the given database type.
   *
   * @param databaseType The database type
   * @return The syntax
   */
  public Syntax loadSyntax(String databaseType) {
    final String resourcePath = String.format("/syntaxes/%s.xml", databaseType);

    parseSecure(resourcePath);

    try (final InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
      new SyntaxValidatorFactory().createValidator().validate(new StreamSource(inputStream));
    } catch (IOException | SAXException e) {
      throw new SyntaxLoadException("Could not validate syntax!", e);
    }

    try (final InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {

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


}
