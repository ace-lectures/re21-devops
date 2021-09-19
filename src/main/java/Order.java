import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Order {

    private List<Drink> contents = new LinkedList<>();

    public void setOwner(String who) { /* ...*/ }
    public void setRecipient(String who) { /* ...*/ }

    public List<Order.Drink> getDrinks() { return contents; }

    static class Drink {
        public Drink(String name){ this.name = name; }
        private String name;
        public String getName() { return name; }
    }
}
