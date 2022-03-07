package dataflow

import com.spotify.scio._
import com.spotify.scio.bigquery._
import com.spotify.scio.pubsub._
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage
import org.apache.beam.sdk.options.Validation.Required
import org.apache.beam.sdk.options.{
  Description,
  PipelineOptions,
  PipelineOptionsFactory,
  StreamingOptions
}
import play.api.libs.json.{Json, OFormat}

object PubSubToBigQuery {
  //I wanted to place this case class and companion object in their own file as it's usually done OOP style,
  //but unfortunately these bigquery annotations are stupid and don't work if not inside object
  @BigQueryType.toTable
  case class Data(id: Long, count: Long)

  object Data {
    implicit val format: OFormat[Data] = Json.format[Data]
  }

  trait Options extends PipelineOptions with StreamingOptions {
    @Description("The Cloud Pub/Sub subscription to read from")
    @Required
    def getInputSubscription: String
    def setInputSubscription(value: String): Unit

    @Description("The BigQuery table to write to")
    @Required
    def getOutputTable: String
    def setOutputTable(output: String): Unit
  }

  def main(cmdlineArgs: Array[String]): Unit = {
    PipelineOptionsFactory.register(classOf[Options])
    val options = PipelineOptionsFactory
      .fromArgs(cmdlineArgs: _*)
      .withValidation
      .as(classOf[Options])
    options.setStreaming(true)
    run(options)
  }

  def run(options: Options): Unit = {
    val sc = ScioContext(options)

    val pubsubSubscription: PubsubIO[PubsubMessage] =
      PubsubIO.pubsub[PubsubMessage](options.getInputSubscription)

    // If you opened this in Intellij Idea then next block will probably will show a lot of red.
    // Read param is correct, you can ignore this error, it's just Idea being stupid,
    // for everything else you should install scio plugin, idea on its own doesn't work well with bigquery annotations.
    // You can run sbt compile to confirm that it all works correctly
    sc.read(pubsubSubscription)(PubsubIO.ReadParam(PubsubIO.Subscription))
      .map(m => Json.parse(m.getPayload).as[Data])
      .saveAsTypedBigQueryTable(
        table = Table.Spec(options.getOutputTable),
        writeDisposition = WriteDisposition.WRITE_APPEND
      )

    sc.run()
  }
}
