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
  region  = "us-central1"
  zone    = "us-central1-b"
}

resource "google_project_service" "gcp_services" {
  for_each = toset([
    "cloudbuild.googleapis.com",
    "cloudfunctions.googleapis.com",
    "dataflow.googleapis.com"
  ])
  service = each.key
}


resource "google_storage_bucket" "project-data-bucket" {
  name          = "${var.PROJECT_NAME}-data"
  location      = "US-CENTRAL1"
  force_destroy = true

  uniform_bucket_level_access = true
}

