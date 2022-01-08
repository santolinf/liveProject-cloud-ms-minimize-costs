vcl 4.0;

import directors;   # load the directors

# the first backend found will also be the "default" one
backend api-catalog {
    .host = "<your IP addr or kubernetes srv name>";
    .port = "6070";
    .probe = {
        .url = "/api/flights/catalog/health";
        .timeout = 1s;
        .interval = 5s;
        .window = 5;
        .threshold = 3;
    }
}

backend api-catalog2 {
    .host = "<other IP addr>";
    .port = "6070";
    .probe = {
        .url = "/api/flights/catalog/health";
        .timeout = 1s;
        .interval = 5s;
        .window = 5;
        .threshold = 3;
    }
}

sub vcl_init {
    new vdir = directors.round_robin();
    vdir.add_backend(api-catalog);
    vdir.add_backend(api-catalog2);
}

sub vcl_recv {
    # send all traffic to the vdir director:
    set req.backend_hint = vdir.backend();

    # happens before we check if we have this in cache already

    if (req.url ~ "^/api/flights/catalog/city/.*$" && req.method == "GET") {

        # strip the cookies
        unset req.http.cookie;

        return(hash);
    } else {
        return(pass);
    }
}

sub vcl_backend_response {
    # happens after we have read the response headers from the backend

    if (bereq.url ~ "^/api/flights/catalog/city/.*$") {
        # time-to-live 5 minutes
        set beresp.ttl = 300s;
        unset beresp.http.set-cookie;
        unset beresp.http.Pragma;
        unset beresp.http.Expires;
    }

    # dont cache redirects and errors
    if (beresp.status >= 300) {
        set beresp.uncacheable = true;
        set beresp.ttl = 30s;
        return(deliver);
    }

    return(deliver);
}
