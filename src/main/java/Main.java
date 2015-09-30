import components.PersistenceManager;
import models.StockPrice;

import javax.persistence.EntityManager;
import java.util.Calendar;

public class Main
{
    public static void main(String[] args) {

        StockPrice frsPrice = new StockPrice();

        frsPrice.setPrice(10.1);
        frsPrice.setDate(Calendar.getInstance());

        EntityManager em = PersistenceManager.INSTANCE.getEntityManager();

        em.getTransaction().begin();
        em.persist(frsPrice);

        em.getTransaction().commit();
        em.close();

        PersistenceManager.INSTANCE.close();
    }
}
