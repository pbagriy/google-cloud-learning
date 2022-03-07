resource "google_dataflow_job" "pubsub_to_bigquery" {
  name = "pubsub_to_bigquery"
  template_gcs_path = "gs://${var.CONFIG_BUCKET}/templates/PubSubToBigQuery"
  temp_gcs_location = "gs://${google_storage_bucket.project-data-bucket.name}/temp"
  enable_streaming_engine = true
  parameters = {
    inputSubscription = google_pubsub_subscription.data-topic-subscription-dataflow.id
    outputTable = "${var.PROJECT_NAME}:${google_bigquery_dataset.learning_dataset.dataset_id}.data"
  }
  on_delete = "cancel"
}