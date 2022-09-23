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

record DeviceCloudEvent(String id) { }

class Main {
  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("QuickStart");

    final Source<DeviceCloudEvent, NotUsed> source = Source.range(1, 100).map(i -> new DeviceCloudEvent(i.toString()));

    final CompletionStage<Done> done = source.runForeach(i -> System.out.println(i), system);
    done.thenRun(() -> system.terminate());
  }

}