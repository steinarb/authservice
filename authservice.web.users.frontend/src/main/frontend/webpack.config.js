var path = require('path');
const ESLintPlugin = require('eslint-webpack-plugin');

const PATHS = {
    build: path.join(__dirname, '..', '..', '..', 'target', 'classes')
};

module.exports = {
    entry: './src/index.js',
    output: {
        path: PATHS.build,
        filename: 'bundle.js'
    },
    devtool: 'source-map',
    resolve: {
        extensions: ['.js', '.jsx']
    },
    plugins: [new ESLintPlugin()],
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: 'babel-loader?' + JSON.stringify({
                    cacheDirectory: true,
                    presets: [
                        '@babel/preset-env',
                        '@babel/preset-react',
                    ],
                }),
            },
            {
                test: /\.css$/,
                use: [ { loader: 'style-loader' }, { loader: 'css-loader' } ]
            },
            {
                test: /\.(eot|svg|ttf|woff|woff2|otf)(\??#?v=[.0-9]+)?$/,
                use: 'file-loader?name=[name].[ext]',
            },
        ]
    }
};
