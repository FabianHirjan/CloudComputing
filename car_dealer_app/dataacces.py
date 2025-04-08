from google.cloud import datastore

datastore_client = datastore.Client()

def add_car(brand, model, year, price):
    key = datastore_client.key('Car')
    entity = datastore.Entity(key)
    entity.update({
        'brand': brand,
        'model': model,
        'year': year,
        'price': price
    })
    datastore_client.put(entity)

def list_cars():
    query = datastore_client.query(kind='Car')
    return list(query.fetch())
