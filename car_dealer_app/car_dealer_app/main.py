import os
import requests
from flask import Flask, render_template, request, redirect, url_for
from google.cloud import datastore, storage

app = Flask(__name__)

# Inițializează clienții Google Cloud
datastore_client = datastore.Client()
storage_client = storage.Client()

# Numele bucket-ului în Cloud Storage (asigură-te că l-ai creat în Cloud Console)
BUCKET_NAME = 'car-dealer-bucket'

@app.route('/')
def home():
    return render_template('home.html')

@app.route('/list_cars')
def list_cars_route():
    query = datastore_client.query(kind='Car')
    car_entities = list(query.fetch())
    cars = []
    for car_entity in car_entities:
        # Folosim un identificator comun pentru integrarea cu serviciul de stoc: "brand-model"
        identifier = f"{car_entity.get('brand')}-{car_entity.get('model')}"
        car_data = {
            'brand': car_entity.get('brand'),
            'model': car_entity.get('model'),
            'year': car_entity.get('year'),
            'price': car_entity.get('price'),
            'photo_url': car_entity.get('photo_url', None),
            'identifier': identifier
        }
        cars.append(car_data)
    return render_template('list_cars.html', cars=cars)

@app.route('/add_car', methods=['GET', 'POST'])
def add_car_route():
    if request.method == 'POST':
        brand = request.form.get('brand')
        model = request.form.get('model')
        year = request.form.get('year')
        price = request.form.get('price')
        stock = request.form.get('stock')  # noul câmp pentru stoc
        photo = request.files.get('photo')

        # Salvăm mașina în Cloud Datastore
        key = datastore_client.key('Car')
        car_entity = datastore.Entity(key=key)
        car_entity.update({
            'brand': brand,
            'model': model,
            'year': int(year) if year else None,
            'price': float(price) if price else None,
        })

        # Dacă a fost încărcată o poză, o urcăm în bucket și salvăm URL-ul
        if photo and photo.filename:
            bucket = storage_client.bucket(BUCKET_NAME)
            blob = bucket.blob(photo.filename)
            blob.upload_from_file(photo)
            blob.make_public()
            car_entity['photo_url'] = blob.public_url

        datastore_client.put(car_entity)

        # Actualizăm stocul apelând serviciul de stoc
        stock_service_url = os.environ.get("STOCK_SERVICE_URL", "https://stock-dot-cc-team-456211.appspot.com")
        # Identificatorul pe care îl folosim în ambele servicii: "brand-model"
        car_identifier = f"{brand}-{model}"
        payload = {"item": car_identifier, "quantity": int(stock)}
        try:
            r = requests.post(f"{stock_service_url}/stock/update", json=payload)
            r.raise_for_status()
        except Exception as e:
            print("Error updating stock:", e)

        return redirect(url_for('list_cars_route'))

    return render_template('add_car.html')

if __name__ == '__main__':
    # Rulează local pe port 8080
    app.run(host='0.0.0.0', port=8080, debug=True)
