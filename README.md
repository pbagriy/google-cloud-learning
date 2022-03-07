# google-cloud-learning

Project to learn about and practice using various resources in Google Cloud

## Set up project in playground

Most of the setup is done (or will be done once I get there) by cloud build and terraform, but some initial manual setup
is required in order for it to work correctly.

All resources are currently created in us-central1 region, because playground only allows to choose europe-west1 and
functions cannot be created there due to a bug with builder.

### Manually connect this project to cloud source repository

Terraform doesn't support mirroring, and we need it if we want to automate things. Maybe I'll consider just uploading to
storage via `gsutil`, but for now I'm trying to make this work.

Steps:

- Enable API https://console.cloud.google.com/apis/api/sourcerepo.googleapis.com/overview
- Go to https://source.cloud.google.com/
- Click `Add Repository`
- Click `Connect external repository`
- Choose your project and `Github` as provider
- Connect your GitHub account
- Choose this project

### Export variables

`export TF_VAR_PROJECT_NAME=$GOOGLE_CLOUD_PROJECT`
`export TF_VAR_CONFIG_BUCKET=$GOOGLE_CLOUD_PROJECT-configs`

### Create bucket for terraform state and other configs and templates, and enable versioning (temporary, will be replaced with cloud build)

```
gsutil mb -l us-central1 gs://$TF_VAR_CONFIG_BUCKET
gsutil versioning set on gs://$TF_VAR_CONFIG_BUCKET
```

### Publish dataflow template (temporary, will be replaced with cloud build)

- `sdk install sbt`
- `sbt "runMain dataflow.PubSubToBigQuery --project=$GOOGLE_CLOUD_PROJECT --runner=DataflowRunner --region=us-central1 --stagingLocation=gs://$TF_VAR_CONFIG_BUCKET/staging --templateLocation=gs://$TF_VAR_CONFIG_BUCKET/templates/PubSubToBigQuery"`

### Initiate terraform with partial config

`terraform init -backend-config="bucket=$TF_VAR_CONFIG_BUCKET"`

Add `-reconfigure` if for some reason first init failed