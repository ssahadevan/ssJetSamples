
package org.example;

import com.hazelcast.jet.*;
import com.hazelcast.jet.config.*;
import com.hazelcast.jet.examples.tradesource.*;
import com.hazelcast.jet.pipeline.*;

import static com.hazelcast.jet.datamodel.Tuple4.tuple4;

public class JoinUsingMapJob {

    public static final int TRADES_PER_SEC = 1;

    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.create();

        pipeline.readFrom(TradeSource.tradeStream(TRADES_PER_SEC))
                .withoutTimestamps()
                .mapUsingIMap("companyNames", Trade::getTicker, (trade, name) ->
                        tuple4(trade.getTicker(), trade.getQuantity(), trade.getPrice(), name))
                .writeTo(Sinks.logger(tuple -> String.format("%5s quantity=%4d, price=%d (%s)",
                        tuple.f0(), tuple.f1(), tuple.f2(), tuple.f3()
                )));

        JetInstance instance = Jet.bootstrappedInstance();
        instance.newJob(pipeline, new JobConfig().setName("map-join-tutorial"));
        instance.shutdown();
    }

}