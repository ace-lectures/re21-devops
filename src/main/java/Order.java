import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

public class Order {

    private List<Drink> contents = new LinkedList<>();
    private double taxes = 0.0;

    private String owner;
    public void setOwner(String who) { this.owner = who; }
    private String recipient;
    public void setRecipient(String who) { this.recipient = who; }
    public void setTaxes(double rate) { this.taxes = rate; }

    public List<Order.Drink> getDrinks() { return contents; }

    public double computePrice(Catalogue catalogue){
        return this.getDrinks().stream()
                  .map(d -> catalogue.getPrice(d.getName()))
                  .reduce(0.0, Double::sum);
    }

    public double computePriceWithTaxes(Catalogue catalogue){
        return new BigDecimal(this.computePrice(catalogue) * taxes)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    @Override
    public String toString() {
        return "Order: " + owner + " / " + recipient + " / { " + contents + "}";
    }

    static class Drink {
        public Drink(String name){ this.name = name; }
        private String name;
        public String getName() { return name; }

        @Override
        public String toString() { return name; }
    }
}
