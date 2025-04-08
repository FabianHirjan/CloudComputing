from flask import Flask, request, jsonify
from google.cloud import datastore
from flask_cors import CORS

app = Flask(__name__)
# Configure CORS to allow requests from your specific origin
CORS(app, resources={r"/*": {"origins": ["https://cc-team-456211.ey.r.appspot.com"], 
                            "methods": ["GET", "POST", "OPTIONS"],
                            "allow_headers": ["Content-Type", "Authorization"]}})

datastore_client = datastore.Client()

@app.route('/appointments', methods=['GET'])
def get_appointments():
    query = datastore_client.query(kind='Appointment')
    appointment_entities = list(query.fetch())
    appointments = []
    for appt in appointment_entities:
        appointments.append({
            'customer_name': appt.get('customer_name'),
            'car_id': appt.get('car_id'),
            'appointment_datetime': appt.get('appointment_datetime')
        })
    return jsonify(appointments)

@app.route('/appointments/add', methods=['POST'])
def add_appointment():
    data = request.get_json()
    required = ['customer_name', 'car_id', 'appointment_datetime']
    if not all(k in data for k in required):
        return jsonify({"error": "Missing fields"}), 400
    key = datastore_client.key('Appointment')
    appointment_entity = datastore.Entity(key=key)
    appointment_entity.update({
        'customer_name': data.get('customer_name'),
        'car_id': int(data.get('car_id')),
        'appointment_datetime': data.get('appointment_datetime')
    })
    datastore_client.put(appointment_entity)
    return jsonify({"message": "Appointment added successfully"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=True)