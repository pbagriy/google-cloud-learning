terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "4.11.0"
    }
  }

  backend "gcs" {
    prefix = "terraform/state"
  }
}

provider "google" {
  project = var.PROJECT_NAME
  region  = "europe-west1"
  zone    = "europe-west1-b"
}

resource "google_project_service" "gcp_services" {
  for_each = toset([
    "cloudbuild.googleapis.com",
    "cloudfunctions.googleapis.com",
    "dataflow.googleapis.com"
  ])
  service = each.key
}


resource "google_storage_bucket" "static-site" {
  name          = "${var.PROJECT_NAME}-data"
  location      = "EUROPE-WEST1"
  force_destroy = true

  uniform_bucket_level_access = true
}

