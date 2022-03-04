# google-cloud-learning

Project to learn about and practice using various resources in Google Cloud 

## Set up project in playground

Most of the setup is done by cloud build and terraform, but some initial manual setup is required in order for it to
work correctly.

All resources are currently created in us-central1 region, because playground only allows to choose europe-west1 and
functions cannot be created there due to a bug with builder.

### Create bucket for terraform state and enable versioning

```
gsutil mb -l us-central1 gs://$GOOGLE_CLOUD_PROJECT-tf-state
gsutil versioning set on gs://$GOOGLE_CLOUD_PROJECT-tf-state
```

### Manually connect this project to cloud source repository

Terraform doesn't support mirroring, and we need it if we want to automate things. Maybe I'll consider just uploading to
storage via `gsutil`, but for now I'm trying to make this work.

Steps:

- Enable API https://console.cloud.google.com/apis/api/sourcerepo.googleapis.com/overview
- Go to https://source.cloud.google.com/
- Click `Add Repository`
- Click `Connect external repository`
- Choose your project and `Github` as provider
- Connect your Github account
- Choose this project

### Export variables

`export TF_VAR_PROJECT_NAME=$GOOGLE_CLOUD_PROJECT`

### Initiate terraform with partial config

`terraform init -backend-config="bucket=$TF_VAR_PROJECT_NAME-tf-state"`

Add `-reconfigure` if for some reason first init failed