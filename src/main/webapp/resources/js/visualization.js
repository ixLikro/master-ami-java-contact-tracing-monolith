
// init map
const map = L.map('map').setView([52.178774, 10.559594], 10);
let heat = undefined
let lastBiggestMapBounds = map.getBounds()
let isShowHeadMap = false
const movementMap = new Map()
let visibleCircles = []

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);


map.on("moveend", (event)=>{
    if (!lastBiggestMapBounds.contains(map.getBounds())){
        //after a map move is a new part of the map visible -> load heat map data data
        lastBiggestMapBounds = map.getBounds()
        if (isShowHeadMap){
            loadHeatMapData()
        }
    }

    if (map.getZoom() >= 14){
        //check if circles are not seen any more -> remove them
        visibleCircles = visibleCircles.filter((visCircle, index, arr) => {
            if (!map.getBounds().contains(visCircle.movement)){
                visCircle.circle.removeFrom(map)
                return false;
            }
            return true;
        })

        movementMap.forEach((value, userId) => {
           value.rawMovement.forEach(aMovement => {
               if (map.getBounds().contains(aMovement)){

                   //check if the movement already has a drawn circle
                   const found = visibleCircles.find(visCircle => {
                       return visCircle.movement.toString() === aMovement.toString()
                   })

                   //not found -> add a new circle
                   if(!found){
                       const circle = L.circleMarker(aMovement, {radius: 8, color: value.color})
                           .bindTooltip(value.email+ ", "+new Date(aMovement[2]).toLocaleString())
                           .addTo(map)
                       visibleCircles.push({userId: userId, movement: aMovement, circle: circle})
                   }
               }
           })
        });
    }else {
        // the user zoomed out -> remove all circles
        visibleCircles.forEach(value => value.circle.removeFrom(map))
        visibleCircles = []
    }

})


/**
 * load heat map data between the given points from the server and render them.
 * if at lest one point is undefined, the current map bounds are used as points.
 * @param point1 the northEast point of the data that should be loaded
 * @param point2 the _southWest point of the data that should be loaded
 */
function loadHeatMapData(point1 = undefined, point2 = undefined){
    setHeatmapLoadState(true)

    if (!point1 || !point2){
        //use the current map bounds to load data
        const bounds = map.getBounds()
        point1=[bounds._northEast.lat, bounds._northEast.lng]
        point2=[bounds._southWest.lat, bounds._southWest.lng]
    }

    //call the h:commandScript method to perform a ajax request
    loadHeatDataFromServer({
        p1Latitude: point1[0],
        p1Longitude: point1[1],
        p2Latitude: point2[0],
        p2Longitude: point2[1],
    })
}

/**
 * onchange method of the checkbox "showHeatmap"
 */
function showHeatmap(){
    const show = document.getElementById("showHeatmapCheckbox").checked
    if(show){
        heat = loadHeatMapData()
        isShowHeadMap = true
        lastBiggestMapBounds = map.getBounds()
    }else {
        isShowHeadMap = false
        heat.remove(map)
    }
}

/**
 * get called from the server as ajax response (EvalScripts)
 * @param data the data as array, each point [0]: latitude, [1]: longitude, [2]: intensity
 */
function serverCallback_HeatmapData(data){
    if(heat){
        heat.remove(map)
    }
    if(isShowHeadMap){
        heat = L.heatLayer(data).addTo(map);
    }

    setHeatmapLoadState(false)
}

/**
 * get called from the server as ajax response (EvalScripts)
 * @param data the data as object, with the following form: {
 *        email: "usermail",
 *        color: "#hexColor"
 *        id: userId,
 *        movement: [[latitude, longitude, timestamp],[latitude, longitude, timestamp], ...]}
 */
function serverCallback_newUserSelected(data){
    const path = L.polyline(data.movement, {color: data.color, interactive: false}).addTo(map)
    movementMap.set(data.id, {
        path: path,
        rawMovement: data.movement,
        color: data.color,
        email: data.email
    })
}

/**
 * get called from the server as ajax response (EvalScripts)
 * @param userId the id if the unselected user
 */
function serverCallback_userUnselected(userId){
    const value = movementMap.get(userId);
    value.path.remove(map);
    movementMap.delete(userId)
}

/**
 * Enable or disable the the heatmap load state
 * @param state bool: true -> enable load state, false -> disable load state
 */
function setHeatmapLoadState(state){
    const label = document.getElementById("showHeatmapLabel")
    const spinner = document.getElementById("heatmapSpinner")
    if(state){
        label.classList.add("hidden")
        spinner.classList.remove("hidden")
    }else {
        label.classList.remove("hidden")
        spinner.classList.add("hidden")
    }
}

function flyToUserMovement(userId){
    map.flyToBounds(movementMap.get(userId).path.getBounds())
}


