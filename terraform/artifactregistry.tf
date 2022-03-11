resource "google_artifact_registry_repository" "docker-registry" {
  provider = google-beta

  location = var.LOCATION
  repository_id = "dataflow"
  format = "DOCKER"

  depends_on = [google_project_service.gcp_services]
}

resource "null_resource" "authorize_docker" {

  provisioner "local-exec" {
    command = "gcloud auth configure-docker us-central1-docker.pkg.dev"
  }

  depends_on = [google_artifact_registry_repository.docker-registry]
}