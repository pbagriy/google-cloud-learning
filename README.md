# google-cloud-learning

Project to learn about and practice using various resources in Google Cloud

## Set up project in playground

Most of the setup is done (or will be done once I get there) by cloud build (playground doesn't allow a great deal of
power when it comes to cloud build) and terraform, but some initial manual setup is required in order for it to work
correctly.

All resources are currently created in us-central1 region, because playground only allows to choose europe-west1 and
functions cannot be created there due to a bug with builder. In real project you can change this by setting
TF_VAR_LOCATION to your desired region, but make sure that all resources are supported in that region.

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

### Initiate terraform with partial config (clone project first, will add command later, right now you can copy it in source repository)

`terraform init -backend-config="bucket=$TF_VAR_CONFIG_BUCKET"`

Add `-reconfigure` if for some reason first init failed

### TODO plans for the future (when I have time). PRs and suggestions are welcome of course.

- investigate why playground is being forcibly shutdown and if it can be fixed (dataflow is likely culprit, either
  because of compute resources or because of streaming inserts into bigQuery, I'd say latter is more likely. so maybe
  job rewrite will be enough, otherwise look if we can reduce assigned resources, we don't need much to begin with)
- generate more interesting data, so we can experiment with writing more sophisticated queries in BigQuery
- investigate if we can optimize cloud function, right now it's pretty slow when writing many messages
- Investigate how to better write pom.xml for cloud functions, maven sucks and sbt doesn't generate completely correct
  file, so further manual modifications are required right now. At the very least write script to semi-manually add
  required plugin in the final file
- investigate if it's possible to simplify or at least partially automate connecting to GitHub repository
- validate cloud build configuration on real project, playground doesn't give enough permissions to access source
  repository from cloud build
- Maybe write script to do all initial manual cli steps at once, obviously can't include those that require UI