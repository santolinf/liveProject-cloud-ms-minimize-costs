# Redis - Remote Cache

The service is deployed to a remote Kubernetes server cluster.

Deploy the resources and the service.

```shell
kubectl apply -f ./redis-deploy.yaml

kubectl apply -f ./redis-svc.yaml
```

To access the service from outside the Kubernetes cluster, expose the service through the *NodePort*:

```shell
kubectl apply -f ./redis-svc-np.yaml
```

## Accessing the service from outside the Kubernetes cluster

Once deployed, get the externalised port number:

```shell
kubectl get service
```

    NAME                        TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)           AGE
    kubernetes                  ClusterIP   10.152.183.1     <none>        443/TCP           177d
    broker-admin-console        NodePort    10.152.183.106   <none>        8161:32172/TCP    176d
    broker                      ClusterIP   10.152.183.140   <none>        61616/TCP         175d
    mongodb-np                  NodePort    10.152.183.150   <none>        27017:31668/TCP   175d
    mongodb                     ClusterIP   10.152.183.77    <none>        27017/TCP         175d
    varnish-np                  NodePort    10.152.183.220   <none>        6078:31723/TCP    17h
    varnish                     ClusterIP   10.152.183.38    <none>        6078/TCP          17h
    api-clusters-remote-cache   NodePort    10.152.183.155   <none>        6079:32565/TCP    12h

The last entry reveals the external port number (**32565**) for our Redis service:

    api-clusters-remote-cache   NodePort    10.152.183.155   <none>        6079:32565/TCP    12h

**Use this external port number and the Kubernetes hosting server hostname within the `api-clusters`,
`api-provider-alpha` and `api-provider-beta` microservices when connecting to the Redis cache service.**

## Installing and running Node.js redis-cli
Pre-requisites:

* Node.js
* npm

Install (globally) the Node.js version of redis-cli:

		npm install -g redis-cli

Then you can run it with the command:

		rdcli -h your.redis.host -a yourredispassword -p 11111

(using your relevant connection information).

## Commands
Redis [Command reference](https://redis.io/commands)

For example, `MONITOR` listens for all requests received by the server in real time.

        $ rdcli -h kubeserver -p 32565
        kubeserver:32565> ping
        PONG
        kubeserver:32565> monitor
        OK

Also, `FLUSHALL` removes all keys.

        kubeserver:32565> flushall
        OK

