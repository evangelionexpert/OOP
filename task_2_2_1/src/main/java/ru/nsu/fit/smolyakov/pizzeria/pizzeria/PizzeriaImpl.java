package ru.nsu.fit.smolyakov.pizzeria.pizzeria;

import ru.nsu.fit.smolyakov.pizzeria.pizzeria.entity.Order;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.entity.OrderDescription;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.printer.OrderStatusPrinter;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.printer.OrderStatusPrinterImpl;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.worker.Baker;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.worker.DeliveryBoy;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.worker.OrderQueue;
import ru.nsu.fit.smolyakov.pizzeria.pizzeria.worker.Warehouse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PizzeriaImpl implements PizzeriaOrderService,
                                     PizzeriaEmployeeService,
                                     PizzeriaOwnerService {
    private final OrderQueue orderQueue;
    private final Warehouse warehouse;
    private final List<Baker> bakerList;
    private final List<DeliveryBoy> deliveryBoyList;

    private final ExecutorService threadPool = Executors.newScheduledThreadPool(8);
    private final OrderStatusPrinter orderStatusPrinter = new OrderStatusPrinterImpl();

    private boolean working = false;
    private int orderId = 0;

    public PizzeriaImpl(OrderQueue orderQueue,
                        Warehouse warehouse,
                        List<Baker> bakerList,
                        List<DeliveryBoy> deliveryBoyList) {
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
        this.bakerList = bakerList;
        this.deliveryBoyList = deliveryBoyList;
    }

    @Override
    public synchronized void start() {
        working = true;
    }

    @Override
    public synchronized boolean makeOrder(OrderDescription orderDescription) {
        if (!working) {
            return false;
        } else {
            submitTask(() ->
                orderQueue.acceptOrder(Order.create(orderId++, orderDescription))
            );
            return true;
        }
    }

    @Override
    public void printOrderStatus(Order order) {
        orderStatusPrinter.print(order);
    }

    @Override
    public synchronized void submitTask(Runnable task) {
        threadPool.submit(task);
    }

    @Override
    public void stop() {
        working = false;
    }
}
