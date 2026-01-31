#!/usr/bin/env python3
"""
Script to add mileage_logic_text to models that are missing it.
"""
import json
from pathlib import Path

# Path to the reliability JSON file
RELIABILITY_PATH = Path(__file__).parent.parent / "src/main/resources/data/model_reliability.json"

# Mileage logic text templates for different vehicle types
MILEAGE_LOGIC_TEMPLATES = {
    # Trucks / Heavy Duty
    "ford_f250_alumiduty": {
        "100000": "CP4 fuel pump is a ticking time bomb on 6.7L diesel. Install disaster prevention kit.",
        "150000": "Front end steering components wear fast. Budget for ball joints and tie rods.",
        "200000": "If the 6.7L Powerstroke is still running strong, you have a 400k mile truck."
    },
    "ram_2500_4th_gen": {
        "100000": "68RFE transmission is the weak link. Consider aftermarket upgrades if towing heavy.",
        "150000": "Grid heater bolt is a known failure. Delete before it drops into the engine.",
        "250000": "The Cummins 6.7L is just getting started. These easily hit 400k+ miles."
    },
    
    # Honda Models
    "honda_pilot_yf3": {
        "80000": "VCM (Variable Cylinder Management) causes oil consumption. Consider VCM Muzzler install.",
        "105000": "Timing belt is due. This is interference engine - failure destroys valves.",
        "150000": "Transmission judder may appear. Fluid changes with Honda DW-1 often resolve."
    },
    "honda_odyssey_rl6": {
        "60000": "Transmission judder is common early. Fluid change may help temporarily.",
        "100000": "Timing belt due. Include water pump replacement - labor is the same.",
        "150000": "Sliding door cables and motors wear. Expect replacement costs around $800."
    },
    
    # Hyundai/Kia Models
    "hyundai_elantra_ad": {
        "60000": "Dual-clutch transmission hesitation is normal characteristic, not a defect.",
        "100000": "Nu 2.0 engine is safe. Gamma/Theta engines are the problem motors.",
        "150000": "If engine hasn't failed by now, you're likely past the danger zone."
    },
    "hyundai_santa_fe_dm": {
        "60000": "Check VIN for Theta II engine recall. Engine seizure risk exists.",
        "100000": "Transmission shudder may develop. Fluid change helps.",
        "150000": "Solid platform if engine survived. Keep up with maintenance."
    },
    "kia_sorento_um": {
        "60000": "Same Theta II engine risk. Verify recall status and engine replacement history.",
        "100000": "8-speed transmission is holding up well. No major concerns.",
        "150000": "These are proving reliable. Korean quality has improved dramatically."
    },
    "hyundai_sonata_lf": {
        "60000": "Theta II engine fire risk. Check if recall was performed.",
        "90000": "Rod bearing test should be done. Listen for knocking on cold start.",
        "150000": "If engine survived, the rest is solid. Keep driving."
    },
    "kia_optima_jf": {
        "60000": "Same Theta II concerns as Sonata. Verify recall status.",
        "80000": "Check for engine knock sensor software update.",
        "150000": "Turbo models need more care. NA engines are proving reliable."
    },
    
    # German Luxury
    "bmw_3series_f30": {
        "60000": "N20/N26 timing chain tensioner is critical. Listen for startup rattle.",
        "100000": "Oil leaks from valve cover and oil filter housing are expected.",
        "150000": "Expensive repairs become frequent. Budget accordingly or exit."
    },
    "bmw_x5_f15": {
        "70000": "Electric water pump failure is common. Replace proactively.",
        "90000": "V8 models develop valve stem seal issues. Blue smoke on startup is the sign.",
        "150000": "Repair costs escalate rapidly. Extended warranty is essential."
    },
    "bmw_x3_g01": {
        "60000": "Oil filter housing gasket is a common leak point. Monitor oil level.",
        "80000": "B58 engine is more reliable than previous N-series. Good choice.",
        "150000": "Cooling system components age. Budget for hoses and thermostat."
    },
    "mercedes_c_class_w205": {
        "60000": "M274 engine balance shaft failure is possible. Listen for rough idle.",
        "100000": "9G-Tronic transmission may develop issues. Fluid change helps.",
        "150000": "Electrical complexity means expensive repairs. Consider selling."
    },
    "mercedes_glc_x253": {
        "60000": "B1 service is due. Includes many important items.",
        "100000": "Suspension bushings wear. Clunking over bumps is the sign.",
        "150000": "Air suspension (if equipped) may need attention."
    },
    "audi_a4_b9": {
        "60000": "DSG transmission service is mandatory. Don't skip.",
        "100000": "Water pump failure is common. Look for coolant leaks.",
        "150000": "2.0T engine is solid if maintained. Keep oil fresh."
    },
    "audi_q5_fy": {
        "70000": "Water pump/thermostat housing is weak point. Watch coolant levels.",
        "100000": "Gateway module issues may cause electrical quirks.",
        "150000": "Overall solid platform. Maintenance is key."
    },
    "audi_q7_4m": {
        "80000": "Motor mounts wear. Vibration at idle is the symptom.",
        "100000": "PCV valve (oil separator) needs replacement.",
        "150000": "Air suspension struts may need replacement. Budget $2k+."
    },
    
    # Nissan CVT Vehicles
    "nissan_sentra_b17": {
        "60000": "CVT fluid service is MANDATORY. This is survival maintenance.",
        "100000": "CVT danger zone begins. Any whining means imminent failure.",
        "120000": "CVT replacement ($4000) exceeds vehicle value. Sell before failure."
    },
    "nissan_pathfinder_r52": {
        "60000": "CVT fluid exchange required. Use only Nissan NS-3.",
        "100000": "CVT is the limiting factor. Budget for replacement or exit.",
        "140000": "These rarely make it past here without CVT work."
    },
    
    # American Sedans
    "chevrolet_malibu_9th": {
        "60000": "1.5T may have occasional stalling. Software updates available.",
        "100000": "9-speed transmission shudder is common. Fluid helps.",
        "150000": "Solid platform overall. V6 models more reliable than turbo-4."
    },
    "ford_edge_cd4": {
        "60000": "PTU (Power Transfer Unit) fluid change is critical on AWD models.",
        "100000": "Water pump may leak. Internal design means quick action needed.",
        "150000": "Transmission shudder may develop. Plan for eventual rebuild."
    },
    "ford_fusion_2nd": {
        "60000": "1.5L/2.0L EcoBoost has coolant intrusion risk. Watch for white smoke.",
        "100000": "Transmission judder is common. Fluid change may help temporarily.",
        "150000": "Shifter cable bushing fails. $10 part, annoying symptom."
    },
    
    # Jeep/Dodge
    "jeep_cherokee_kl": {
        "60000": "9-speed ZF transmission is problematic. Software updates help.",
        "100000": "Electrical gremlins common. Check all modules.",
        "150000": "2.4L Tigershark engine may consume oil. V6 is more reliable."
    },
    "dodge_charger_ld": {
        "80000": "Hemi tick (exhaust manifold bolts) may appear. Not immediately critical.",
        "100000": "ZF 8-speed is excellent. Just change fluid.",
        "150000": "Suspension refresh needed. Control arms and bushings."
    },
    "dodge_durango_wd": {
        "80000": "TIPM electrical issues may appear. Random symptoms indicate failure.",
        "100000": "A/C condenser failures common. Budget for replacement.",
        "150000": "Hemi engine is reliable. Transmission and electronics are concerns."
    },
    "dodge_challenger_la": {
        "80000": "Water pump on V8 models needs attention. Look for weeping.",
        "100000": "Hemi tick is common. Not immediately dangerous.",
        "150000": "Manual transmission models are bulletproof. Autos need fluid service."
    },
    "jeep_compass_mp": {
        "60000": "Battery drain issues common. Auxiliary battery may need replacement.",
        "100000": "Oil consumption on 2.4L is possible. Monitor levels.",
        "120000": "This is near expected lifespan. Major repairs questionable."
    },
    
    # Subaru
    "subaru_crosstrek_xv": {
        "60000": "CVT fluid change is mandatory. Subaru CVT depends on fresh fluid.",
        "100000": "Oil consumption on FB20 engine varies. Check levels often.",
        "150000": "Head gasket issues of older Subarus mostly resolved in this gen."
    },
    
    # Tesla
    "tesla_model_3": {
        "60000": "Door handle motors may fail. Common issue.",
        "100000": "Battery degradation is minimal. Expect 90%+ capacity remaining.",
        "200000": "EVs thrive at high mileage. Just brake fluid and coolant."
    },
    "tesla_model_y": {
        "60000": "Heat pump issues in cold weather (early models). Software updates help.",
        "100000": "Suspension components may creak. Normal wear.",
        "200000": "EV simplicity means long life. Battery remains strong."
    },
    
    # Luxury
    "lexus_rx_al20": {
        "100000": "Nearly bulletproof. Just do scheduled maintenance.",
        "150000": "Brake actuator (hybrid models) may need service.",
        "200000": "The gold standard of reliability. Keep driving."
    },
    "lexus_es_xz10": {
        "100000": "Minor trim rattles are the worst complaint. Exceptional reliability.",
        "200000": "Toyota TNGA platform is proven. Expect long life.",
        "300000": "These regularly exceed 300k miles with basic maintenance."
    },
    "cadillac_escalade_k2": {
        "100000": "AFM lifter failure risk. Same as Silverado/Sierra. Listen for ticking.",
        "120000": "Air suspension may need service. Budget for struts.",
        "180000": "Transmission shudder may develop. Fluid change helps."
    },
    "lincoln_navigator_u554": {
        "80000": "Turbo failure risk on 3.5L EcoBoost increases.",
        "100000": "Air suspension may need attention.",
        "150000": "These are expensive to repair. Extended warranty recommended."
    },
    "acura_mdx_yd3": {
        "80000": "VCM oil consumption is possible on V6. Install VCM Muzzler.",
        "105000": "Timing belt is due. Critical maintenance.",
        "150000": "Transmission judder may appear. Fluid service helps."
    },
    "land_rover_range_rover_sport_l494": {
        "60000": "Coolant pipes fail. Proactive replacement recommended.",
        "80000": "Timing chain service on V8 is expensive ($7500+).",
        "120000": "Air suspension components wear. Budget $3k+ for rebuild.",
        "140000": "You are at expected end of life. Major repairs exceed value."
    },
    
    # Sports Cars
    "ford_mustang_s550": {
        "60000": "MT-82 manual transmission (2015-2017) has issues. Tremec swap is popular.",
        "100000": "10-speed auto is reliable. Coyote V8 is bulletproof.",
        "150000": "Differential whine may appear. Fluid change helps."
    },
    
    # Vans
    "chrysler_pacifica_ru": {
        "50000": "Electrical issues and stalling common on early models.",
        "80000": "Hybrid models had head gasket issues. Check if addressed.",
        "120000": "Rust is accelerated on early years. Inspect underbody."
    },
    "toyota_sienna_xl30": {
        "100000": "Sliding door cable/motor replacement is common. Budget $600.",
        "150000": "Transmission may develop slight flare. Not critical.",
        "250000": "These regularly hit 300k miles. Keep driving."
    },
    
    # Full-Size SUVs
    "chevrolet_tahoe_4th": {
        "100000": "AFM/DFM lifter failure is the primary concern. Listen for ticking.",
        "150000": "Magnetic ride shocks (if equipped) may need replacement.",
        "200000": "If lifters survived, engine will go forever."
    },
    "chevrolet_traverse_2nd": {
        "80000": "Shift to Park message is common. Shifter cable needs adjustment.",
        "100000": "Transmission control module issues possible.",
        "150000": "Evap purge valve may trigger check engine light."
    },
    
    # Trucks
    "toyota_tundra_2nd": {
        "100000": "Cam tower seals may leak oil. Monitor and address.",
        "150000": "Secondary air injection pump may fail. Check engine light common.",
        "300000": "The 5.7L V8 is legendary. These easily exceed 400k miles."
    },
    "nissan_frontier_d40": {
        "100000": "Pre-2010 models have SMOD (strawberry milkshake) transmission contamination risk.",
        "120000": "Replace radiator preventively if not done. Critical.",
        "200000": "VQ40 engine is solid. Frame rust is the killer in northern climates."
    },
    
    # German Compact Luxury
    "volkswagen_jetta_a6": {
        "40000": "DSG service mandatory. Skipping leads to mechatronic failure.",
        "80000": "Timing chain tensioner (2.0T) failure is possible on early models.",
        "120000": "Water pump is weak point. Replace proactively."
    },
    "volkswagen_tiguan_5n": {
        "60000": "Carbon buildup on TSI engine. Walnut blasting recommended.",
        "80000": "Timing chain tensioner on early 2.0T is critical check.",
        "120000": "Water pump failure common. Address proactively."
    },
    "volkswagen_jetta_mk6": {
        "60000": "Same timing chain tensioner concern as Tiguan.",
        "90000": "Updated tensioner required if not already done.",
        "140000": "If tensioner survived, the engine is solid."
    },
    
    # Mazda
    "mazda3_bm": {
        "80000": "SKYACTIV engine is exceptionally reliable. Just maintain.",
        "120000": "Clutch (manual) may need replacement. Normal wear.",
        "180000": "Rust in northern climates is the primary concern, not mechanicals."
    },
    
    # Volvo
    "volvo_xc90_spa": {
        "80000": "Piston ring issues on 2016 models. Oil consumption indicates problem.",
        "100000": "Air suspension may need service on equipped models.",
        "140000": "Sensus infotainment can be slow. Software updates help."
    }
}

def main():
    # Load current reliability data
    with open(RELIABILITY_PATH, 'r', encoding='utf-8-sig') as f:
        reliability_data = json.load(f)
    
    updated_count = 0
    
    # Process each model
    for model in reliability_data:
        model_id = model.get('model_id', '')
        
        # Skip if already has mileage_logic_text
        if 'mileage_logic_text' in model and model['mileage_logic_text']:
            continue
        
        # Check if we have a template for this model
        if model_id in MILEAGE_LOGIC_TEMPLATES:
            model['mileage_logic_text'] = MILEAGE_LOGIC_TEMPLATES[model_id]
            updated_count += 1
            print(f"[OK] Added mileage_logic_text to: {model_id}")
    
    # Save updated data
    with open(RELIABILITY_PATH, 'w', encoding='utf-8') as f:
        json.dump(reliability_data, f, indent=4, ensure_ascii=False)
    
    print(f"\n[DONE] Updated {updated_count} models with mileage_logic_text")

if __name__ == "__main__":
    main()
