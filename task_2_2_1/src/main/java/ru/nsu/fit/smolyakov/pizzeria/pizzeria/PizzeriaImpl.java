package ru.nsu.fit.smolyakov.pizzeria.pizzeria;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.entity.order.Order;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.entity.order.OrderInformationService;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.entity.order.description.OrderDescription;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.workers.baker.Baker;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.workers.deliveryboy.DeliveryBoy;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.workers.orderqueue.OrderQueue;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.workers.warehouse.Warehouse;
import ru.nsu.fit.smolyakov.pizzeria.util.PizzeriaPrinter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class PizzeriaImpl implements PizzeriaOrderService,
    PizzeriaEmployeeService,
    PizzeriaOwnerService,
    PizzeriaBakerService,
    PizzeriaDeliveryBoyService {

    private final static ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService executorService =
        new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

    @JsonProperty("name")
    private String pizzeriaName;

    @JsonProperty("orderQueue")
    @JsonManagedReference(value = "orderQueue")
    private OrderQueue orderQueue;

    @JsonProperty("warehouse")
    @JsonManagedReference(value = "warehouse")
    private Warehouse warehouse;

    @JsonProperty("bakers")
    @JsonManagedReference(value = "bakers")
    private List<Baker> bakerList;

    @JsonProperty("deliveryBoys")
    @JsonManagedReference(value = "deliveryBoys")
    private List<DeliveryBoy> deliveryBoyList;

    @JsonIgnore
    private final AtomicBoolean working = new AtomicBoolean(false);
    @JsonIgnore
    private int orderId = 0;

    @JsonCreator
    private PizzeriaImpl(@JsonProperty("name") String pizzeriaName) {
        this.pizzeriaName = pizzeriaName;
    }


    public static PizzeriaOwnerService fromJson(InputStream stream) {
        try {
            return mapper.readValue(stream, PizzeriaImpl.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderInformationService> makeOrder(OrderDescription orderDescription) {
        if (!working.get()) {
            return Optional.empty();
        }

        var order = Order.create(this, orderId++, orderDescription);

        orderQueue.put(order);

        return Optional.of(order);
    }

    @Override
    public synchronized void start() {
        working.set(true);

        orderQueue.start();
        bakerList.forEach(Baker::start);
        deliveryBoyList.forEach(DeliveryBoy::start);
    }

    @Override
    public boolean isWorking() {
        return working.get();
    }

    @Override
    public synchronized void stop() {
        orderQueue.stop();
        bakerList.forEach(Baker::stop);
        deliveryBoyList.forEach(DeliveryBoy::stop);
        working.set(false);
    }

    @Override
    public PizzeriaOrderService getOrderService() {
        return this;
    }

    @Override
    public Warehouse getWarehouse() {
        return warehouse;
    }

    @Override
    public OrderQueue getOrderQueue() {
        return orderQueue;
    }

    @Override
    public String getPizzeriaName() {
        return pizzeriaName;
    }

    @Override
    public void execute(Runnable task) {
        executorService.execute(task);
    }

    @Override
    public void schedule(int delayMillis, Runnable task) {
        executorService.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }
}
