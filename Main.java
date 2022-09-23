import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.util.ByteString;
import akka.stream.*;
import akka.stream.javadsl.*;

import java.nio.file.Paths;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

record DeviceCloudEvent(String id, int chassisTemp) {
}

class Main {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("QuickStart");

        // build a source
        final Source<DeviceCloudEvent, NotUsed> source = Source
                .range(1, 1000)
                .map(i -> new DeviceCloudEvent("device" + i.toString(), (int) (100.0 * Math.random())));

        // a flow
        final Flow<DeviceCloudEvent, DeviceCloudEvent, NotUsed> hotDeviceFilter = Flow.of(DeviceCloudEvent.class)
                .filter(m -> m.chassisTemp() > 80);

        // a sink
        final Sink<DeviceCloudEvent, CompletionStage<Done>> sink = Sink
                .foreach(m -> System.out.println(m));

        // wire them together into a graph
        final RunnableGraph<CompletionStage<Done>> runnableGraph =
                // build a simple graph
                // source.via(hotDeviceFilter).toMat(sink, Keep.right());

                // build graph using GraphDSL (supports more complex flows)
                RunnableGraph.fromGraph(
                        GraphDSL.create(sink,
                                (builder, out) -> {
                                    final Outlet<DeviceCloudEvent> a = builder.add(source).out();
                                    final FlowShape<DeviceCloudEvent, DeviceCloudEvent> b = builder
                                            .add(hotDeviceFilter);
                                    // final Inlet<DeviceCloudEvent> c = builder.add(sink).in();
                                    builder.from(a).via(b).to(out);
                                    return ClosedShape.getInstance();
                                }));

        // run the graph
        final CompletionStage<Done> done = runnableGraph.run(system);

        // terminate the actor system when the graph finishes (via future completed)
        done.thenRun(() -> system.terminate());
    }

}