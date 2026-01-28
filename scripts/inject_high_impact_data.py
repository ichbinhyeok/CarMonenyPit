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

# --- 1. NEW DATA DEFINITIONS (HIGH QUALITY / PRECISION) ---
# Format: ID, Dictionary of Data Objects

NEW_MODELS = [
    # --- HEAVY DUTY TRUCKS ---
    {
        "model_def": {
            "id": "ford_f250_alumiduty",
            "brand": "FORD",
            "model": "F-250 Super Duty",
            "generation": "Alumiduty",
            "start_year": 2017,
            "end_year": 2022
        },
        "market": {
            "model_id": "ford_f250_alumiduty",
            "jan_2026_avg_price": 42000,
            "depreciation_rate": 0.05, # Low depreciation
            "avg_annual_repair_cost": 1200,
            "depreciation_outlook": "Very Low (Workhorse)"
        },
        "reliability": {
            "model_id": "ford_f250_alumiduty",
            "score": 45, # Low due to catastrophic repair risks
            "lifespan_miles": 350000, # If you fix it, it lasts
            "best_years": [2021, 2022],
            "worst_years": [2017, 2018],
            "common_trouble_spots": ["CP4 Fuel Pump", "Death Wobble", "Body Mounts"],
            "critical_milestones": [
                {"mileage": 100000, "description": "CP4 Disaster Prevention Kit", "est_cost": 450},
                {"mileage": 120000, "description": "Front End Rebuild (Steering)", "est_cost": 2500}
            ]
        },
        "faults": {
            "model_id_ref": "ford_f250_alumiduty",
            "faults": [
                {
                    "component": "CP4 Fuel Pump Failure",
                    "symptoms": "Truck dies instantly, 'Low Fuel Pressure', no restart",
                    "repairCost": 9500,
                    "verdictImplication": "CATASTROPHIC: Pump sends metal shavings into all injectors and lines. Total fuel system replacement required."
                },
                {
                    "component": "Death Wobble",
                    "symptoms": "Violent shaking of steering wheel after irregular road surface",
                    "repairCost": 1800,
                    "verdictImplication": "Severe: Track bar and damper failure. Dangerous at highway speeds."
                }
            ]
        }
    },
    {
        "model_def": {
            "id": "ram_2500_4th_gen",
            "brand": "RAM",
            "model": "2500",
            "generation": "4th Gen (Cummins)",
            "start_year": 2013,
            "end_year": 2018
        },
        "market": {
            "model_id": "ram_2500_4th_gen",
            "jan_2026_avg_price": 38000,
            "depreciation_rate": 0.06,
            "avg_annual_repair_cost": 1100,
            "depreciation_outlook": "Low"
        },
        "reliability": {
            "model_id": "ram_2500_4th_gen",
            "score": 50,
            "lifespan_miles": 400000,
            "best_years": [2017, 2018],
            "worst_years": [2013, 2014],
            "common_trouble_spots": ["68RFE Transmission", "Heater Grid Bolt", "Actuator Failure"],
            "critical_milestones": [
                {"mileage": 150000, "description": "Transmission Rebuild", "est_cost": 5500},
                {"mileage": 100000, "description": "Grid Heater Nut Delete", "est_cost": 800}
            ]
        },
        "faults": {
            "model_id_ref": "ram_2500_4th_gen",
            "faults": [
                {
                    "component": "68RFE Transmission Failure",
                    "symptoms": "Slipping gears, burnt fluid smell, limp mode",
                    "repairCost": 6500,
                    "verdictImplication": "Critical: The stock transmission cannot handle the Cummins torque. Expect failure near 150k miles."
                },
                {
                    "component": "Heater Grid Bolt",
                    "symptoms": "Bolt falls into intake manifold",
                    "repairCost": 12000,
                    "verdictImplication": "Engine Killer: A $2 bolt can destroy a $12k engine. Inspect immediately."
                }
            ]
        }
    },
    # --- LUXURY MONEY PITS ---
    {
        "model_def": {
            "id": "land_rover_range_rover_sport_l494",
            "brand": "LAND ROVER",
            "model": "Range Rover Sport",
            "generation": "L494",
            "start_year": 2014,
            "end_year": 2022
        },
        "market": {
            "model_id": "land_rover_range_rover_sport_l494",
            "jan_2026_avg_price": 28000,
            "depreciation_rate": 0.20, # Massive depreciation
            "avg_annual_repair_cost": 2500,
            "depreciation_outlook": "Extreme (Falling Knife)"
        },
        "reliability": {
            "model_id": "land_rover_range_rover_sport_l494",
            "score": 25, # Very poor
            "lifespan_miles": 140000,
            "best_years": [],
            "worst_years": [2014, 2015, 2016],
            "common_trouble_spots": ["Timing Chain", "Air Suspension", "Coolant Pipes"],
            "critical_milestones": [
                {"mileage": 80000, "description": "Timing Chain Service", "est_cost": 7500},
                {"mileage": 60000, "description": "Crossover Coolant Pipe", "est_cost": 1200}
            ]
        },
        "faults": {
            "model_id_ref": "land_rover_range_rover_sport_l494",
            "faults": [
                {
                    "component": "Timing Chain Failure",
                    "symptoms": "Rattling noise from engine front (5.0L SC)",
                    "repairCost": 8500,
                    "verdictImplication": "Financial Ruin: Requires engine removal. Often exceeds vehicle value. SELL IMMEDIATELY."
                },
                {
                    "component": "Air Suspension Compressor",
                    "symptoms": "Vehicle sagging, 'Suspension Fault' error",
                    "repairCost": 1800,
                    "verdictImplication": "Common: Compressor wears out due to leak overwork."
                }
            ]
        }
    },
    {
        "model_def": {
            "id": "bmw_x5_f15",
            "brand": "BMW",
            "model": "X5",
            "generation": "F15",
            "start_year": 2014,
            "end_year": 2018
        },
        "market": {
            "model_id": "bmw_x5_f15",
            "jan_2026_avg_price": 21000,
            "depreciation_rate": 0.15,
            "avg_annual_repair_cost": 1800,
            "depreciation_outlook": "High"
        },
        "reliability": {
            "model_id": "bmw_x5_f15",
            "score": 40,
            "lifespan_miles": 180000,
            "best_years": [2017, 2018],
            "worst_years": [2014],
            "common_trouble_spots": ["Valve Stem Seals (V8)", "Water Pump", "Oil Leaks"],
            "critical_milestones": [
                {"mileage": 90000, "description": "Valve Stem Seals (V8)", "est_cost": 5000},
                {"mileage": 70000, "description": "Electric Water Pump", "est_cost": 1200}
            ]
        },
        "faults": {
            "model_id_ref": "bmw_x5_f15",
            "faults": [
                {
                    "component": "Valve Stem Seals",
                    "symptoms": "Blue smoke from exhaust after idling (N63 V8)",
                    "repairCost": 5500,
                    "verdictImplication": "High Risk: Engine burns extensive oil. Repair labor is intensive."
                },
                {
                    "component": "Timing Chain Guides",
                    "symptoms": "High pitched whine (N20/N26 4-cyl models)",
                    "repairCost": 3500,
                    "verdictImplication": "Critical: Plastic guides shatter and clog oil pickup."
                }
            ]
        }
    },
     {
        "model_def": {
            "id": "audi_q7_4m",
            "brand": "AUDI",
            "model": "Q7",
            "generation": "4M",
            "start_year": 2017,
            "end_year": 2023
        },
        "market": {
            "model_id": "audi_q7_4m",
            "jan_2026_avg_price": 26500,
            "depreciation_rate": 0.16,
            "avg_annual_repair_cost": 1600,
            "depreciation_outlook": "High"
        },
        "reliability": {
            "model_id": "audi_q7_4m",
            "score": 50,
            "lifespan_miles": 180000,
            "best_years": [2020, 2021],
            "worst_years": [2017],
            "common_trouble_spots": ["Oil Consumption", "Water Pump", "Motor Mounts"],
            "critical_milestones": [
                {"mileage": 80000, "description": "Motor Mounts", "est_cost": 1400},
                {"mileage": 100000, "description": "Oil Separator (PCV)", "est_cost": 1200}
            ]
        },
        "faults": {
            "model_id_ref": "audi_q7_4m",
            "faults": [
                {
                    "component": "Supercharger Clutch",
                    "symptoms": "Chirping noise during engagement (3.0T)",
                    "repairCost": 1500,
                    "verdictImplication": "Moderate: Wear item on high mileage units."
                },
                {
                    "component": "Oil Consumption",
                    "symptoms": "Low oil light every 500 miles",
                    "repairCost": 4500,
                    "verdictImplication": "Severe: Piston ring issues on early 3.0T/2.0T engines. Requires rebuild."
                }
            ]
        }
    },
    # --- FAMILY HAULERS ---
    {
        "model_def": {
            "id": "honda_odyssey_rl6",
            "brand": "HONDA",
            "model": "Odyssey",
            "generation": "RL6",
            "start_year": 2018,
            "end_year": 2024
        },
        "market": {
            "model_id": "honda_odyssey_rl6",
            "jan_2026_avg_price": 24000,
            "depreciation_rate": 0.09,
            "avg_annual_repair_cost": 600,
            "depreciation_outlook": "Low"
        },
        "reliability": {
            "model_id": "honda_odyssey_rl6",
            "score": 75,
            "lifespan_miles": 250000,
            "best_years": [2021, 2022],
            "worst_years": [2018, 2019],
            "common_trouble_spots": ["Transmission Judder", "Sliding Doors", "Infotainment"],
            "critical_milestones": [
                {"mileage": 100000, "description": "Timing Belt & Water Pump", "est_cost": 1300},
                {"mileage": 60000, "description": "Transmission Fluid", "est_cost": 300}
            ]
        },
        "faults": {
            "model_id_ref": "honda_odyssey_rl6",
            "faults": [
                {
                    "component": "Sliding Door Failure",
                    "symptoms": "Door stuck, motor grinding, battery drain",
                    "repairCost": 1500,
                    "verdictImplication": "Common: Cable or motor failure. Expensive convenience repair."
                },
                {
                    "component": "9-Speed/10-Speed Judder",
                    "symptoms": "Rough shifting, hunting for gears",
                    "repairCost": 3500,
                    "verdictImplication": "Moderate: Software updates help, but torque converter may fail."
                }
            ]
        }
    },
     {
        "model_def": {
            "id": "chrysler_pacifica_ru",
            "brand": "CHRYSLER",
            "model": "Pacifica",
            "generation": "RU",
            "start_year": 2017,
            "end_year": 2023
        },
        "market": {
            "model_id": "chrysler_pacifica_ru",
            "jan_2026_avg_price": 18500,
            "depreciation_rate": 0.14,
            "avg_annual_repair_cost": 900,
            "depreciation_outlook": "Moderate"
        },
        "reliability": {
            "model_id": "chrysler_pacifica_ru",
            "score": 45,
            "lifespan_miles": 180000,
            "best_years": [2021],
            "worst_years": [2017, 2018],
            "common_trouble_spots": ["Head Gasket (Hybrid)", "Stalling", "Rust"],
            "critical_milestones": [
                {"mileage": 80000, "description": "Head Gasket Check", "est_cost": 3500},
                {"mileage": 50000, "description": "Electrical System", "est_cost": 800}
            ]
        },
        "faults": {
            "model_id_ref": "chrysler_pacifica_ru",
            "faults": [
                {
                    "component": "Head Gasket Failure",
                    "symptoms": "Misfire, coolant consumption (Hybrid Models)",
                    "repairCost": 4000,
                    "verdictImplication": "Critical: Widespread issue on Hybrid Pacificas. Engine replacement likely."
                },
                {
                    "component": "Stalling while Driving",
                    "symptoms": "Engine cuts out at speed (PHEV)",
                    "repairCost": 2200,
                    "verdictImplication": "Dangerous: Transmission wiring harness recall. Verify VIN."
                }
            ]
        }
    }
]

# --- 2. INJECTION LOGIC ---

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

    # Track IDs to avoid duplicates
    existing_ids = {m['id'] for m in data_models}

    print(f"Adding {len(NEW_MODELS)} new high-quality models...")
    
    for new_entry in NEW_MODELS:
        model_id = new_entry['model_def']['id']
        
        if model_id in existing_ids:
            print(f"Skipping {model_id} (Already exists)")
            continue

        # Append to all lists
        data_models.append(new_entry['model_def'])
        data_market.append(new_entry['market'])
        data_reliability.append(new_entry['reliability'])
        data_faults.append(new_entry['faults'])
        
        print(f" - Added: {new_entry['model_def']['brand']} {new_entry['model_def']['model']}")

    # Save all
    print("Saving files...")
    save_json(PATHS['models'], data_models)
    save_json(PATHS['market'], data_market)
    save_json(PATHS['reliability'], data_reliability)
    save_json(PATHS['faults'], data_faults)
    
    print("Success! Data injection complete.")

if __name__ == "__main__":
    main()
