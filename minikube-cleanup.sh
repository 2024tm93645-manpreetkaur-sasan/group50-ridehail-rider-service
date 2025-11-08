#!/bin/bash
set -e

PROFILE="rider-cluster"
NAMESPACE="rhf"

echo "🧹 Starting cleanup for profile '$PROFILE'..."

# --- Optional flag check ---
FULL_CLEAN=false
if [[ "$1" == "--full" ]]; then
  FULL_CLEAN=true
  echo "⚠️ Full cleanup mode: cached images will also be deleted!"
fi

# --- Delete namespace resources ---
if kubectl get ns $NAMESPACE >/dev/null 2>&1; then
  echo ">>> Deleting all resources in namespace '$NAMESPACE'..."
  kubectl delete ns $NAMESPACE --ignore-not-found --wait
else
  echo "✅ Namespace '$NAMESPACE' not found — skipping."
fi

# --- Stop Minikube safely ---
if minikube -p $PROFILE status >/dev/null 2>&1; then
  echo ">>> Stopping Minikube profile '$PROFILE'..."
  minikube stop -p $PROFILE
else
  echo "✅ Minikube profile '$PROFILE' not running — skipping stop."
fi

# --- Delete cluster ---
echo ">>> Deleting Minikube cluster '$PROFILE'..."
minikube delete -p $PROFILE || true

# --- Optionally clear cache ---
if [ "$FULL_CLEAN" = true ]; then
  echo ">>> Removing all cached images..."
  minikube cache delete || true
else
  echo "✅ Cached images preserved (postgres, elasticsearch, kibana, logstash, etc.)"
fi

echo "🧽 Cleanup complete."
