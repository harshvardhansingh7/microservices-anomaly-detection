# from flask import Flask, request, jsonify
# import numpy as np
# import pandas as pd
# from sklearn.ensemble import IsolationForest
# from sklearn.preprocessing import StandardScaler
# import joblib
# import os
# from datetime import datetime
# import json
#
# app = Flask(__name__)
#
# class AnomalyDetector:
#     def __init__(self):
#         self.model = None
#         self.scaler = StandardScaler()
#         self.model_path = "model/isolation_forest.pkl"
#         self.scaler_path = "model/scaler.pkl"
#         self.is_trained = False
#         self.initialize_model()
#
#     def initialize_model(self):
#         """Initialize or load existing model"""
#         os.makedirs("model", exist_ok=True)
#
#         if os.path.exists(self.model_path) and os.path.exists(self.scaler_path):
#             try:
#                 self.model = joblib.load(self.model_path)
#                 self.scaler = joblib.load(self.scaler_path)
#                 self.is_trained = True
#                 print("Loaded pre-trained model")
#             except:
#                 self.create_new_model()
#         else:
#             self.create_new_model()
#
#     def create_new_model(self):
#         """Create a new Isolation Forest model"""
#         self.model = IsolationForest(
#             n_estimators=100,
#             contamination=0.1,
#             random_state=42
#         )
#         self.is_trained = False
#         print("Created new model - waiting for training data")
#
#     def preprocess_data(self, logs):
#         """Convert log data to feature matrix"""
#         features = []
#         for log in logs:
#             feature_vector = [
#                 log['latency'],
#                 log['errorRate'] * 100,  # Convert to percentage
#                 log['userCount'],
#                 log['memoryUsage'],
#                 log['cpuUsage']
#             ]
#             features.append(feature_vector)
#
#         return np.array(features)
#
#     def train_model(self, features):
#         """Train the model with normal data"""
#         try:
#             # Fit scaler and transform features
#             scaled_features = self.scaler.fit_transform(features)
#
#             # Train Isolation Forest
#             self.model.fit(scaled_features)
#             self.is_trained = True
#
#             # Save model and scaler
#             joblib.dump(self.model, self.model_path)
#             joblib.dump(self.scaler, self.scaler_path)
#
#             print("Model trained and saved successfully")
#             return True
#         except Exception as e:
#             print(f"Error training model: {e}")
#             return False
#
#     def detect(self, logs):
#         """Detect anomalies in log data"""
#         if not self.is_trained:
#             # If model not trained, use simple rule-based detection
#             return self.rule_based_detection(logs)
#
#         try:
#             features = self.preprocess_data(logs)
#             scaled_features = self.scaler.transform(features)
#
#             # Get anomaly scores (-1 for anomalies, 1 for normal)
#             scores = self.model.decision_function(scaled_features)
#             predictions = self.model.predict(scaled_features)
#
#             # Convert to anomaly scores (0-1, where 1 is most anomalous)
#             anomaly_scores = 1 - (scores - scores.min()) / (scores.max() - scores.min())
#
#             results = []
#             for i, log in enumerate(logs):
#                 is_anomaly = predictions[i] == -1
#                 results.append({
#                     'anomalyId': None,  # Will be set by consumer
#                     'serviceName': log['serviceName'],
#                     'anomalyScore': float(anomaly_scores[i]),
#                     'anomaly': bool(is_anomaly),
#                     'timestamp': datetime.now().isoformat(),
#                     'originalLog': log
#                 })
#
#             return results
#         except Exception as e:
#             print(f"Error in anomaly detection: {e}")
#             return self.rule_based_detection(logs)
#
#     def rule_based_detection(self, logs):
#         """Fallback rule-based anomaly detection"""
#         results = []
#         for log in logs:
#             score = 0.0
#             is_anomaly = False
#
#             # Simple rules for anomaly detection
#             if log['latency'] > 500:
#                 score = max(score, 0.8)
#                 is_anomaly = True
#             elif log['latency'] > 300:
#                 score = max(score, 0.6)
#
#             if log['errorRate'] > 0.3:
#                 score = max(score, 0.9)
#                 is_anomaly = True
#             elif log['errorRate'] > 0.1:
#                 score = max(score, 0.7)
#
#             if log['memoryUsage'] > 90:
#                 score = max(score, 0.8)
#                 is_anomaly = True
#
#             if log['cpuUsage'] > 85:
#                 score = max(score, 0.7)
#                 is_anomaly = score > 0.6
#
#             results.append({
#                 'anomalyId': None,
#                 'serviceName': log['serviceName'],
#                 'anomalyScore': score,
#                 'anomaly': is_anomaly,
#                 'timestamp': datetime.now().isoformat(),
#                 'originalLog': log
#             })
#
#         return results
#
# # Global anomaly detector instance
# detector = AnomalyDetector()
#
# @app.route('/detect-anomalies', methods=['POST'])
# def detect_anomalies():
#     try:
#         logs = request.get_json()
#
#         if not logs:
#             return jsonify({'error': 'No logs provided'}), 400
#
#         # Train model if we have enough normal data and model isn't trained
#         if not detector.is_trained and len(logs) >= 50:
#             features = detector.preprocess_data(logs)
#             detector.train_model(features)
#
#         results = detector.detect(logs)
#         return jsonify(results)
#
#     except Exception as e:
#         return jsonify({'error': str(e)}), 500
#
# @app.route('/health', methods=['GET'])
# def health():
#     return jsonify({
#         'status': 'healthy',
#         'model_trained': detector.is_trained
#     })
#
# if __name__ == '__main__':
#     app.run(host='0.0.0.0', port=5000, debug=True)

