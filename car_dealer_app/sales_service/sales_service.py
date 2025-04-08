import os
import datetime
from flask import Flask, render_template, request, redirect, url_for, abort
from google.cloud import datastore

app = Flask(__name__)

datastore_client = datastore.Client()

@app.route('/list_sales')
def list_sales():
    """
    Endpoint pentru afișarea listei de comenzi de vânzare.
    Preia toate entitățile de tip "Order" din Datastore și le pasează către template-ul 'list_sales.html'.
    """
    query = datastore_client.query(kind='Order')
    order_entities = list(query.fetch())
    
    orders = []
    for order in order_entities:
        orders.append({
            'customer_name': order.get('customer_name'),
            'car_id': order.get('car_id'),
            'order_status': order.get('order_status'),
            'price': order.get('price'),
            'order_date': order.get('order_date')  # se stochează data comenzii (ISO format)
        })
    
    return render_template('list_sales.html', orders=orders)

@app.route('/add_sale', methods=['GET', 'POST'])
def add_sale():
    """
    Endpoint pentru adăugarea unei noi comenzi de vânzare.
    Pentru GET: afișează formularul 'add_sale.html'.
    Pentru POST: procesează datele primite, salvează o nouă entitate de tip "Order" în Datastore,
    iar apoi redirecționează către lista de comenzi.
    """
    if request.method == 'POST':
        customer_name = request.form.get('customer_name')
        car_id = request.form.get('car_id')
        order_status = request.form.get('order_status')
        price = request.form.get('price')
        
        order_date = datetime.datetime.utcnow().isoformat()
        
        if not customer_name or not car_id or not order_status or not price:
            abort(400, description="Toate câmpurile sunt necesare!")
        
        key = datastore_client.key('Order')
        order_entity = datastore.Entity(key)
        order_entity.update({
            'customer_name': customer_name,
            'car_id': int(car_id),
            'order_status': order_status,
            'price': float(price),
            'order_date': order_date
        })
        datastore_client.put(order_entity)
        
        return redirect(url_for('list_sales'))
    
    return render_template('add_sale.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8100, debug=True)
