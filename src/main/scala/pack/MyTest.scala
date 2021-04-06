package pack

import zio._
import org.apache.kafka.clients.consumer.{ConsumerConfig, CooperativeStickyAssignor, RoundRobinAssignor, StickyAssignor}
import zio.ZManaged
import zio.clock.Clock
import zio.kafka.consumer.ConsumerSettings
import zio.ZLayer
import zio.kafka.consumer._
import zio.kafka.serde._
import zio.blocking.Blocking
import zio.console.{Console, putStrLn}

object MyTest {

  import MyConfig._

  val consumerSettings: ConsumerSettings =
    ConsumerSettings(bootstrapServers)
      .withGroupId("consumerGroup")
      .withProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, partitionAssignor)

  val consumerManaged: ZManaged[Clock with Blocking, Throwable, Consumer.Service] =
    Consumer.make(consumerSettings)

  val consumer: ZLayer[Clock with Blocking, Throwable, Consumer] =
    ZLayer.fromManaged(consumerManaged)

  val data2 =
    Consumer.subscribeAnd(Subscription.topics(topic))
      .plainStream(Serde.string, Serde.string)
      .tap(cr => putStrLn(s"offset: ${cr.record.offset()}, key: ${cr.record.key}, value: ${cr.record.value}, timestamp: ${java.time.Instant.ofEpochMilli(cr.record.timestamp)}"))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapM(_.commit)
}

object MyApp extends zio.App {

  override def run(args: List[String]): URIO[ZEnv with Console, ExitCode] =
    MyTest.data2
      .runDrain
      .provideCustomLayer(MyTest.consumer)
      .exitCode

}