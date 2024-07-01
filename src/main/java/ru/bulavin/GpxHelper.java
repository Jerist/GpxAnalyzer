package ru.bulavin;

import io.jenetics.jpx.WayPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.Optional;

public class GpxHelper {

    private static final XPath xPath = XPathFactory.newInstance().newXPath();

    public static Optional<Double> getExtensionValueAsDouble(WayPoint point, String key) {
        return getExtensionValue(point, key).map(Double::parseDouble);
    }

    public static Optional<Integer> getExtensionValueAsInt(WayPoint point, String key) {
        return getExtensionValue(point, key).map(Integer::parseInt);
    }

    public static Optional<String> getExtensionValue(WayPoint point, String key) {
        Optional<Document> extensions = point.getExtensions();
        if (extensions.isPresent()) {
            try {
                XPathExpression expr = xPath.compile("//*[local-name()='" + key + "']");
                Element element = (Element) expr.evaluate(extensions.get(), XPathConstants.NODE);
                if (element != null) {
                    return Optional.of(element.getTextContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}

