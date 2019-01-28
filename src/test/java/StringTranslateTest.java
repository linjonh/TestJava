import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class StringTranslateTest {
    @Test
    public void testString() throws IOException, ParserConfigurationException, SAXException {
        File file = new File("L:\\PROJECT\\playerTranslate.xml");
        File fileTras = new File("C:\\Users\\Jaysen\\Documents\\translate.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileTras));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        Element documentElement = document.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("string");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            printFmt(bufferedReader, item);
//            printTextValue(item);
        }
    }

    private void printTextValue(Node item) {
        Node firstChild = item.getFirstChild();
        System.out.println(firstChild.getNodeValue());
    }

    private void printFmt(BufferedReader bufferedReader, Node item) throws IOException {
        NamedNodeMap attributes = item.getAttributes();
        Node attr = attributes.item(0);
        String s = bufferedReader.readLine();
        System.out.println("<string " + attr + ">" + s + "</string>");
    }
}
