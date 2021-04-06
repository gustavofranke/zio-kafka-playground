package pack

import zio._
import org.apache.kafka.clients.consumer.{ConsumerConfig, CooperativeStickyAssignor}
import zio.ZManaged
import zio.clock.Clock
import zio.kafka.consumer.ConsumerSettings
import zio.ZLayer
import zio.kafka.consumer._
import zio.kafka.serde._
import zio.blocking.Blocking
import zio.console.{Console, putStrLn}


object MyTest {

  val consumerSettings: ConsumerSettings =
    ConsumerSettings(List("localhost:9092"))
      .withGroupId("group")
      .withProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, classOf[CooperativeStickyAssignor].getSimpleName)

  val consumerManaged: ZManaged[Clock with Blocking, Throwable, Consumer.Service] =
    Consumer.make(consumerSettings)

  val consumer: ZLayer[Clock with Blocking, Throwable, Consumer] =
    ZLayer.fromManaged(consumerManaged)

  val data2 =
    Consumer.subscribeAnd(Subscription.topics("topic150"))
      .plainStream(Serde.string, Serde.string)
      .tap(cr => putStrLn(s"key: ${cr.record.key}, value: ${cr.record.value}"))
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