from flask import Flask, request, jsonify
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
import joblib
import os
from datetime import datetime

app = Flask(__name__)

class AnomalyDetector:
    def __init__(self):
        self.model = None
        self.scaler = StandardScaler()
        self.model_path = "model/isolation_forest.pkl"
        self.scaler_path = "model/scaler.pkl"
        self.is_trained = False
        self.initialize_model()

    def initialize_model(self):
        os.makedirs("model", exist_ok=True)
        if os.path.exists(self.model_path) and os.path.exists(self.scaler_path):
            try:
                self.model = joblib.load(self.model_path)
                self.scaler = joblib.load(self.scaler_path)
                self.is_trained = True
                print("Loaded pre-trained model")
            except:
                self.create_new_model()
        else:
            self.create_new_model()

    def create_new_model(self):
        self.model = IsolationForest(n_estimators=100, contamination=0.1, random_state=42)
        self.is_trained = False
        print("Created new model - waiting for training data")

    def preprocess_data(self, logs):
        features = []
        for log in logs:
            features.append([
                log['latency'],
                log['errorRate'] * 100,
                log['userCount'],
                log['memoryUsage'],
                log['cpuUsage']
            ])
        return np.array(features)

    def train_model(self, features):
        try:
            scaled_features = self.scaler.fit_transform(features)
            self.model.fit(scaled_features)
            self.is_trained = True
            joblib.dump(self.model, self.model_path)
            joblib.dump(self.scaler, self.scaler_path)
            print("Model trained and saved successfully")
            return True
        except Exception as e:
            print(f"Error training model: {e}")
            return False

    def detect(self, logs):
        if not self.is_trained:
            return self.rule_based_detection(logs)
        try:
            features = self.preprocess_data(logs)
            scaled_features = self.scaler.transform(features)
            scores = self.model.decision_function(scaled_features)
            predictions = self.model.predict(scaled_features)
            anomaly_scores = 1 - (scores - scores.min()) / (scores.max() - scores.min())

            results = []
            for i, log in enumerate(logs):
                is_anomaly = predictions[i] == -1
                log_copy = log.copy()  # Ensure timestamp is ISO
                log_copy['timestamp'] = datetime.now().isoformat()
                results.append({
                    'anomalyId': None,
                    'serviceName': log['serviceName'],
                    'anomalyScore': float(anomaly_scores[i]),
                    'anomaly': bool(is_anomaly),
                    'timestamp': datetime.now().isoformat(),
                    'originalLog': log_copy
                })
            return results
        except Exception as e:
            print(f"Error in anomaly detection: {e}")
            return self.rule_based_detection(logs)

    def rule_based_detection(self, logs):
        results = []
        for log in logs:
            score = 0.0
            is_anomaly = False
            if log['latency'] > 500:
                score = max(score, 0.8)
                is_anomaly = True
            elif log['latency'] > 300:
                score = max(score, 0.6)
            if log['errorRate'] > 0.3:
                score = max(score, 0.9)
                is_anomaly = True
            elif log['errorRate'] > 0.1:
                score = max(score, 0.7)
            if log['memoryUsage'] > 90:
                score = max(score, 0.8)
                is_anomaly = True
            if log['cpuUsage'] > 85:
                score = max(score, 0.7)
                is_anomaly = score > 0.6

            log_copy = log.copy()
            log_copy['timestamp'] = datetime.now().isoformat()
            results.append({
                'anomalyId': None,
                'serviceName': log['serviceName'],
                'anomalyScore': score,
                'anomaly': is_anomaly,
                'timestamp': datetime.now().isoformat(),
                'originalLog': log_copy
            })
        return results

# Global detector
detector = AnomalyDetector()

@app.route('/detect-anomalies', methods=['POST'])
def detect_anomalies():
    try:
        logs = request.get_json()
        if not logs:
            return jsonify({'error': 'No logs provided'}), 400

        if not detector.is_trained and len(logs) >= 50:
            features = detector.preprocess_data(logs)
            detector.train_model(features)

        results = detector.detect(logs)
        return jsonify(results)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        'status': 'healthy',
        'model_trained': detector.is_trained
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
