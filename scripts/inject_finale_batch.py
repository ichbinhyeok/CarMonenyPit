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

FINALE_MODELS = [
    # --- Entry-Luxury SUVs ---
    {
        "model_def": {"id": "bmw_x3_g01", "brand": "BMW", "model": "X3", "generation": "G01", "start_year": 2018, "end_year": 2024},
        "market": {"model_id": "bmw_x3_g01", "jan_2026_avg_price": 31000, "depreciation_rate": 0.14, "avg_annual_repair_cost": 1200, "depreciation_outlook": "Moderate"},
        "reliability": {"model_id": "bmw_x3_g01", "score": 58, "lifespan_miles": 180000, "best_years": [2021, 2022], "worst_years": [2018, 2019], "common_trouble_spots": ["Coolant Leak", "Oil Filter Housing", "Sunroof Drainage"], "critical_milestones": [{"mileage": 80000, "description": "Oil Filter Housing replacement", "est_cost": 1400}]},
        "faults": {"model_id_ref": "bmw_x3_g01", "faults": [{"component": "Plastic Oil Filter Housing Crack", "symptoms": "Oil leak onto hot engine, burning smell, coolant contamination", "repairCost": 1500, "verdictImplication": "Moderate: A known weak point in the B48/B58 engines. Requires replacement with updated unit."}]}
    },
    {
        "model_def": {"id": "audi_q5_fy", "brand": "AUDI", "model": "Q5", "generation": "FY", "start_year": 2018, "end_year": 2024},
        "market": {"model_id": "audi_q5_fy", "jan_2026_avg_price": 28500, "depreciation_rate": 0.15, "avg_annual_repair_cost": 1100, "depreciation_outlook": "Moderate"},
        "reliability": {"model_id": "audi_q5_fy", "score": 55, "lifespan_miles": 170000, "best_years": [2020], "worst_years": [2018], "common_trouble_spots": ["Water Pump", "Gateway Control Module", "Coolant Shutoff Valve"], "critical_milestones": [{"mileage": 70000, "description": "Water Pump/Thermostat Housing", "est_cost": 1300}]},
        "faults": {"model_id_ref": "audi_q5_fy", "faults": [{"component": "Water Pump & Thermostat Failure", "symptoms": "Overheating warning, puddles under front of car", "repairCost": 1450, "verdictImplication": "Moderate: Extremely common on VAG 2.0T engines. Plastic housing warps."}]}
    },
    {
        "model_def": {"id": "mercedes_glc_x253", "brand": "MERCEDES-BENZ", "model": "GLC", "generation": "X253", "start_year": 2016, "end_year": 2022},
        "market": {"model_id": "mercedes_glc_x253", "jan_2026_avg_price": 25000, "depreciation_rate": 0.16, "avg_annual_repair_cost": 1300, "depreciation_outlook": "High"},
        "reliability": {"model_id": "mercedes_glc_x253", "score": 52, "lifespan_miles": 160000, "best_years": [2021, 2022], "worst_years": [2016, 2017], "common_trouble_spots": ["Suspension Bushings", "Cracking Seats", "Brake Squeal"], "critical_milestones": [{"mileage": 60000, "description": "B1 Maintenance Service", "est_cost": 1100}]},
        "faults": {"model_id_ref": "mercedes_glc_x253", "faults": [{"component": "Control Arm Bushing Wear", "symptoms": "Clunking over bumps, uneven tire wear", "repairCost": 1800, "verdictImplication": "Moderate: Mercedes multi-link suspension is sensitive to bushing wear."}]}
    },
    {
        "model_def": {"id": "volvo_xc90_spa", "brand": "VOLVO", "model": "XC90", "generation": "SPA", "start_year": 2016, "end_year": 2023},
        "market": {"model_id": "volvo_xc90_spa", "jan_2026_avg_price": 32000, "depreciation_rate": 0.18, "avg_annual_repair_cost": 1500, "depreciation_outlook": "High"},
        "reliability": {"model_id": "volvo_xc90_spa", "score": 45, "lifespan_miles": 150000, "best_years": [2022], "worst_years": [2016, 2017], "common_trouble_spots": ["Piston Rings (2016)", "Air Suspension", "Sensus Infotainment"], "critical_milestones": [{"mileage": 100000, "description": "Supercharger/Turbocharger Check", "est_cost": 3500}]},
        "faults": {"model_id_ref": "volvo_xc90_spa", "faults": [{"component": "High Oil Consumption (Piston Rings)", "symptoms": "Low oil light every 1000 miles, blue smoke", "repairCost": 6000, "verdictImplication": "Severe: Critical failure on early Drive-E engines. Requires engine rebuild or replacement."}]}
    },
    # --- Mid-Size Sedans ---
    {
        "model_def": {"id": "hyundai_sonata_lf", "brand": "HYUNDAI", "model": "Sonata", "generation": "LF", "start_year": 2015, "end_year": 2019},
        "market": {"model_id": "hyundai_sonata_lf", "jan_2026_avg_price": 12500, "depreciation_rate": 0.15, "avg_annual_repair_cost": 650, "depreciation_outlook": "High"},
        "reliability": {"model_id": "hyundai_sonata_lf", "score": 42, "lifespan_miles": 160000, "best_years": [2018, 2019], "worst_years": [2015, 2016], "common_trouble_spots": ["Theta II Engine", "Blind Spot Sensor", "Steering Coupler"], "critical_milestones": [{"mileage": 90000, "description": "Rod Bearing Test", "est_cost": 0}]},
        "faults": {"model_id_ref": "hyundai_sonata_lf", "faults": [{"component": "Engine Bearings (Theta II)", "symptoms": "Knocking noise, oil light flicker, engine stall", "repairCost": 5500, "verdictImplication": "Severe: High risk of catastrophic engine failure. Check recall status immediately."}]}
    },
    {
        "model_def": {"id": "kia_optima_jf", "brand": "KIA", "model": "Optima", "generation": "JF", "start_year": 2016, "end_year": 2020},
        "market": {"model_id": "kia_optima_jf", "jan_2026_avg_price": 13000, "depreciation_rate": 0.15, "avg_annual_repair_cost": 650, "depreciation_outlook": "High"},
        "reliability": {"model_id": "kia_optima_jf", "score": 42, "lifespan_miles": 160000, "best_years": [2019, 2020], "worst_years": [2016, 2017], "common_trouble_spots": ["Engine Fire Risk", "Turbo Wastegate", "Transmission Shifting"], "critical_milestones": [{"mileage": 80000, "description": "Engine Knock Sensor Update", "est_cost": 0}]},
        "faults": {"model_id_ref": "kia_optima_jf", "faults": [{"component": "Engine Seizure (Rod Bearings)", "symptoms": "Metallic rattling, sudden loss of power", "repairCost": 5500, "verdictImplication": "Severe: Major reliability hazard. Repair cost often exceeds 50% of value on high-mileage units."}]}
    },
    {
        "model_def": {"id": "ford_fusion_2nd", "brand": "FORD", "model": "Fusion", "generation": "2nd Gen", "start_year": 2013, "end_year": 2020},
        "market": {"model_id": "ford_fusion_2nd", "jan_2026_avg_price": 11000, "depreciation_rate": 0.16, "avg_annual_repair_cost": 750, "depreciation_outlook": "High"},
        "reliability": {"model_id": "ford_fusion_2nd", "score": 48, "lifespan_miles": 170000, "best_years": [2019, 2020], "worst_years": [2013, 2014, 2016], "common_trouble_spots": ["Coolant Intrusion", "Transmission Judder", "Shifter Cable"], "critical_milestones": [{"mileage": 100000, "description": "Coolant Level Monitoring", "est_cost": 150}]},
        "faults": {"model_id_ref": "ford_fusion_2nd", "faults": [{"component": "1.5L/2.0L Coolant Intrusion", "symptoms": "White smoke, misfires on startup, chronic low coolant", "repairCost": 6000, "verdictImplication": "CATASTROPHIC: Coolant leaks into cylinders due to block design. Engine replacement required."}]}
    },
    # --- Trucks & Large SUVs ---
    {
        "model_def": {"id": "toyota_tundra_2nd", "brand": "TOYOTA", "model": "Tundra", "generation": "2nd Gen", "start_year": 2007, "end_year": 2021},
        "market": {"model_id": "toyota_tundra_2nd", "jan_2026_avg_price": 27000, "depreciation_rate": 0.05, "avg_annual_repair_cost": 600, "depreciation_outlook": "Low"},
        "reliability": {"model_id": "toyota_tundra_2nd", "score": 88, "lifespan_miles": 400000, "best_years": [2018, 2019, 2020], "worst_years": [2007, 2012], "common_trouble_spots": ["Cam Tower Leak", "Frame Rust", "Air Induction Pump"], "critical_milestones": [{"mileage": 150000, "description": "Secondary Air Injection Pump", "est_cost": 1800}]},
        "faults": {"model_id_ref": "toyota_tundra_2nd", "faults": [{"component": "Cam Tower Oil Leak", "symptoms": "Oil seeping onto exhaust manifold, burning smell", "repairCost": 3500, "verdictImplication": "Severe: High labor cost for a gasket repair. Often deferred by owners."}]}
    },
    {
        "model_def": {"id": "nissan_frontier_d40", "brand": "NISSAN", "model": "Frontier", "generation": "D40", "start_year": 2005, "end_year": 2021},
        "market": {"model_id": "nissan_frontier_d40", "jan_2026_avg_price": 14000, "depreciation_rate": 0.08, "avg_annual_repair_cost": 550, "depreciation_outlook": "Low"},
        "reliability": {"model_id": "nissan_frontier_d40", "score": 70, "lifespan_miles": 300000, "best_years": [2016, 2017, 2018], "worst_years": [2005, 2006, 2007], "common_trouble_spots": ["Strawberry Milkshake (Pre-2010)", "Timing Chain Guides", "Crank Sensor"], "critical_milestones": [{"mileage": 120000, "description": "Radiator Replacement (Preventive)", "est_cost": 650}]},
        "faults": {"model_id_ref": "nissan_frontier_d40", "faults": [{"component": "SMOD (Strawberry Milkshake of Death)", "symptoms": "Transmission slipping, pink frothy fluid", "repairCost": 5500, "verdictImplication": "Critical: Radiator failure mixes coolant into transmission. Destroys the gearbox. Pre-2010 models only."}]}
    },
    {
        "model_def": {"id": "chevrolet_traverse_2nd", "brand": "CHEVROLET", "model": "Traverse", "generation": "2nd Gen", "start_year": 2018, "end_year": 2023},
        "market": {"model_id": "chevrolet_traverse_2nd", "jan_2026_avg_price": 22000, "depreciation_rate": 0.14, "avg_annual_repair_cost": 850, "depreciation_outlook": "Moderate"},
        "reliability": {"model_id": "chevrolet_traverse_2nd", "score": 55, "lifespan_miles": 180000, "best_years": [2022], "worst_years": [2018], "common_trouble_spots": ["Shift to Park", "Transmission Control Module", "Evap Purge Valve"], "critical_milestones": [{"mileage": 100000, "description": "Transmission Fluid Exchange", "est_cost": 350}]},
        "faults": {"model_id_ref": "chevrolet_traverse_2nd", "faults": [{"component": "Transmission Control Module Failure", "symptoms": "Erratic shifting, won't move from Park, limp mode", "repairCost": 1200, "verdictImplication": "Moderate: Electrical/Control issue common on Hydra-Matic 9T65."}]}
    },
    # --- Niche & High-Intent ---
    {
        "model_def": {"id": "dodge_challenger_la", "brand": "DODGE", "model": "Challenger", "generation": "LA", "start_year": 2015, "end_year": 2023},
        "market": {"model_id": "dodge_challenger_la", "jan_2026_avg_price": 24000, "depreciation_rate": 0.10, "avg_annual_repair_cost": 800, "depreciation_outlook": "Low (Last of Hemingway)"},
        "reliability": {"model_id": "dodge_challenger_la", "score": 60, "lifespan_miles": 200000, "best_years": [2021, 2022], "worst_years": [2015], "common_trouble_spots": ["Water Pump", "HEMI Tick", "UConnect Delamination"], "critical_milestones": [{"mileage": 100000, "description": "Spark Plug Service (16 Plugs for V8)", "est_cost": 650}]},
        "faults": {"model_id_ref": "dodge_challenger_la", "faults": [{"component": "HEMI Tick (Lifter Failure)", "symptoms": "Rhythmic ticking noise at idle, misfires", "repairCost": 4500, "verdictImplication": "Severe: Lifter roller fails and eats the camshaft. Requires major engine teardown."}]}
    },
    {
        "model_def": {"id": "jeep_compass_mp", "brand": "JEEP", "model": "Compass", "generation": "MP", "start_year": 2017, "end_year": 2024},
        "market": {"model_id": "jeep_compass_mp", "jan_2026_avg_price": 16500, "depreciation_rate": 0.18, "avg_annual_repair_cost": 750, "depreciation_outlook": "High"},
        "reliability": {"model_id": "jeep_compass_mp", "score": 38, "lifespan_miles": 130000, "best_years": [2022], "worst_years": [2017, 2018], "common_trouble_spots": ["Oil Consumption", "Electronic Park Brake", "Battery Drain"], "critical_milestones": [{"mileage": 60000, "description": "Main & Auxiliary Battery Replace", "est_cost": 600}]},
        "faults": {"model_id_ref": "jeep_compass_mp", "faults": [{"component": "Excessive Oil Consumption", "symptoms": "Engine stalling on turns due to low oil, no warning light", "repairCost": 5000, "verdictImplication": "Severe: TigerShark 2.4L engine flaw. May require engine replacement if out of warranty."}]}
    },
    {
        "model_def": {"id": "lexus_es_xz10", "brand": "LEXUS", "model": "ES", "generation": "XZ10", "start_year": 2019, "end_year": 2024},
        "market": {"model_id": "lexus_es_xz10", "jan_2026_avg_price": 34000, "depreciation_rate": 0.08, "avg_annual_repair_cost": 450, "depreciation_outlook": "Low"},
        "reliability": {"model_id": "lexus_es_xz10", "score": 92, "lifespan_miles": 350000, "best_years": [2021, 2022], "worst_years": [], "common_trouble_spots": ["Minor Trim Rattles"], "critical_milestones": [{"mileage": 100000, "description": "Brake Fluid & Coolant Exchange", "est_cost": 450}]},
        "faults": {"model_id_ref": "lexus_es_xz10", "faults": [{"component": "No Major Systematic Faults", "symptoms": "N/A", "repairCost": 500, "verdictImplication": "Very Low Risk: Highly reliable platform. Repairs are typically only wear items."}]}
    }
]

