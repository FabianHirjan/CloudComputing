import os
from flask import Flask, render_template, request, redirect, url_for
from google.cloud import datastore, storage

app = Flask(__name__)

# Inițializează clienții GCP (Datastore și Storage)
datastore_client = datastore.Client()
storage_client = storage.Client()

# Numele bucket-ului (trebuie să-l creezi în Cloud Storage)
BUCKET_NAME = 'car-dealer-bucket'

@app.route('/')
def home():
    return render_template('home.html')

@app.route('/list_cars')
def list_cars_route():
    query = datastore_client.query(kind='Car')
    car_entities = list(query.fetch())

    # convertim object->dict pentru a accesa mai usor in template
    cars = []
    for car_entity in car_entities:
        car_data = {
            'brand': car_entity.get('brand'),
            'model': car_entity.get('model'),
            'year': car_entity.get('year'),
            'price': car_entity.get('price'),
            'photo_url': car_entity.get('photo_url', None)
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
        photo = request.files.get('photo')

        # Salvăm entitatea Car în Datastore
        key = datastore_client.key('Car')
        car_entity = datastore.Entity(key)
        car_entity.update({
            'brand': brand,
            'model': model,
            'year': int(year) if year else None,
            'price': float(price) if price else None,
        })

        # Dacă s-a încărcat o poză, o urcăm în Cloud Storage și stocăm URL-ul
        if photo and photo.filename:
            bucket = storage_client.bucket(BUCKET_NAME)
            blob = bucket.blob(photo.filename)
            blob.upload_from_file(photo)
            # Faci public obiectul sau gestionezi accesul cum dorești
            blob.make_public()

            # Stocăm link-ul public în Datastore
            car_entity['photo_url'] = blob.public_url

        datastore_client.put(car_entity)

        return redirect(url_for('list_cars_route'))

    # Metodă GET -> arătăm formularul
    return render_template('add_car.html')

if __name__ == '__main__':
    # Pentru rulare locală (debug)
    app.run(host='0.0.0.0', port=8080, debug=True)
