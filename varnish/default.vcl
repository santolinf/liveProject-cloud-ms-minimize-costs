vcl 4.0;

# the first backend found will also be the "default" one
backend api-catalog {
    .host = "<your IP addr or kubernetes srv name>";
    .port = "6070";
}

sub vcl_recv {
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
