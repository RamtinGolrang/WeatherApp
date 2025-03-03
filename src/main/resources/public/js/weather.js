document.addEventListener('DOMContentLoaded', () => {
    const searchBtn = document.getElementById('searchBtn');
    const cityInput = document.getElementById('cityInput');
    const weatherCard = document.getElementById('weatherCard');
    const errorMsg = document.getElementById('errorMsg');

    // Elementen för väderdata
    const cityName = document.getElementById('cityName');
    const temperature = document.getElementById('temperature');
    const description = document.getElementById('description');
    const windSpeed = document.getElementById('windSpeed');
    const humidity = document.getElementById('humidity');
    const updatedAt = document.getElementById('updatedAt');

    // Sök när användaren klickar på sökknappen
    searchBtn.addEventListener('click', () => {
        searchWeather();
    });

    // Sök när användaren trycker Enter
    cityInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            searchWeather();
        }
    });

    // Funktion för att hämta väderdata
    function searchWeather() {
        const city = cityInput.value.trim();
        if (!city) {
            showError('Vänligen ange en stad');
            return;
        }

        // Dölj eventuella felmeddelanden
        errorMsg.style.display = 'none';

        // Anropa API:et
        fetch(`/weather?city=${encodeURIComponent(city)}`)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.error || 'Ett fel inträffade');
                    });
                }
                return response.json();
            })
            .then(data => {
                // Uppdatera UI med väderdata
                cityName.textContent = city.charAt(0).toUpperCase() + city.slice(1).toLowerCase();
                temperature.textContent = `${data.temperature.toFixed(1)}°C`;
                description.textContent = data.description;
                document.getElementById('weatherIcon').className = `weather-icon ${getWeatherIconClass(data.description)}`;
                windSpeed.textContent = `${data.windSpeed.toFixed(1)} m/s`;
                humidity.textContent = `${data.humidity.toFixed(0)}%`;

                // Formatera datum
                const date = new Date(data.updatedAt);
                updatedAt.textContent = date.toLocaleString('sv-SE');

                // Visa väderkortet
                weatherCard.style.display = 'block';
            })
            .catch(error => {
                showError(error.message);
                weatherCard.style.display = 'none';
            });
    }

    // Funktion för att visa felmeddelanden
    function showError(message) {
        errorMsg.textContent = message;
        errorMsg.style.display = 'block';
    }

    // Funktion som hämtar Font Awesome ikon baserat på väder beskrivningen
    function getWeatherIconClass(description) {
        if (description === 'Klar himmel') {
            return 'fa-solid fa-sun';
        } else if (description === 'Mestadels klart' || description === 'Delvis molnigt') {
            return 'fa-solid fa-cloud-sun';
        } else if (description === 'Molnigt') {
            return 'fa-solid fa-cloud';
        } else if (description === 'Regn' || description === 'Snöblandat regn') {
            return 'fa-solid fa-cloud-rain';
        } else if (description === 'Snö') {
            return 'fa-solid fa-snowflake';
        } else if (description === 'Åska') {
            return 'fa-solid fa-cloud-bolt';
        } else if (description === 'Dimma') {
            return 'fa-solid fa-smog';
        }else {
            return 'fa-solid fa-cloud';
        }
    }
});