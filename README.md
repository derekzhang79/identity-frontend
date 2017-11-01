# identity-frontend

Frontend for Identity account sign in and registration at [theguardian.com](http://theguardian.com).

## Configuration files

- Environment-specific configuration (`conf/<ENV>.conf`)
- Application configuration (`conf/application.conf`)
- Private configuration (`/etc/gu/identity-frontend.conf`)

## Local development

### Nginx setup

Clone [identity-platform](https://github.com/guardian/identity-platform) and follow its [nginx README](https://github.com/guardian/identity-platform/tree/master/nginx)

### Configuration

Download DEV private configuration from s3:

```bash
aws s3 cp --profile identity s3://identity-private-config/DEV/identity-frontend/identity-frontend.conf /etc/gu
```

**Note**: If you do not have Janus access to Identity, we can grant your team specific access, which means you would substitute `--profile identity` with e.g. `--profile membership`. Contact the Identity team if you require access to these files.


### Running

Requires:

 - [JDK 8](http://openjdk.java.net)
 - [sbt](http://www.scala-sbt.org)
 - [Node.js](https://nodejs.org) - version is specified by [.nvmrc](.nvmrc), execute [`$ nvm use`](https://github.com/creationix/nvm#nvmrc) to use it

To run the application in development mode use:

    ./start-frontend.sh

This command will automatically pull down all dependencies for the Scala app,
and client-side dependencies with Node.js. Sources will automatically be watched,
so making changes locally will result in compile being triggered automatically.

Client side sources will automatically be compiled using the `npm run build` command.

Test by hitting [https://profile.thegulocal.com/management/healthcheck](https://profile.thegulocal.com/management/healthcheck). 

## Running against DEV Identity API

By default identity-frontend targets CODE Identity API. To target DEV Identity API update `/etc/gu/identity-frontend.conf` to include:

```hocon
identity.api.host=idapi.thegulocal.com
identity.api.key=frontend-dev-client-token
```
## Contributing

See [CONTRIBUTING.MD](https://github.com/guardian/identity-frontend/blob/master/CONTRIBUTING.md).

## Running Tests

* Unit tests: `sbt test`
* Functional tests: `sbt "project functional-tests" test`
    
