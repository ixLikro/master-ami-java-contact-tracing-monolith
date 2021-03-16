package de.ami.team1.visualization.control;

import de.ami.team1.visualization.model.Chart;
import de.ami.team1.visualization.model.MapPoint;
import de.ami.team1.visualization.model.Region;
import de.ami.team1.visualization.model.State;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.SessionScoped;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SessionScoped
public class GeoDataProvider implements Serializable {

    private ArrayList<Region> states;
    private HashMap<State, ArrayList<Region>> regions;

    /**
     * Constructor
     * creates list of states
     * deserialize geo json to region objects
     */
    public GeoDataProvider() {
        String StatesJson = loadGeoData("states");
        states = geoDataToRegionList(StatesJson);
        states = setState(states);
        regions = new HashMap<>();
        initRegionMap();
    }

    /**
     * creates region map of all states
     * deserialize geo json to region objects
     */
    private void initRegionMap() {
        for (State state : State.values()) {
            String json = loadGeoData(state.getStateValue());
            ArrayList<Region> regionList = geoDataToRegionList(json);
            regions.put(state, regionList);
        }
    }

    /**
     * returns chart data of a selected state
     * @param state selected state
     * @return chart data of selected state
     */
    public Chart getChartOf(State state) {
        for (Region region : this.states) {
            if (region.getState() == state) {
                return region.getChart();
            }
        }
        return null;
    }

    /**
     * Clear all chart data
     */
    public void clearCharts() {
        this.states.forEach(region -> region.getChart().clearChart());
    }

    /**
     * Add chart data to a state
     * @param points map points of case
     * @param date data of the case
     */
    public void addChartData(List<MapPoint> points, String date) {
        for (MapPoint point : points) {
            Region state = searchStateObj(point);
            if (state != null) {
                state.getChart().incrementCase(date);
            }
        }
    }

    /**
     * Add multiple incidences to a region
     * (seven day incidence rate)
     * @param points map points of incidences
     */
    public void addIncidences(List<MapPoint> points) {
        this.regions.forEach((s, r) -> r.forEach(region -> region.setSevenDayIncidence(0)));
        for (MapPoint point : points) {
            addIncidence(point);
        }
    }

    /**
     * Add a incidence to a region
     * (seven day incidence rate)
     * @param point map point of incidence
     */
    private void addIncidence(MapPoint point) {
        State state = searchStates(point);
        if (state != null) {
            Region region = searchRegions(state, point);
            if (region != null) {
                region.incrementIncidence();
            }
        }
    }

    /**
     * Add multiple cases to a region
     * (active cases)
     * @param points map points of cases
     */
    public void addCases(List<MapPoint> points) {
        this.regions.forEach((s, r) -> r.forEach(region -> region.setCases(0)));
        for (MapPoint point : points) {
            addCase(point);
        }
    }

    /**
     * Add a single case to a region
     * (active cases)
     * @param point map points of cases
     */
    private void addCase(MapPoint point) {
        State state = searchStates(point);
        if (state != null) {
            Region region = searchRegions(state, point);
            if (region != null) {
                region.incrementCases();
            }
        }
    }

    /**
     * Search state list by map point
     * and return the matching state object
     * @param point map point
     * @return matching state object
     */
    private State searchStates(MapPoint point) {
        Region region = searchStateObj(point);
        if (region != null) {
            return region.getState();
        } else {
            return null;
        }
    }

    /**
     * Search state list by map point
     * and return the matching region object representing the state
     * @param point
     * @return
     */
    private Region searchStateObj(MapPoint point) {
        for (Region state : states) {
            for (Polygon polygon : state.getPolygons()) {
                if (polygon.contains((int) (point.getLatitude() * 1000000), (int) (point.getLongitude() * 1000000))) {
                    return state;
                }
            }
        }
        return null;
    }

