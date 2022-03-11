resource "google_artifact_registry_repository" "docker-registry" {
  provider = google-beta

  location = var.LOCATION
  repository_id = "dataflow"
  format = "DOCKER"

  depends_on = [google_project_service.gcp_services]
}