import json
import os

# Define the paths
BASE_DIR = "c:\\Development\\Owner\\CarMoneyPit\\src\\main\\resources\\data"
PATHS = {
    "models": os.path.join(BASE_DIR, "car_models.json"),
    "market": os.path.join(BASE_DIR, "model_market.json"),
    "reliability": os.path.join(BASE_DIR, "model_reliability.json"),
    "faults": os.path.join(BASE_DIR, "major_faults.json")
}

BATCH_2_MODELS = [
    {
        "model_def": {"id": "chevrolet_tahoe_4th", "brand": "CHEVROLET", "model": "Tahoe", "generation": "4th Gen (K2UC)", "start_year": 2015, "end_year": 2020},
        "market": {"model_id": "chevrolet_tahoe_4th", "jan_2026_avg_price": 32000, "depreciation_rate": 0.12, "avg_annual_repair_cost": 950, "depreciation_outlook": "Moderate"},
        "reliability": {"model_id": "chevrolet_tahoe_4th", "score": 55, "lifespan_miles": 220000, "best_years": [2019, 2020], "worst_years": [2015, 2016], "common_trouble_spots": ["Lifter Failure", "Transmission Shudder", "Magnetic Ride"], "critical_milestones": [{"mileage": 100000, "description": "AFM/DFM Lifter Check", "est_cost": 3500}]},
        "faults": {"model_id_ref": "chevrolet_tahoe_4th", "faults": [{"component": "AFM Lifter Collapse", "symptoms": "Ticking noise, misfire, 'Service Stabilitrak'", "repairCost": 4200, "verdictImplication": "Severe: High failure rate on early L83 engines. Requires partial engine teardown."}]}
    },
    {
        "model_def": {"id": "toyota_sienna_xl30", "brand": "TOYOTA", "model": "Sienna", "generation": "3rd Gen", "start_year": 2011, "end_year": 2020},
        "market": {"model_id": "toyota_sienna_xl30", "jan_2026_avg_price": 18500, "depreciation_rate": 0.08, "avg_annual_repair_cost": 550, "depreciation_outlook": "Low"},
        "reliability": {"model_id": "toyota_sienna_xl30", "score": 82, "lifespan_miles": 300000, "best_years": [2015, 2016], "worst_years": [2011], "common_trouble_spots": ["Sliding Door Cables", "Transmission Flare"], "critical_milestones": [{"mileage": 120000, "description": "Spark Plugs (Intake Removal Needed)", "est_cost": 650}]},
        "faults": {"model_id_ref": "toyota_sienna_xl30", "faults": [{"component": "Power Sliding Door Failure", "symptoms": "Door won't open or close automatically", "repairCost": 1600, "verdictImplication": "Moderate: Common convenience failure. Cable and motor assembly replacement."}]}
    },
    {
        "model_def": {"id": "nissan_sentra_b17_pit", "brand": "NISSAN", "model": "Sentra", "generation": "B17", "start_year": 2013, "end_year": 2019},
        "market": {"model_id": "nissan_sentra_b17_pit", "jan_2026_avg_price": 9500, "depreciation_rate": 0.18, "avg_annual_repair_cost": 600, "depreciation_outlook": "High"},
        "reliability": {"model_id": "nissan_sentra_b17_pit", "score": 35, "lifespan_miles": 140000, "best_years": [2019], "worst_years": [2013, 2014, 2015], "common_trouble_spots": ["CVT Failure", "Paint Peeling"], "critical_milestones": [{"mileage": 80000, "description": "CVT Replacement Risk", "est_cost": 4200}]},
        "faults": {"model_id_ref": "nissan_sentra_b17_pit", "faults": [{"component": "CVT Transmission Failure", "symptoms": "Whining, juddering, loss of power", "repairCost": 4500, "verdictImplication": "Critical: Cost often exceeds 50% of vehicle market value. SELL RECOMMENDED."}]}
    },
     {
        "model_def": {"id": "jeep_grand_cherokee_wk2_pit", "brand": "JEEP", "model": "Grand Cherokee", "generation": "WK2", "start_year": 2011, "end_year": 2021},
        "market": {"model_id": "jeep_grand_cherokee_wk2_pit", "jan_2026_avg_price": 17500, "depreciation_rate": 0.14, "avg_annual_repair_cost": 900, "depreciation_outlook": "Moderate"},
        "reliability": {"model_id": "jeep_grand_cherokee_wk2_pit", "score": 48, "lifespan_miles": 175000, "best_years": [2019, 2020], "worst_years": [2011, 2012, 2014], "common_trouble_spots": ["Air Suspension", "Oil Cooler Leak", "TIPM"], "critical_milestones": [{"mileage": 100000, "description": "Oil Filter Housing/Cooler Leak", "est_cost": 1200}]},
        "faults": {"model_id_ref": "jeep_grand_cherokee_wk2_pit", "faults": [{"component": "Quadra-Lift Air Suspension Failure", "symptoms": "Vehicle sagging, bumpy ride, compressor running constantly", "repairCost": 3200, "verdictImplication": "High Risk: Expensive Nitro-charged system. Consider coil spring conversion."}]}
    },
    {
        "model_def": {"id": "volkswagen_jetta_mk6", "brand": "VOLKSWAGEN", "model": "Jetta", "generation": "MK6", "start_year": 2011, "end_year": 2018},
        "market": {"model_id": "volkswagen_jetta_mk6", "jan_2026_avg_price": 10500, "depreciation_rate": 0.15, "avg_annual_repair_cost": 750, "depreciation_outlook": "High"},
        "reliability": {"model_id": "volkswagen_jetta_mk6", "score": 52, "lifespan_miles": 180000, "best_years": [2017, 2018], "worst_years": [2011, 2012], "common_trouble_spots": ["Timing Chain Tensioner", "DSG Mechatronics", "Water Pump"], "critical_milestones": [{"mileage": 90000, "description": "Timing Chain Tensioner Update", "est_cost": 1800}]},
        "faults": {"model_id_ref": "volkswagen_jetta_mk6", "faults": [{"component": "Timing Chain Tensioner Failure", "symptoms": "Rattle on start, engine won't turn over (Valve jump)", "repairCost": 5500, "verdictImplication": "Severe: Can result in complete engine destruction if it skips a tooth."}]}
    }
]

def load_json(path):
    with open(path, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_json(path, data):
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=4)

def main():
    print("Loading existing data...")
    data_models = load_json(PATHS['models'])
    data_market = load_json(PATHS['market'])
    data_reliability = load_json(PATHS['reliability'])
    data_faults = load_json(PATHS['faults'])

    existing_ids = {m['id'] for m in data_models}

    print(f"Adding {len(BATCH_2_MODELS)} new high-quality models (Batch 2)...")
    
    for new_entry in BATCH_2_MODELS:
        model_id = new_entry['model_def']['id']
        if model_id in existing_ids: continue
        data_models.append(new_entry['model_def'])
        data_market.append(new_entry['market'])
        data_reliability.append(new_entry['reliability'])
        data_faults.append(new_entry['faults'])
        print(f" - Added: {new_entry['model_def']['brand']} {new_entry['model_def']['model']}")

    save_json(PATHS['models'], data_models)
    save_json(PATHS['market'], data_market)
    save_json(PATHS['reliability'], data_reliability)
    save_json(PATHS['faults'], data_faults)
    print("Batch 2 injection complete.")

if __name__ == "__main__":
    main()
