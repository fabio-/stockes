package components.extractors;

import components.PersistenceManager;
import models.StockPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joox.JOOX;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.joox.JOOX.$;

public class XmlExtractor
{
    private Path inputXmlPath;

    private Document xmlDocument;

    private static Logger logger = LogManager.getLogger(XmlExtractor.class.getName());

    public XmlExtractor(Path inputXmlPath)
    {
       this.inputXmlPath = inputXmlPath;
    }

    private void loadDocument()
    {
        try {
            DocumentBuilder builder = JOOX.builder();
            this.xmlDocument = builder.parse(new FileInputStream(inputXmlPath.toFile()));

        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void processItems()
    {
        EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
        loadDocument();

        Match x1 = $(xmlDocument)
                .xpath("/quandl-response/dataset/data[@type='array']/datum[@type='array']");

        int i = 0;
        Node xmlItem;

        while(true) {

            xmlItem = x1.get(i);

            if(xmlItem == null){
                em.close();
                PersistenceManager.INSTANCE.close();
                return;
            }

            StockPrice stockPrice = new StockPrice();

            Node xmlDate = xmlItem.getFirstChild().getNextSibling();
            String date = xmlDate.getFirstChild().getTextContent();

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                cal.setTime(sdf.parse(date));
            } catch (ParseException e) {
                logger.warn("Couldn't parse date: "+ date);
            }

            Node xmlOpenValue = xmlDate.getNextSibling().getNextSibling();
            String openValue = xmlOpenValue.getFirstChild().getTextContent();
            Double parsedOpenValue = Double.parseDouble(openValue);

            stockPrice.setDate(cal);
            stockPrice.setPrice(parsedOpenValue);


            em.getTransaction().begin();
            em.persist(stockPrice);
            em.getTransaction().commit();

            logger.info(i + " / "+ x1.size());

            i++;
        }
    }
}
