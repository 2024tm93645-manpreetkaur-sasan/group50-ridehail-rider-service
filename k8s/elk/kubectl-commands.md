# 1. Apply updated ConfigMap
kubectl apply -f filebeat-configmap.yaml -n rhf

# 2. Apply updated DaemonSet
kubectl apply -f filebeat-daemonset.yaml -n rhf

# 3. Restart the DaemonSet to pick up new config
kubectl rollout restart daemonset/filebeat -n rhf

# 4. Verify the pods are running
kubectl get pods -n rhf -l app=filebeat

# 5. Check logs for confirmation
kubectl logs -f daemonset/filebeat -n rhf

kubectl apply -f logstash.yaml
kubectl rollout restart deployment/logstash -n rhf

kubectl get pods -n rhf -l app=logstash

kubectl logs -n rhf deployment/logstash | grep "Pipeline started"


kubectl get pods -n rhf

minikube profile list