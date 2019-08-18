# Jenkins Configuration as Code AWS Secrets Manager plugin
Jenkins plugin for getting secrets from AWS Secrets Manager when using Jenkins Configuration as Code plugin.

This plugin is inspired/based on the work done by Bambora on the [Configuration as a code SSM plugin](https://github.com/jenkinsci/configuration-as-code-secret-ssm-plugin)

[More information about AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html)

[More information about Jenkins Configuration as Code plugin](https://github.com/jenkinsci/configuration-as-code-plugin)

## Features
The followings are supported:

- get secrets from the same account/region
- cross account/region secrets retrieval

*Note*: AWS KMS and IAM roles needs to be setup correctly for the cross account secrets retrieval. *The cross account setup will require a custom KMS key*, check AWS documentation for more details.

## Usage
Install plugin via Jenkins Update Center.

The following example will try to resolve the secret called `password` from the same region of the same AWS account where Jenkins is deployed

```
credentials:
  system:
    domainCredentials:
    - credentials:
      - usernamePassword:
          description: "Secret password description"
          id: "custom-super-secret-id"
          password: ${password}
```    

For cross account/region make sure the secret ARN is specified like on the example below:

```
credentials:
  system:
    domainCredentials:
    - credentials:
      - usernamePassword:
          description: "Secret password description"
          id: "custom-super-secret-id"
          secret: ${arn:aws:secretsmanager:REGION:AWS_ACCOUNT_ID:secret:password}
```
The secret called `password` will be resolved from the mentioned REGION and AWS_ACCOUNT.

A prefix can be configured as environment variable `CASC_SECRETSMANAGER_PREFIX`.

Example:
```
CASC_SECRETSMANAGER_PREFIX=arn:aws:secretsmanager:REGION:AWS_ACCOUNT_ID:secret:
```
or
```
CASC_SECRETSMANAGER_PREFIX=production/
```
or
```
CASC_SECRETSMANAGER_PREFIX=arn:aws:secretsmanager:REGION:AWS_ACCOUNT_ID:secret:production/
```
or any other value which should prefix ALL the secrets.
