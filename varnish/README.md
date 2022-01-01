# Deploy Varnish to Kubernetes

## Decisions

Instead of installing [Docker Compose](https://docs.docker.com/compose/) to run a local Docker container, I resorted to use remote deployments
of [Microk8s](https://microk8s.io/) and Docker running on a virtualized Ubuntu Server.

## Milestone 3 - Adding Cache in front of Microservice

Either use the IntelliJ IDEA Docker plugin (not shown) or command line tools (see below) to create
the Docker image remotely.

```shell
tar cfz - ./Dockerfile ./default.vcl | curl -i -v -T - -XPOST -H "Content-Type: application/x-tar" -H "content-transfer-encoding: binary" $KUBE_SERVER:2375/v1.40/build?t=localhost:32000/twa-varnish
```

The service is deployed to Kubernetes using the Docker image created (and which is pushed
to the local Docker repository on the remote server).

Make sure that on the remote server we run the following command:

```shell
ssh $KUBE_SERVER docker push localhost:32000/twa-varnish
```

Use Kubernetes deployment resource to deploy the service.

```shell
kubectl apply -f ./varnish-deploy.yaml

kubectl apply -f ./varnish-svc.yaml
```

To access the service from outside the Kubernetes cluster, expose the service through the *NodePort*:

```shell
kubectl apply -f ./varnish-svc-np.yaml
```

## Accessing the service from outside the Kubernetes cluster

Once deployed, get the externalised port number:

```shell
kubectl get service
```

    NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)           AGE
    kubernetes             ClusterIP   10.152.183.1     <none>        443/TCP           177d
    broker-admin-console   NodePort    10.152.183.106   <none>        8161:32172/TCP    176d
    broker                 ClusterIP   10.152.183.140   <none>        61616/TCP         175d
    mongodb-np             NodePort    10.152.183.150   <none>        27017:31668/TCP   175d
    mongodb                ClusterIP   10.152.183.77    <none>        27017/TCP         175d
    redis                  ClusterIP   10.152.183.54    <none>        6079/TCP          44h
    redis-np               NodePort    10.152.183.90    <none>        6079:31569/TCP    44h
    varnish-np             NodePort    10.152.183.220   <none>        6078:31723/TCP    17m
    varnish                ClusterIP   10.152.183.38    <none>        6078/TCP          14m

The second last entry reveals the external port number (**31723**) for our Varnish service:

    varnish-np             NodePort    10.152.183.220   <none>        6078:31723/TCP    17m

**Use this external port number and the Kubernetes hosting server hostname within the `api-clusters`, 
`api-provider-alpha` and `api-provider-beta` microservices when connecting to the Catalog API.**

## Viewing Statistics

To execute any of the Varnish statistics generating tools, you must execute the commands on
a running Kubernetes container.

Get the Pod-name on the Kubernetes cluster.

```shell
kubectl get pod
```

    NAME                                READY   STATUS    RESTARTS   AGE
    mongodb-55d65c56bc-mrmzt            1/1     Running   38         155d
    broker-7879c87599-gssfm             1/1     Running   34         155d
    redis-fd7cc6786-ssz2g               1/1     Running   5          47h
    camel-k-operator-69c96b94ff-lzcbx   1/1     Running   13         138d
    varnish-8b64cb947-zcl87             1/1     Running   0          32m

In this case the Pod name is `varnish-8b64cb947-zcl87`.


The following command displays a continuously updated list of the most frequently requested URLs:

```shell
kubectl exec -it varnish-8b64cb947-zcl87 -- /bin/bash -c varnishtop -i ReqURL
```

Initially, if the service did not receive any requests yet, it will show
`list length 0`.

After one request to the Clusters microservice, we can see that, at least 2 URLs are now 
displayed:

    1.56 ReqURL         /api/flights/catalog/city/MIA
    1.56 ReqURL         /api/flights/catalog/city/BUE
