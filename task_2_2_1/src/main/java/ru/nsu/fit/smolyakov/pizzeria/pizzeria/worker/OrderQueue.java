package ru.nsu.fit.smolyakov.pizzeria.pizzeria.worker;

import ru.nsu.fit.smolyakov.pizzeria.pizzeria.entity.Order;

public interface OrderQueue {
    void acceptOrder(Order order);

    Order giveOrderToBaker();

    void stop();
}
