import azure.functions as func
import logging
import json
import re

app = func.FunctionApp()

###########################
# Helper – Luhn algorithm #
###########################

def _luhn_checksum(card_number: str) -> bool:
    """Return True if the supplied card_number passes the Luhn algorithm"""
    # Strip spaces / hyphens so users can send "4111-1111-1111-1111" or "4111 1111 1111 1111"
    digits_only = re.sub(r"[^0-9]", "", card_number)

    # Luhn requires at least two digits
    if len(digits_only) < 2:
        return False

    digits = [int(d) for d in digits_only]
    checksum = 0
    parity = len(digits) % 2

    for i, d in enumerate(digits):
        if i % 2 == parity:
            d *= 2
            if d > 9:
                d -= 9
        checksum += d

    return checksum % 10 == 0

############################################
# Endpoint: /card-validation (GET or POST) #
############################################

@app.route(
    route="card-validation",
    methods=["GET", "POST"],
    auth_level=func.AuthLevel.ANONYMOUS,
)
def card_validation(req: func.HttpRequest) -> func.HttpResponse:
    """Validates a payment-card number using the Luhn algorithm.

    Accepts either:
      • GET  /card-validation?cardNumber=4111111111111111
      • POST /card-validation  JSON body: {"cardNumber": "4111111111111111"}
    Returns JSON → {"cardNumber": "…", "valid": true/false}
    """

    logging.info("Card-validation function triggered")

    # 1️⃣ Try query-string first
    card_number = req.params.get("cardNumber") or req.params.get("card_number")

    # 2️⃣ Fall back to JSON body
    if not card_number:
        try:
            body = req.get_json()
            card_number = body.get("cardNumber") or body.get("card_number")
        except (ValueError, AttributeError):
            body = None  # keep static analysers happy

    # 3️⃣ Bad request if still missing
    if not card_number:
        return func.HttpResponse(
            json.dumps({"error": "Provide cardNumber in query string or JSON body."}),
            status_code=400,
            mimetype="application/json",
        )

    # 4️⃣ Validate using Luhn
    is_valid = _luhn_checksum(card_number)

    # 5️⃣ Create a consistent JSON response
    response = {
        "cardNumber": card_number,
        "valid": is_valid,
    }

    return func.HttpResponse(
        json.dumps(response),
        status_code=200,
        mimetype="application/json",
    )

############################################
# Optional: original demo endpoint remains #
############################################

@app.route(route="MyHttpTrigger", auth_level=func.AuthLevel.ANONYMOUS)
def MyHttpTrigger(req: func.HttpRequest) -> func.HttpResponse:
    logging.info("Python HTTP trigger function processed a request.")

    name = req.params.get("name")
    if not name:
        try:
            req_body = req.get_json()
        except ValueError:
            req_body = {}
        name = req_body.get("name")

    if name:
        return func.HttpResponse(f"Hello, {name}. This HTTP triggered function executed successfully.")

    return func.HttpResponse(
        "This HTTP triggered function executed successfully. Pass a name in the query string or in the request body for a personalized response.",
        status_code=200,
    )