    /**
     * Search region by state and map point
     * @param state state of the region
     * @param point point of the region
     * @return matching region object
     */
    private Region searchRegions(State state, MapPoint point) {
        for (Region region : this.regions.get(state)) {
            for (Polygon polygon : region.getPolygons()) {
                if (polygon.contains((int) (point.getLatitude() * 1000000), (int) (point.getLongitude() * 1000000))) {
                    return region;
                }
            }
        }
        return null;
    }

    /**
     * Return geo json with case information and seven day incidence rate
     * @param state of the requested regions
     * @return json of all regions in requested state
     */
    public String getGeoJson(State state) {
        String jsonStr = loadGeoData(state.getStateValue());
        JSONObject json = new JSONObject(jsonStr);

        for (Region region : this.regions.get(state)) {
            json.getJSONArray("features")
                    .getJSONObject(region.getFeatureIndex()).getJSONObject("properties")
                    .put("cases", region.getCases());
            json.getJSONArray("features")
                    .getJSONObject(region.getFeatureIndex()).getJSONObject("properties")
                    .put("sevenDayIncidence", region.getSevenDayIncidence());
        }
        return json.toString();
    }

    /**
     * Return region list of specified state
     * @param state selected state
     * @return region list
     */
    public List<Region> getRegions(State state) {
        return regions.get(state);
    }

    /**
     * Reads geo json from the filesystem
     * @param file file name
     * @return json
     */
    private String loadGeoData(String file) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("geodata/" + file + ".json");
        String data;
        data = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
                .parallel().collect(Collectors.joining("\n"));
        return data;
    }

    /**
     * Creates region list
     * @param geoData geo json
     * @return list of regions
     */
    private ArrayList<Region> geoDataToRegionList(String geoData) {
        ArrayList<Region> regions = new ArrayList<>();
        JSONArray features = new JSONObject(geoData).getJSONArray("features");
        for (int i = 0; i < features.length(); i++) {
            JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
            JSONArray coordinates = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
            List<Polygon> polygons = createMultiPolygon(coordinates);
            Region region = new Region(properties.getString("GEN"), polygons, i);
            if (properties.has("destatis") && properties.getJSONObject("destatis").has("population")) {
                region.setPopulation(properties.getJSONObject("destatis").getInt("population"));
            }
            regions.add(region);
        }
        return regions;
    }

    /**
     * Creates multi polygon using the geo data coordinates
     * @param coordinates json array of coordinates representing multi polygon
     * @return list of polygon representing a region
     */
    private ArrayList<Polygon> createMultiPolygon(JSONArray coordinates) {
        ArrayList<Polygon> polygons = new ArrayList<>();
        ArrayList<Integer> xPoints = new ArrayList<>();
        ArrayList<Integer> yPoints = new ArrayList<>();
        for (Object obj : coordinates) {
            if (obj instanceof JSONArray) {
                if (!(((JSONArray) obj).get(0) instanceof JSONArray) && ((JSONArray) obj).get(0) != null) {
                    // add coordinates
                    xPoints.add((int) (((JSONArray) obj).getBigDecimal(1).doubleValue() * 1000000));
                    yPoints.add((int) (((JSONArray) obj).getBigDecimal(0).doubleValue() * 1000000));
                } else if (((JSONArray) obj).get(0) instanceof JSONArray) {
                    // move forward
                    polygons.addAll(createMultiPolygon((JSONArray) obj));
                }
            }
        }
        // create polygon
        if (!xPoints.isEmpty() && !yPoints.isEmpty()) {
            Polygon polygon;
            polygon = new Polygon(
                    xPoints.stream().mapToInt(x -> x).toArray(),
                    yPoints.stream().mapToInt(y -> y).toArray(),
                    xPoints.size()
            );
            polygons.add(polygon);
        }
        return polygons;
    }

    /**
     * Set state value of region object
     * @param regions list of regions
     * @return list of regions with state value
     */
    private ArrayList<Region> setState(ArrayList<Region> regions) {
        for (Region region : regions) {
            for (State state : State.values()) {
                if (region.getName().equalsIgnoreCase(state.getStateName())) {
                    region.setState(state);
                    break;
                }
            }
        }
        return regions;
    }
}
