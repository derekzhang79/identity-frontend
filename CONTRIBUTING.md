# Contributing to identity-frontend

1. Branch off of master, name your branch related to the feature you're
   implementing, or prefix with `fix-` for bug fixes
2. Do your thing
3. Ensure tests pass locally with `sbt test`
4. Make sure your branch is up to date with our master
  - by merging or (preferably, if possible) rebasing onto master
  - this makes sure any conflicts are resolved prior to code review
5. Open a pull request
6. Code will be reviewed and require a :+1: from a team member before it
   will be merged
7. The merger is required to ensure the change is deployed to production.
   Each merge is automatically deployed to our CODE environment, where it
   should be sanity checked before PROD deploy

If you have any questions, come chat to us or send us an email.


## Coding conventions

- 2 space indent, trimmed spaces, lf, utf8, enforced with
  [editorconfig](http://editorconfig.org/), please install the plugin for your
  editor
- **Commits:** Please don't squash, history is good
- **Scala:** [Scala style guide](http://docs.scala-lang.org/style/)
- **Views:** [handlebars](http://jknack.github.io/handlebars.java/) used, view
  inputs defined in view model objects, only use built in helpers if you can

### Component organisation
TODO

### Javascript guidelines
Javascript source should be written in ES6 in the [Idiomatic JS](https://github.com/rwaldron/idiomatic.js)
style. This is enforced using [eslint](http://eslint.org/) when running tests.

ES6 is transpiled with [Babel](https://babeljs.io/) as part of a
[Webpack](http://webpack.github.io/) build step. The webpack build config
is defined in [`webpack.config.js`](https://github.com/guardian/identity-frontend/blob/master/webpack.config.js).

The build is triggered as part of sbt's compile stage. This is configured using
an `sbt-web` task in [`frontend.sbt`](https://github.com/guardian/identity-frontend/blob/master/frontend.sbt)
which will run `npm run build` when a frontend asset has changed.

### CSS guidelines
CSS source should be written using [Idiomatic CSS](https://github.com/necolas/idiomatic-css) style.
This is enforced using [stylelint](http://stylelint.io/) when running tests.

CSS is processed using [PostCSS](https://github.com/postcss/postcss)

### Test guidelines

- Tests should complete in under five minutes.
- Prefer unit tests to integration/functional tests.
- Unstable tests should be removed.
