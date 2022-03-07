package functions

import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import functions.models.Data

import scala.jdk.OptionConverters.RichOptional
import scala.util.Random

class GenerateData extends HttpFunction {
  val topic: String = System.getenv("PUBSUB_TOPIC")

  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    request.getFirstQueryParameter("count").toScala match {
      case Some(count) if count.toIntOption.isDefined =>
        val publisher = Publisher.newBuilder(topic).build()
        (1 to count.toInt).foreach { _ =>
          val data = ByteString.copyFromUtf8(
            Data(Random.nextInt(10), Random.nextInt(100)).toJsonString
          )
          val messageIdFuture =
            publisher.publish(PubsubMessage.newBuilder().setData(data).build())

          messageIdFuture.get
        }
        response.getWriter.write(s"$count messages were sent to pubsub")
      case Some(count) =>
        response.getWriter.write(s"Invalid count value: $count")
        response.setStatusCode(400)
      case _ =>
        response.getWriter.write("Query parameter is missing: count")
        response.setStatusCode(400)
    }
  }
}
