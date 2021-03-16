
// init map
const map = L.map('map').setView([52.178774, 10.559594], 8);
let heat = undefined
let lastBiggestMapBounds = map.getBounds()
let isShowHeadMap = false

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);


var statesData;
var geojson;

//call the h:commandScript method to perform a ajax request
loadGeoData();
//call the h:commandScript method to perform a ajax request
loadChartData();


function getColor(d) {
    if (isNaN(d)) {
        // if (typeof d.destatis == 'undefined') {
        //     d = 0
        // } else {
        //     d = d.destatis.population_density
        // }
        if (typeof d.cases == 'undefined') {
            d = 0
        } else {
            d = d.cases
        }
    }
    return d > 250 ? '#800026' :
        d > 200  ? '#BD0026' :
            d > 150  ? '#E31A1C' :
                d > 100  ? '#FC4E2A' :
                    d > 50   ? '#FD8D3C' :
                        d > 35   ? '#FEB24C' :
                            d > 0   ? '#FED976' :
                                '#FFEDA000';
}

function styleMap(feature) {
    return {
        fillColor: getColor(feature.properties),
        weight: 1,
        opacity: 1,
        color: '#636263',
        dashArray: '3',
        fillOpacity: 0.7
    };
}




/**
 * get called from the server as ajax response (EvalScripts)
 * @param data
 */
function serverCallBack_GeoData(data){
    console.log(data); // this will show the info it in firebug console
    statesData = data.features;


    if (typeof geojson != 'undefined') {
        map.removeLayer(geojson);
    }
    geojson = L.geoJson(data.features, {
        style: styleMap,
        onEachFeature: onEachFeature
    }).addTo(map);

    //map.setView([52.178774, 10.559594], 10);
    var lat = data.features[0].geometry.coordinates[0][0][1];
    if (Array.isArray(lat)) {
        lat = lat[1];
    }
    var lng = data.features[0].geometry.coordinates[0][0][0];
    if (Array.isArray(lng)) {
        lng = lng[0];
    }
    map.setView([lat,lng], 8);
}



// ----- Mousover -----
function highlightFeature(e) {
    var layer = e.target;

    layer.setStyle({
        weight: 2,
        color: '#505050',
        dashArray: '',
        fillOpacity: 0.8
    });

    if (!L.Browser.ie && !L.Browser.opera && !L.Browser.edge) {
        layer.bringToFront();
    }
    info.update(layer.feature.properties);
}

function resetHighlight(e) {
    geojson.resetStyle(e.target);
    info.update();
}

function zoomToFeature(e) {
    map.fitBounds(e.target.getBounds());
}

function onEachFeature(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetHighlight,
        click: zoomToFeature
    });
}

// ----- Control Panal -----
var info = L.control();

info.onAdd = function (map) {
    this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
    this.update();
    return this._div;
};

// method that we will use to update the control based on feature properties passed
info.update = function (properties) {
    this._div.innerHTML = '<h4>COVID-19 Cases</h4><p></p>' +  (properties ?
        '<b>' + properties.GEN + '</b><br />' +
        '<p>Confirmed Cases<br>' + properties.cases + '</p>' +
        '<p>7 Day Incidence Rates<br>' + properties.sevenDayIncidence + '</p>'
        : 'Hover over a region');
};
info.addTo(map);

// ----- Legend -----
var legend = L.control({position: 'bottomright'});
legend.onAdd = function (map) {
    var div = L.DomUtil.create('div', 'info legend'),
        grades = [0, 1, 35, 50, 100, 150, 200, 250],
        labels = [];

    // loop through our density intervals and generate a label with a colored square for each interval
    for (var i = 0; i < grades.length; i++) {
        div.innerHTML +=
            '<i style="background:' + getColor(grades[i] + 1) + '"></i> ' +
            grades[i] + (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');
    }
    return div;
};
legend.addTo(map);


/***
 * initialize Chart
 */
var ctx = document.getElementById('myChart').getContext('2d');
var myChart = new Chart(ctx, {
    type: 'line',
    options: {
        responsive: false,
        legend: {
            display: false
        },
        tooltips: {
            mode: 'index',
            intersect: false,
        },
        hover: {
            mode: 'nearest',
            intersect: true
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: false,
                    labelString: 'Month'
                }
            }],
            yAxes: [{
                display: true,
                scaleLabel: {
                    display: false,
                    labelString: 'Value'
                }
            }]
        }
    }
});

/***
 * Chart server call back
 * @param data
 */
function serverCallBack_Chart(data){
    myChart.data = {
            labels: data[1],
            datasets: [{
                label: 'Cases',
                backgroundColor: '#00526b',
                borderColor: '#00526b',
                data: data[0],
                fill: false,
            }]
    }
    myChart.update();
}

