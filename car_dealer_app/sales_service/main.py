import datetime
from flask import Flask, request, jsonify
from google.cloud import datastore
from flask_cors import CORS

app = Flask(__name__)
# Configure CORS to allow requests from your specific origin
CORS(app, resources={r"/*": {"origins": ["https://cc-team-456211.ey.r.appspot.com"], 
                            "methods": ["GET", "POST", "OPTIONS"],
                            "allow_headers": ["Content-Type", "Authorization"]}})

datastore_client = datastore.Client()

@app.route('/sales', methods=['GET'])
def get_sales():
    try:
        query = datastore_client.query(kind='Order')
        order_entities = list(query.fetch())
        orders = []
        for order in order_entities:
            orders.append({
                'customer_name': order.get('customer_name'),
                'car_id': order.get('car_id'),
                'order_status': order.get('order_status'),
                'price': order.get('price'),
                'order_date': order.get('order_date')
            })
        return jsonify(orders)
    except Exception as e:
        print(f"Error fetching sales: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/sales/add', methods=['POST'])
def add_sale():
    try:
        data = request.get_json()
        required = ['customer_name', 'car_id', 'order_status', 'price', 'order_date']
        if not all(k in data for k in required):
            return jsonify({"error": "Missing fields"}), 400
        key = datastore_client.key('Order')
        order_entity = datastore.Entity(key=key)
        order_entity.update({
            'customer_name': data.get('customer_name'),
            'car_id': int(data.get('car_id')),
            'order_status': data.get('order_status'),
            'price': float(data.get('price')),
            'order_date': data.get('order_date')
        })
        datastore_client.put(order_entity)
        return jsonify({"message": "Sale added successfully"}), 200
    except Exception as e:
        print(f"Error adding sale: {str(e)}")
        return jsonify({"error": str(e)}), 500

# Add a health check endpoint
@app.route('/', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8100, debug=True)
