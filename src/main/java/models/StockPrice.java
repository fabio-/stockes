package models;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "stock_prices")
public class StockPrice
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date", columnDefinition="DATE")
    @Temporal(TemporalType.DATE)
    private Calendar date;

    private Double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
