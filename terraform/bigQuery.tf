resource "google_bigquery_dataset" "learning_dataset" {
  dataset_id                  = "learning_dataset"
  description                 = "This is a dataset for learning purposes"
  location                    = var.LOCATION
}