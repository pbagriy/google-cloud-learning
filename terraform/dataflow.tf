resource "google_dataflow_flex_template_job" "pubsub_to_bigquery" {
  provider                = google-beta
  name                    = "pubsub-to-bigquery"
  container_spec_gcs_path = "gs://${var.CONFIG_BUCKET}/dataflow/templates/PubSubToBigQuery.json"
  parameters              = {
    inputSubscription = google_pubsub_subscription.data-topic-subscription-dataflow.id
    outputTable       = "${var.PROJECT_NAME}:${google_bigquery_dataset.learning_dataset.dataset_id}.data"
  }
  on_delete = "cancel"

  depends_on = [null_resource.build_template]
}

resource "null_resource" "build_template" {

  provisioner "local-exec" {
    command = "sbt createFlexTemplate"
    working_dir = "../dataflow-job"
  }

  depends_on = [google_artifact_registry_repository.docker-registry]
}