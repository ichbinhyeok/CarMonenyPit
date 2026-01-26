@import com.carmoneypit.engine.api.InputModels.VehicleType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Car Money Pit Analyzer</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
    <style>
        body { padding-top: 2rem; }
        .container { max-width: 600px; }
        .hero { text-align: center; margin-bottom: 2rem; }
    </style>
</head>
<body>
    <main class="container">
        <div class="hero">
            <h1>Car Money Pit Analyzer</h1>
            <p>Should you fix it or ditch it? Get a regret-based verdict in seconds.</p>
        </div>

        <article>
            <form action="/analyze" method="post">
                <label for="vehicleType">Vehicle Type</label>
                <select name="vehicleType" id="vehicleType" required>
                    @for(VehicleType type : VehicleType.values())
                        <option value="${type}">${type}</option>
                    @endfor
                </select>

                <label for="mileage">Current Mileage (miles)</label>
                <input type="number" name="mileage" id="mileage" placeholder="e.g. 120000" required>

                <label for="repairQuoteUsd">Repair Quote ($)</label>
                <input type="number" name="repairQuoteUsd" id="repairQuoteUsd" placeholder="e.g. 1500" required>

                <button type="submit">Analyze My Money Pit</button>
            </form>
        </article>
    </main>
</body>
</html>
