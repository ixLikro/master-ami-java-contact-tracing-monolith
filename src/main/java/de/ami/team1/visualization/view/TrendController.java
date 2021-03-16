package de.ami.team1.visualization.view;

import de.ami.team1.visualization.control.GeoDataProvider;
import de.ami.team1.visualization.control.TrendDataProvider;
import de.ami.team1.visualization.model.*;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Named
@ViewScoped
public class TrendController implements Serializable {

    @Inject
    private FacesContext context;

    @Inject
    private GeoDataProvider geoDataProvider;

    @Inject
    private TrendDataProvider trendDataProvider;

    private State selectedState;
    private List<Region> regions;
    // pagination
    private List<Region> filteredRegions;
    private int regionPage = 0;
    private int regionPageSize = 20;

    @PostConstruct
    public void init() {
        selectedState = State.Niedersachsen;
        loadUserData();
        loadGeoData();
    }

    /**
     * Loads user data for trend usage
     * using GeoDataProvider to map all coordinates to regions
     *
     * Loads: Chart data, incidence rate and active cases
     */
    private void loadUserData() {
        // read chart data
        geoDataProvider.clearCharts();
        LocalDate init = LocalDate.now();
        List<List<MapPoint>> chart = trendDataProvider.getChart();
        for (int i = 0; i < chart.size(); i++) {
            geoDataProvider.addChartData(chart.get(i), init.minusMonths(i).getMonth().toString());
        }
        // read active cases
        List<MapPoint> cases = trendDataProvider.getActiveCases();
        if (cases != null) {
            geoDataProvider.addCases(cases);
        }
        // read incidence data
        List<MapPoint> incidence = trendDataProvider.getIncidences();
        if (incidence != null) {
            geoDataProvider.addIncidences(incidence);
        }
        // set regions
        regions = geoDataProvider.getRegions(selectedState);
    }

    /**
     * Change selected state
     */
    public void changeState() {
        regions = geoDataProvider.getRegions(selectedState);
        loadGeoData();
        loadGraphData();
        System.out.println(selectedState.getStateName());
    }

    /**
     * load geo json from GeoDataProvider
     *
     * adds javaScript code to the ajax response as evalScript.
     * The script will render all regions of the selected state as leafLet layer.
     */
    public void loadGeoData() {
        String data = geoDataProvider.getGeoJson(selectedState);
        loadRegionPage();
        regionPage = 0;
        context.getPartialViewContext()
                .getEvalScripts()
                .add("serverCallBack_GeoData(" + data + ");");
    }

    /**
     * load graph json from GeoDataProvider
     *
     * adds javaScript code to the ajax response as evalScript.
     * The script will render the graph.
     */
    public void loadGraphData() {
        Chart chart = geoDataProvider.getChartOf(selectedState);
        context.getPartialViewContext()
                .getEvalScripts()
                .add("serverCallBack_Chart(" + chart.toJsonArray() + ");");
    }

    /**
     * Region list pagination
     * creates filtered region list
     */
    private void loadRegionPage() {
        int from = regionPage * regionPageSize;
        int to = from + regionPageSize;
        if (to >= regions.size()) {
            to = regions.size();
        }
        filteredRegions = IntStream.range(from, to)
                .mapToObj(i -> regions.get(i))
                .collect(Collectors.toList());
    }

    /**
     * Pagination
     * @return page count
     */
    public int regionsPageCount() {
        return (int) Math.ceil(regions.size() / (double) regionPageSize);
    }

    /**
     * Pagination
     * @return previous page number
     */
    public Integer regionPrevPage() {
        regionPage--;
        loadRegionPage();
        return regionPage;
    }

    /**
     * Pagination
     * @return new page number
     */
    public Integer regionNextPage() {
        regionPage++;
        loadRegionPage();
        return regionPage;
    }

    /**
     * Selected state
     * @return current selected state
     */
    public String getSelectedState() {
        return selectedState.getStateValue();
    }

    /**
     * Selected state
     * @param selectedState set the current selected state
     */
    public void setSelectedState(String selectedState) {
        for (State state : State.values()) {
            if (selectedState.equalsIgnoreCase(state.getStateValue())) {
                this.selectedState = state;
            }
        }
    }

    public List<Region> getFilteredRegions() {
        return filteredRegions;
    }

    public void setFilteredRegions(List<Region> filteredRegions) {
        this.filteredRegions = filteredRegions;
    }

    public int getRegionPage() {
        return regionPage;
    }

    public void setRegionPage(int regionPage) {
        this.regionPage = regionPage;
    }

    public int getRegionPageSize() {
        return regionPageSize;
    }

    public void setRegionPageSize(int regionPageSize) {
        this.regionPageSize = regionPageSize;
    }
}
