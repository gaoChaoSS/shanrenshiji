require.config({
    baseUrl : '/js',
    paths : {
        "jquery" : "./lib/jquery-2.1.3.min",
        "angular" : "./lib/angular.min"
    },
    shim : {
        angular : {
            exports : 'angular',
            deps : [ 'jquery' ]
        },
        jquery : {
            exports : '$'
        }
    }
});
require([ 'app' ], function(app) {
    app.init();
});
