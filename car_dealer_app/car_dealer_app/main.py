import os
import requests
import datetime
from flask import Flask, render_template, request, redirect, url_for, abort
from google.cloud import datastore, storage

app = Flask(__name__)

# Inițializează clienții pentru Cloud Datastore și Cloud Storage
datastore_client = datastore.Client()
storage_client = storage.Client()
BUCKET_NAME = 'car-dealer-bucket'  # Asigură-te că acest bucket există în Cloud Storage

# Preia URL-urile microserviciilor din variabilele de mediu
SALE_SERVICE_URL = os.environ.get("SALE_SERVICE_URL")
APPOINTMENT_SERVICE_URL = os.environ.get("APPOINTMENT_SERVICE_URL")
STOCK_SERVICE_URL = os.environ.get("STOCK_SERVICE_URL")

##############################
# Rute pentru gestionarea mașinilor
##############################
@app.route('/')
def home():
    return render_template('home.html')

@app.route('/list_cars')
def list_cars():
    query = datastore_client.query(kind='Car')
    car_entities = list(query.fetch())
    cars = []
    for car in car_entities:
        # Folosește un identificator comun pentru stoc: "Brand-Model"
        identifier = f"{car.get('brand')}-{car.get('model')}"
        cars.append({
            'brand': car.get('brand'),
            'model': car.get('model'),
            'year': car.get('year'),
            'price': car.get('price'),
            'photo_url': car.get('photo_url'),
            'identifier': identifier
        })
    return render_template('list_cars.html', cars=cars, STOCK_SERVICE_URL=STOCK_SERVICE_URL)

@app.route('/add_car', methods=['GET', 'POST'])
def add_car():
    if request.method == 'POST':
        brand = request.form.get('brand')
        model = request.form.get('model')
        year = request.form.get('year')
        price = request.form.get('price')
        stock = request.form.get('stock')  # Câmp pentru stoc
        photo = request.files.get('photo')

        if not (brand and model and year and price and stock):
            abort(400, description="Toate câmpurile sunt necesare.")

        # Salvează informațiile mașinii în Datastore
        key = datastore_client.key('Car')
        car_entity = datastore.Entity(key=key)
        car_entity.update({
            'brand': brand,
            'model': model,
            'year': int(year),
            'price': float(price)
        })
        if photo and photo.filename:
            bucket = storage_client.bucket(BUCKET_NAME)
            blob = bucket.blob(photo.filename)
            blob.upload_from_file(photo)
            blob.make_public()
            car_entity['photo_url'] = blob.public_url

        datastore_client.put(car_entity)

        # Setează stocul inițial: identificatorul este "Brand-Model"
        car_identifier = f"{brand}-{model}"
        payload = {"item": car_identifier, "quantity": int(stock)}
        try:
            r = requests.post(f"{STOCK_SERVICE_URL}/stock/update", json=payload)
            r.raise_for_status()
        except Exception as e:
            print("Error updating stock:", e)

        return redirect(url_for('list_cars'))

    return render_template('add_car.html')

##############################
# Rute pentru Sales – apelează microserviciul extern
##############################
@app.route('/list_sales')
def list_sales():
    try:
        response = requests.get(f"{SALE_SERVICE_URL}/sales")
        response.raise_for_status()
        sales = response.json()
    except Exception as e:
        print("Error fetching sales:", e)
        sales = []
    return render_template('list_sales.html', orders=sales)

@app.route('/add_sale', methods=['GET', 'POST'])
def add_sale():
    if request.method == 'POST':
        customer_name = request.form.get('customer_name')
        car_id = request.form.get('car_id')
        order_status = request.form.get('order_status')
        price = request.form.get('price')
        if not (customer_name and car_id and order_status and price):
            abort(400, description="Toate câmpurile sunt necesare!")
        sale_payload = {
            'customer_name': customer_name,
            'car_id': int(car_id),
            'order_status': order_status,
            'price': float(price),
            'order_date': datetime.datetime.utcnow().isoformat()
        }
        try:
            response = requests.post(f"{SALE_SERVICE_URL}/sales/add", json=sale_payload)
            response.raise_for_status()
        except Exception as e:
            print("Error adding sale:", e)
        return redirect(url_for('list_sales'))
    return render_template('add_sale.html')

##############################
# Rute pentru Appointments – apelează microserviciul extern
##############################
@app.route('/list_appointments')
def list_appointments():
    try:
        response = requests.get(f"{APPOINTMENT_SERVICE_URL}/appointments")
        response.raise_for_status()
        appointments = response.json()
    except Exception as e:
        print("Error fetching appointments:", e)
        appointments = []
    return render_template('list_appointments.html', appointments=appointments)

@app.route('/add_appointment', methods=['GET', 'POST'])
def add_appointment():
    if request.method == 'POST':
        customer_name = request.form.get('customer_name')
        car_id = request.form.get('car_id')
        appointment_datetime = request.form.get('appointment_datetime')
        if not (customer_name and car_id and appointment_datetime):
            abort(400, description="Toate câmpurile sunt necesare!")
        payload = {
            'customer_name': customer_name,
            'car_id': int(car_id),
            'appointment_datetime': appointment_datetime
        }
        try:
            response = requests.post(f"{APPOINTMENT_SERVICE_URL}/appointments/add", json=payload)
            response.raise_for_status()
        except Exception as e:
            print("Error adding appointment:", e)
        return redirect(url_for('list_appointments'))
    return render_template('add_appointment.html')

@app.route("/api/locatii", methods=["GET"])
def get_locatii():
    # Retrieve query parameters for brand, lat, lng.
    brand = request.args.get("brand")
    lat = request.args.get("lat")
    lng = request.args.get("lng")

    if not all([brand, lat, lng]):
        return jsonify({"error": "Missing required query parameters: brand, lat, lng"}), 400

    google_api_key = os.getenv("GOOGLE_API_KEY")
    if not google_api_key:
        return jsonify({"error": "GOOGLE_API_KEY environment variable not set"}), 500

    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
    params = {
        "location": f"{lat},{lng}",
        "radius": "100000",  # Adjust the search radius as needed.
        "type": "car_dealer", 
        "name": brand,
        "keyword": brand,
        "key": google_api_key
    }

    try:
        resp = requests.get(url, params=params)
        return jsonify(resp.json()), resp.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)
