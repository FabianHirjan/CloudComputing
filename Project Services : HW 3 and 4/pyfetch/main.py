import pyodbc
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import requests
import logging
import os
import traceback

# Configure logging
logging.basicConfig(level=logging.ERROR)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Car Collection API",
    description="API to manage a collection of cars with links to their Java service endpoints. Fetches car details and states from the specified service URLs.",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# Car model for database and API
class Car(BaseModel):
    car_id: int | None = None
    link: str

# Car details DTO for /details endpoint
class CarDetails(BaseModel):
    make: str
    model: str
    lastLongitude: float
    lastLatitude: float

# Database connection
def get_db_connection():
    try:
        conn_str = (
            "Driver={ODBC Driver 18 for SQL Server};"
            "Server=databasesv.database.windows.net;"
            "Database=hopa;"
            "UID=fabean;"
            "PWD=fabipacO123;"
            "Encrypt=yes;"
            "TrustServerCertificate=no;"
            "HostNameInCertificate=*.database.windows.net;"
            "LoginTimeout=30;"
        )
        conn = pyodbc.connect(conn_str)
        return conn
    except Exception as e:
        logger.error(f"Database connection error: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Database connection error: {str(e)}")

# CRUD Operations
@app.post("/api/cars", response_model=Car, summary="Create a new car", description="Add a new car with a link to its Java service.")
async def create_car(car: Car):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("INSERT INTO Cars (link) VALUES (?)", (car.link,))
        conn.commit()
        cursor.execute("SELECT @@IDENTITY AS id")
        car_id = int(cursor.fetchone()[0])
        car.car_id = car_id
        return car
    except Exception as e:
        logger.error(f"Error creating car: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Error creating car: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.get("/api/cars", response_model=List[Car], summary="Get all cars", description="Retrieve a list of all cars and their service links.")
async def get_all_cars():
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT car_id, link FROM Cars")
        rows = cursor.fetchall()
        cars = [Car(car_id=row.car_id, link=row.link) for row in rows]
        return cars
    except Exception as e:
        logger.error(f"Error retrieving cars: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Error retrieving cars: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.get("/api/cars/{car_id}", response_model=Car, summary="Get a specific car", description="Retrieve the details of a car by its ID.")
async def get_car(car_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT car_id, link FROM Cars WHERE car_id = ?", (car_id,))
        row = cursor.fetchone()
        if not row:
            raise HTTPException(status_code=404, detail="Car not found")
        return Car(car_id=row.car_id, link=row.link)
    except Exception as e:
        logger.error(f"Error retrieving car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Error retrieving car: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.put("/api/cars/{car_id}", response_model=Car, summary="Update a car", description="Update the service link for a specific car.")
async def update_car(car_id: int, car: Car):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT * FROM Cars WHERE car_id = ?", (car_id,))
        if not cursor.fetchone():
            raise HTTPException(status_code=404, detail="Car not found")
        cursor.execute("UPDATE Cars SET link = ? WHERE car_id = ?", (car.link, car_id))
        conn.commit()
        car.car_id = car_id
        return car
    except Exception as e:
        logger.error(f"Error updating car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Error updating car: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.delete("/api/cars/{car_id}", summary="Delete a car", description="Remove a car from the collection by its ID.")
async def delete_car(car_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT * FROM Cars WHERE car_id = ?", (car_id,))
        if not cursor.fetchone():
            raise HTTPException(status_code=404, detail="Car not found")
        cursor.execute("DELETE FROM Cars WHERE car_id = ?", (car_id,))
        conn.commit()
        return {"message": "Car deleted successfully"}
    except Exception as e:
        logger.error(f"Error deleting car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Error deleting car: {str(e)}")
    finally:
        cursor.close()
        conn.close()

# Fetch Endpoints
@app.get("/api/cars/{car_id}/details", response_model=CarDetails, summary="Get car details", description="Fetch car details (make, model, location) from the car's Java service.")
async def get_car_details(car_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT link FROM Cars WHERE car_id = ?", (car_id,))
        row = cursor.fetchone()
        if not row:
            raise HTTPException(status_code=404, detail="Car not found")
        link = row.link.rstrip('/')
        try:
            response = requests.get(f"{link}/api/car/details")
            response.raise_for_status()
            data = response.json()
            return CarDetails(
                make=data["make"],
                model=data["model"],
                lastLongitude=data["lastLongitude"],
                lastLatitude=data["lastLatitude"]
            )
        except requests.RequestException as e:
            logger.error(f"Error fetching details from {link}/api/car/details: {str(e)}\n{traceback.format_exc()}")
            raise HTTPException(status_code=500, detail=f"Error fetching car details from {link}: {str(e)}")
    except Exception as e:
        logger.error(f"Database error for car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.get("/api/cars/{car_id}/check-unrent", summary="Check unrent status", description="Check if the car is unrented (lights off, engine off, locked) via its Java service.")
async def check_unrent(car_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT link FROM Cars WHERE car_id = ?", (car_id,))
        row = cursor.fetchone()
        if not row:
            raise HTTPException(status_code=404, detail="Car not found")
        link = row.link.rstrip('/')
        try:
            response = requests.get(f"{link}/api/car/check-unrent")
            response.raise_for_status()
            # Java service returns plain text: "Car is unrented" or "Car is not unrented"
            return {"message": response.text.strip()}
        except requests.RequestException as e:
            logger.error(f"Error fetching unrent status from {link}/api/car/check-unrent: {str(e)}\n{traceback.format_exc()}")
            raise HTTPException(status_code=500, detail=f"Error fetching unrent status from {link}: {str(e)}")
    except Exception as e:
        logger.error(f"Database error for car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.post("/api/cars/{car_id}/lock", summary="Lock the car", description="Lock the car via its Java service.")
async def lock_car(car_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT link FROM Cars WHERE car_id = ?", (car_id,))
        row = cursor.fetchone()
        if not row:
            raise HTTPException(status_code=404, detail="Car not found")
        link = row.link.rstrip('/')
        try:
            response = requests.post(f"{link}/api/car/lock")
            response.raise_for_status()
            # Java service returns plain text: "Car locked successfully"
            return {"message": response.text.strip()}
        except requests.RequestException as e:
            logger.error(f"Error locking car at {link}/api/car/lock: {str(e)}\n{traceback.format_exc()}")
            raise HTTPException(status_code=500, detail=f"Error locking car at {link}: {str(e)}")
    except Exception as e:
        logger.error(f"Database error for car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")
    finally:
        cursor.close()
        conn.close()

@app.post("/api/cars/{car_id}/unlock", summary="Unlock the car", description="Unlock the car via its Java service.")
async def unlock_car(car_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT link FROM Cars WHERE car_id = ?", (car_id,))
        row = cursor.fetchone()
        if not row:
            raise HTTPException(status_code=404, detail="Car not found")
        link = row.link.rstrip('/')
        try:
            response = requests.post(f"{link}/api/car/unlock")
            response.raise_for_status()
            # Java service returns plain text: "Car unlocked successfully"
            return {"message": response.text.strip()}
        except requests.RequestException as e:
            logger.error(f"Error unlocking car at {link}/api/car/unlock: {str(e)}\n{traceback.format_exc()}")
            raise HTTPException(status_code=500, detail=f"Error unlocking car at {link}: {str(e)}")
    except Exception as e:
        logger.error(f"Database error for car {car_id}: {str(e)}\n{traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")
    finally:
        cursor.close()
        conn.close()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)