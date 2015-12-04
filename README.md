# Identity-frontend

[![Circle CI](https://circleci.com/gh/guardian/identity-frontend/tree/master.svg?style=shield)](https://circleci.com/gh/guardian/identity-frontend/tree/master)

# Application configuration

Configuration files:
- Environment-specific configuration (`conf/<ENV>.conf`)
- Application configuration (`conf/application.conf`)
- System file with additional properties (`/etc/gu/identity-frontend.conf`)

# Setting up Identity Frontend locally

## Hosts

Ensure you have the correct [identity-frontend hosts](https://github.com/guardian/identity-frontend/blob/master/nginx/hosts) included in the /etc/hosts file on your machine

## SSL Certificates & nginx setup

We have valid SSL certificates for thegulocal.com and the subdomains we use for local development.

The certificates for the local subdomain `profile-origin.thegulocal.com` are stored in the AWS S3 Identity bucket and are set up as part of the `identity-frontend.conf` for nginx.

Follow these installation steps to correctly setup nginx and valid SSL certificates locally:

* Make sure you are in the base `identity-frontend` directory

* Make sure you have access to the S3 bucket identity-local-ssl and download them using the [AWS CLI utility](https://aws.amazon.com/cli/) (the following command will download them in your current directory using your Identity profile on AWS):

```bash
aws --profile identity s3 cp s3://identity-local-ssl/profile-origin-thegulocal-com-exp2016-11-10-bundle.crt . 1>/dev/null
aws --profile identity s3 cp s3://identity-local-ssl/profile-origin-thegulocal-com-exp2016-11-10.key . 1>/dev/null
```

* Find the configuration folder of nginx by running:

```bash
nginxHome=`nginx -V 2>&1 | grep "configure arguments:" | sed 's/[^*]*conf-path=\([^ ]*\)\/nginx\.conf.*/\1/g'`
```

`echo $nginxHome` should display the name of the folder.

* Create symbolic links for the certificates and identity-frontend configuration file for nginx (note: this might require `sudo`)

```bash
sudo ln -fs `pwd`/profile-origin-thegulocal-com-exp2016-11-10-bundle.crt $nginxHome/profile-origin-thegulocal-com-exp2016-11-10-bundle.crt
sudo ln -fs `pwd`/profile-origin-thegulocal-com-exp2016-11-10.key $nginxHome/profile-origin-thegulocal-com-exp2016-11-10.key
sudo ln -fs `pwd`/nginx/identity-frontend.conf $nginxHome/sites-enabled/identity-frontend.conf
```

* Restart nginx:

```bash
sudo nginx -s stop
sudo nginx
```

* Optional - verify that your configuration is set up as expected

    - `ls -la $nginxHome` should show the certificates correctly symlinked to the **full pathname** of the downloaded certificates
    - `ls -la $nginxHome/sites-enabled` should show `identity-frontend.conf`  correctly symlinked to the **full pathname** of `identity-frontend/nginx/identity-frontend.conf`

You should now be able to start the application (`sbt run`), go to [https://profile-origin.thegulocal.com/management/healthcheck](https://profile-origin.thegulocal.com/management/healthcheck) and see a green padlock for your local SSL certificate as well as a 200 response.

## Configuration

Install the local configuration file from s3:

```
aws s3 cp --profile identity s3://gu-identity-frontend-private/DEV/identity-frontend.conf /etc/gu
```

## Running the application

```
sbt devrun
```

## Testing

### Functional tests

`sbt "project functional-tests" test`

These are browser driving Selenium tests.

### Unit tests

`sbt test`

### Guidelines

- Tests should complete in under five minutes.
- Prefer unit tests to integration/functional tests.
- Unstable tests should be removed.
