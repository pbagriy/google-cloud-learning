package functions.models

import play.api.libs.json.{Json, OFormat}

case class Data(id: Long, count: Long) {
  def toJsonString: String = Json.toJson(this).toString
}

object Data {
  implicit val format: OFormat[Data] = Json.format[Data]
}
