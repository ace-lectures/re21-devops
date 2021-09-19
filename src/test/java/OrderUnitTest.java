import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class OrderUnitTest {

    @Test public void empty_order_by_default(){
        Order o = new Order();
        o.setOwner("Roméo");
        o.setRecipient("Juliette");
        List<Order.Drink> drinks = o.getDrinks();
        assertEquals(0, drinks.size());
    }

}
