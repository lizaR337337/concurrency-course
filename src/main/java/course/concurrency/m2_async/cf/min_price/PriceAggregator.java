package course.concurrency.m2_async.cf.min_price;

import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

public class PriceAggregator {
    private ExecutorService executor = Executors.newCachedThreadPool();

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {

        List<CompletableFuture<Double>> futureList = shopIds.parallelStream()
                .map(shopId -> CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor))
                .collect(toList());

        try {
            Thread.sleep(2900);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return futureList.stream()
                .map(future -> future.isCompletedExceptionally() ? Double.NaN : future.getNow(Double.NaN))
                .min(Double::compareTo)
                .orElse(Double.NaN);
    }

}
