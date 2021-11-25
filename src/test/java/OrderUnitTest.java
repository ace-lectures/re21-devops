import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderUnitTest {

    @Test
    public void empty_order_by_default() {
        Order o = new Order();
        o.setOwner("Romeo");
        o.setRecipient("Juliet");
        List<Order.Drink> drinks = o.getDrinks();
        assertEquals(0, drinks.size());
    }

}