def load_json(path):
    with open(path, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_json(path, data):
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=4)

def main():
    print("Loading existing data for verification...")
    data_models = load_json(PATHS['models'])
    data_market = load_json(PATHS['market'])
    data_reliability = load_json(PATHS['reliability'])
    data_faults = load_json(PATHS['faults'])

    existing_ids = {m['id'] for m in data_models}

    print(f"Adding {len(FINALE_MODELS)} final high-precision models...")
    added_count = 0
    
    for new_entry in FINALE_MODELS:
        model_id = new_entry['model_def']['id']
        if model_id in existing_ids:
            print(f" ! Skipping {model_id} (Already exists)")
            continue
            
        data_models.append(new_entry['model_def'])
        data_market.append(new_entry['market'])
        data_reliability.append(new_entry['reliability'])
        data_faults.append(new_entry['faults'])
        added_count += 1
        print(f" + Added: {new_entry['model_def']['brand']} {new_entry['model_def']['model']}")

    print(f"Total entries now: {len(data_models)}")
    
    # Save all
    if added_count > 0:
        save_json(PATHS['models'], data_models)
        save_json(PATHS['market'], data_market)
        save_json(PATHS['reliability'], data_reliability)
        save_json(PATHS['faults'], data_faults)
        print(f"Successfully injected {added_count} new models into all 4 JSON files.")
    else:
        print("No new models added.")

if __name__ == "__main__":
    main()
