package com.example.book_webstore.service.strategy.shipping;

import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import java.util.List;
import com.example.book_webstore.model.Shipping;

@Component
public class ShippingCostStrategyFactory {

    private final Map<Shipping.ShippingMethod, ShippingCostStrategy> strategyMap = new EnumMap<>(
            Shipping.ShippingMethod.class);

    public ShippingCostStrategyFactory(List<ShippingCostStrategy> strategies) {
        for (ShippingCostStrategy strategy : strategies) {
            strategyMap.put(strategy.getMethod(), strategy);
        }
    }

    public ShippingCostStrategy getStrategy(Shipping.ShippingMethod method) {
        return strategyMap.get(method);
    }
}