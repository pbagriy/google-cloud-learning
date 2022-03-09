resource "google_dataflow_flex_template_job" "pubsub_to_bigquery" {
  name                    = "pubsub-to-bigquery"
  container_spec_gcs_path = "gs://${var.CONFIG_BUCKET}/templates/PubSubToBigQuery.json"
  parameters              = {
    inputSubscription = google_pubsub_subscription.data-topic-subscription-dataflow.id
    outputTable       = "${var.PROJECT_NAME}:${google_bigquery_dataset.learning_dataset.dataset_id}.data"
  }
  on_delete = "cancel"
}