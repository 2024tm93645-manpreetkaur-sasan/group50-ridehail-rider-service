#!/bin/bash
set -e

# --- Configuration ---
PROFILE="rider-cluster"
NAMESPACE="rhf"
CPUS="6"
MEMORY="6144"   # Adjusted for Docker Desktop (default max ≈ 8 GB)
CACHE_IMAGES=(
  "postgres:16"
  "docker.elastic.co/elasticsearch/elasticsearch:8.15.2"
  "docker.elastic.co/kibana/kibana:8.15.2"
  "docker.elastic.co/logstash/logstash:8.15.2"
)

# --- Helper Function ---
check_cmd() { command -v "$1" >/dev/null 2>&1 || { echo "❌ $1 not found. Please install it."; exit 1; }; }

# --- Preflight ---
check_cmd minikube
check_cmd kubectl
check_cmd docker
check_cmd curl

echo "🏁 Starting Minikube cluster '$PROFILE' with ${CPUS} CPUs and ${MEMORY} MB RAM..."
minikube start -p "$PROFILE" --cpus="$CPUS" --memory="$MEMORY"

echo "🔧 Setting Docker context to Minikube..."
eval "$(minikube -p "$PROFILE" docker-env)"

# --- Preload Common Images ---
echo "💾 Loading common base images into Minikube..."
for img in "${CACHE_IMAGES[@]}"; do
  if ! minikube -p "$PROFILE" ssh "docker images $img --format '{{.Repository}}:{{.Tag}}'" | grep -q "$img"; then
    echo "➡️  Loading $img into Minikube (pulling once locally)..."
    docker pull "$img"
    minikube -p "$PROFILE" image load "$img"
  else
    echo "✅ $img already present in Minikube"
  fi
done

# --- Build Custom Application Image ---
echo "🏗️  Building Rider Service Docker image inside Minikube..."
docker build -t rider-service:latest ./rider-service

# --- Ensure Namespace Exists ---
echo "📦 Ensuring namespace '$NAMESPACE' exists..."
kubectl get ns "$NAMESPACE" >/dev/null 2>&1 || kubectl create ns "$NAMESPACE"

# --- Deploy Core Components ---
echo "🐘 Deploying PostgreSQL..."
kubectl apply -f k8s/rider/postgres.yaml -n "$NAMESPACE"

echo "⚙️  Applying ConfigMap and Secrets..."
kubectl apply -f k8s/rider/configmap.yaml -n "$NAMESPACE"
kubectl apply -f k8s/rider/secret.yaml -n "$NAMESPACE" || true

echo "☕ Deploying Rider Service..."
kubectl apply -f k8s/rider/deployment.yaml -n "$NAMESPACE"
kubectl apply -f k8s/rider/service.yaml -n "$NAMESPACE"

echo "🕐 Giving Kubernetes a few seconds to register pods..."
sleep 5

# --- Wait for Core Pods ---
echo "🕒 Waiting for core pods to be Ready..."
kubectl wait --for=condition=Ready pod -l app=rider-db -n "$NAMESPACE" --timeout=180s || true
kubectl wait --for=condition=Ready pod -l app=rider-service -n "$NAMESPACE" --timeout=180s || true

# --- Deploy ELK Stack (optional heavy components) ---
echo "📊 Deploying ELK Stack..."
kubectl apply -f k8s/elk/elasticsearch.yaml -n "$NAMESPACE"
kubectl apply -f k8s/elk/logstash.yaml -n "$NAMESPACE"
kubectl apply -f k8s/elk/kibana.yaml -n "$NAMESPACE"
kubectl apply -f k8s/elk/filebeat-configmap.yaml -n "$NAMESPACE"
kubectl apply -f k8s/elk/filebeat-daemonset.yaml -n "$NAMESPACE"

# --- Service Health Checks ---
MINIKUBE_IP=$(minikube -p "$PROFILE" ip)
RIDER_NODEPORT=$(kubectl get svc rider-service -n "$NAMESPACE" -o=jsonpath='{.spec.ports[0].nodePort}')
RIDER_URL="http://${MINIKUBE_IP}:${RIDER_NODEPORT}"

echo "🌍 Checking Rider Service health at $RIDER_URL..."
for i in {1..30}; do
  if curl -sf "$RIDER_URL/actuator/health" | grep -q "UP"; then
    echo "✅ Rider Service is UP!"
    break
  else
    echo "⏳ Waiting for Rider Service..."
    sleep 5
  fi
done

# --- Kibana Health Check (optional) ---
if kubectl get svc kibana -n "$NAMESPACE" >/dev/null 2>&1; then
  KIBANA_NODEPORT=$(kubectl get svc kibana -n "$NAMESPACE" -o=jsonpath='{.spec.ports[0].nodePort}')
  KIBANA_URL="http://${MINIKUBE_IP}:${KIBANA_NODEPORT}"
  echo "🌐 Checking Kibana readiness..."
  for i in {1..40}; do
    if curl -sf "$KIBANA_URL/api/status" | grep -q '"level":"available"'; then
      echo "✅ Kibana is ready!"
      break
    else
      echo "⏳ Waiting for Kibana..."
      sleep 8
    fi
  done
fi

# --- Dashboard + Links ---
echo "📟 Launching Minikube dashboard in background..."
(minikube -p "$PROFILE" dashboard >/dev/null 2>&1 &)

echo ""
echo "🎉 Deployment complete!"
echo "🔗 Rider Service → $RIDER_URL"
[ -n "$KIBANA_URL" ] && echo "🔗 Kibana → $KIBANA_URL"
echo ""

