resource "google_cloudfunctions_function" "function" {
  name        = "generate_data"
  description = "Function to generate data and push it to pubsub"
  runtime     = "java11"

  available_memory_mb = 256
  entry_point         = "functions.GenerateData"

  trigger_http = true

  environment_variables = { "PUBSUB_TOPIC" : google_pubsub_topic.data-topic.id }

  source_repository {
    url = "https://source.developers.google.com/projects/${var.PROJECT_NAME}/repos/github_pbagriy_google-cloud-learning/moveable-aliases/main/paths/cloud-function/"
  }

  depends_on = [google_project_service.gcp_services]
}

# IAM entry for all users to invoke the function
resource "google_cloudfunctions_function_iam_member" "invoker" {
  project        = google_cloudfunctions_function.function.project
  region         = google_cloudfunctions_function.function.region
  cloud_function = google_cloudfunctions_function.function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}