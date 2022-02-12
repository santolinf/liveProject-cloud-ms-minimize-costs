# Deploy Zookeeper to Kubernetes

You need to analyze which situation needs which type of mechanism because 
of the complexity of implementation in all the microservices. 
When the request and the response don’t spend a lot of resources and are quick, a good option is to use a cache. But when the request involves a lot of microservices and the response is not static information, 
it’s a good idea to implement synchronization using Zookeeper.

The following table summarize the pros/cons of each mechanism:

|                       | Cache                                                       | Using barrier                                                 |
|-----------------------|-------------------------------------------------------------|---------------------------------------------------------------|
| Advantages            |                                                             |                                                               |
| | - Simple to implement                                       | - There are some implementations like Zookeper or Redis Lock  |
| | - Covers the cases of duplicate request in a different time| - Covers all the cases of duplicate requests at the same time | 
| | - There are a lot of implementations of cache like Caffeine, Guava, Varnish, Redis.|                                                               |
| Disadvantages | |                                                               |
| | - An element to maintain but could be simple depending of the implementation | - A complex element of infrastructure to maintain             |
| | - Does not cover the cases of duplicate requests at the same moment | - Does not cover the cases of duplicate requests in different moments |

Take into consideration that each solution on its own will not resolve the 
problem in all cases, so it’s a good idea to implement both mechanisms in 
combination.


## Part 3 Milestone 2 - Reduce the Number of Duplicate Requests

Either use the IntelliJ IDEA Docker plugin (not shown) or command line tools (see below) to create
the Docker image remotely.

```shell
tar cfz - ./Dockerfile | curl -i -v -T - -XPOST -H "Content-Type: application/x-tar" -H "content-transfer-encoding: binary" $KUBE_SERVER:2375/v1.40/build?t=localhost:32000/twa-zookeeper
```

The service is deployed to Kubernetes using the Docker image created (and which is pushed
to the local Docker repository on the remote server).

Make sure that on the remote server we run the following command:

```shell
ssh $KUBE_SERVER docker push localhost:32000/twa-zookeeper
```

### Create Kubernetes Objects
Use Kubernetes deployment resource to deploy the service.

```shell
kubectl apply -f ./zookeepr-deploy.yaml
```

To access the service from outside the Kubernetes cluster, expose the service through the *NodePort*:

```shell
kubectl apply -f ./zookeeper-svc-np.yaml
```

### Restart Deployment after new (Docker) Image Build

```shell
kubectl scale deployment zookeeper --replicas=0
```

wait for the *Pod* to terminate, then

```shell
kubectl scale deployment zookeeper --replicas=1
```

## Accessing the service from outside the Kubernetes cluster

Once deployed, get the externalised port number:

```shell
kubectl get service
```

    NAME                                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)           AGE
    service/varnish-np                  NodePort    10.152.183.220   <none>        6078:31723/TCP    8d
    service/varnish                     ClusterIP   10.152.183.38    <none>        6078/TCP          8d
    service/api-clusters-remote-cache   NodePort    10.152.183.155   <none>        6079:32565/TCP    8d
    service/zookeeper-np                NodePort    10.152.183.62    <none>        2181:32711/TCP    14h

The last entry reveals the external port number (**32711**) for our Zookeeper service
(*in your case the port numbers may be different*):

    service/zookeeper-np                NodePort    10.152.183.62    <none>        2181:32711/TCP    14h

**Use this external port number and the Kubernetes hosting server hostname within the `api-clusters` microservices
when connecting to Zookeeper.**
