from flask import Flask, request, jsonify
from google.cloud import datastore
from flask_cors import CORS

app = Flask(__name__)
CORS(app)
datastore_client = datastore.Client()

@app.route('/stock', methods=['GET'])
def get_stock():
    query = datastore_client.query(kind='Stock')
    items = list(query.fetch())
    stocks = [{k: v for k, v in item.items()} for item in items]
    return jsonify(stocks)

@app.route('/stock/update', methods=['POST'])
def update_stock():
    data = request.get_json()
    item_name = data.get("item")
    quantity = data.get("quantity")
    if not item_name or quantity is None:
        return jsonify({"error": "Lipsesc parametrii 'item' sau 'quantity'"}), 400
    key = datastore_client.key('Stock', item_name)
    stock_entity = datastore.Entity(key=key)
    stock_entity.update({
        'item': item_name,
        'quantity': quantity
    })
    datastore_client.put(stock_entity)
    return jsonify({"message": f"Stocul pentru {item_name} actualizat la {quantity}."}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8081, debug=True)
