import os
from flask import Flask, render_template, request, redirect, url_for, abort
from google.cloud import datastore

app = Flask(__name__)

datastore_client = datastore.Client()

@app.route('/list_appointments')
def list_appointments():
    """
    Endpoint pentru afișarea listei de programări.
    Preia toate entitățile de tip "Appointment" din Datastore și le pasează către template-ul 'list_appointments.html'.
    """
    query = datastore_client.query(kind='Appointment')
    appointment_entities = list(query.fetch())
    
    # Convertim fiecare entitate într-un dict pentru template
    appointments = []
    for appt in appointment_entities:
        appointments.append({
            'customer_name': appt.get('customer_name'),
            'car_id': appt.get('car_id'),
            'appointment_datetime': appt.get('appointment_datetime')
        })
    
    return render_template('list_appointments.html', appointments=appointments)

@app.route('/add_appointment', methods=['GET', 'POST'])
def add_appointment():
    """
    Endpoint pentru adăugarea unei noi programări.
    Pentru GET: afișează formularul 'add_appointment.html'.
    Pentru POST: validează datele primite, salvează o nouă entitate de tip "Appointment" în Datastore
    și redirecționează către lista de programări.
    """
    if request.method == 'POST':
        customer_name = request.form.get('customer_name')
        car_id = request.form.get('car_id')
        appointment_datetime = request.form.get('appointment_datetime')
        
        if not customer_name or not car_id or not appointment_datetime:
            abort(400, description="Toate câmpurile sunt necesare!")
        
        key = datastore_client.key('Appointment')
        appointment_entity = datastore.Entity(key)
        appointment_entity.update({
            'customer_name': customer_name,
            'car_id': int(car_id),
            'appointment_datetime': appointment_datetime  
        })
        datastore_client.put(appointment_entity)
        
        return redirect(url_for('list_appointments'))
    
    return render_template('add_appointment.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=True)
