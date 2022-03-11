resource "google_dataflow_flex_template_job" "pubsub_to_bigquery" {
  provider                = google-beta
  name                    = "pubsub-to-bigquery"
  container_spec_gcs_path = "gs://${var.CONFIG_BUCKET}/dataflow/templates/dataflow-job.json"
  parameters              = {
    inputSubscription = google_pubsub_subscription.data-topic-subscription-dataflow.id
    outputTable       = "${var.PROJECT_NAME}:${google_bigquery_dataset.learning_dataset.dataset_id}.data"
  }
  on_delete = "cancel"

  depends_on = [null_resource.build_template]
}

resource "null_resource" "build_template" {

  provisioner "local-exec" {
    command = "sdk install sbt"
  }

  provisioner "local-exec" {
    command = "sbt createFlexTemplate"
    working_dir = "../dataflow-job"
  }

  depends_on = [null_resource.authorize_docker]
}