resource "google_pubsub_topic" "data-topic" {
  name = "data-topic"
}

resource "google_pubsub_subscription" "data-topic-subscription-dataflow" {
  name  = "data-topic-subscription-dataflow"
  topic = google_pubsub_topic.data-topic.name
}