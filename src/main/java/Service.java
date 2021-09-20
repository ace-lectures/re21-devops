import io.jooby.Jooby;
import io.jooby.ServerOptions;

import java.util.LinkedList;
import java.util.List;

public class Service extends Jooby {

    {
        setServerOptions(new ServerOptions()
                .setPort(Integer.parseInt(System.getenv("PORT"))));

        get("/", ctx -> "Welcome to our drink ordering system");
        get("/orders", ctx -> { return getAllOrders();});
        get("/orders/{owner}/{recipient}/{drink}", (ctx) -> {
            Order o = addOrder(ctx.path("owner").value(),
                               ctx.path("recipient").value(),
                               ctx.path("drink").value());
            return "added " + o;
        });
    }

    private final List<Order> orders = new LinkedList<>();

    public String getAllOrders() {
        if(orders.isEmpty())
            return "Nothing to show";
        return orders.stream()
                .map(o -> o.toString())
                .reduce("",(s1,s2) -> s1 +"\n" + s2);
    }

    public Order addOrder(String owner, String recipient, String drinkName) {
        Order o = new Order();
        o.setOwner(owner);
        o.setRecipient(recipient);
        o.getDrinks().add(new Order.Drink(drinkName));
        orders.add(o);
        return o;
    }

    public static void main(String[] args) {
        runApp(args, Service::new);
    }
}
