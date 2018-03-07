/*eslint-env node*/

var webpack = require( 'webpack' );
var path = require( 'path' );

module.exports = {
  entry: {
    main: './public/main'
  },
  output: {
    path: path.resolve( __dirname, 'target/web/public/main/' ),
    publicPath: '/static/',
    filename: '[name].bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: [{
          loader: 'babel-loader'
        }]
      }
    ]
  },
  plugins: [
    new webpack.LoaderOptionsPlugin({
      minimize: true
    })
  ],
  resolve: {
    alias: {
      'intl-tel': 'intl-tel-input/build/js/intlTelInput',
      'intl-tel-utils': 'intl-tel-input/build/js/utils.js'
    }
  }
};
