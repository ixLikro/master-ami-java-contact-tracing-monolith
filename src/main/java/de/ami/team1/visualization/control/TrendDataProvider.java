package de.ami.team1.visualization.control;

import de.ami.team1.analyse.VisualizationDataAnalyzer;
import de.ami.team1.visualization.model.ChartHistory;
import de.ami.team1.visualization.model.MapPoint;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Startup
public class TrendDataProvider {

    @Inject
    private VisualizationDataAnalyzer dataAnalyzer;

    private List<MapPoint> activeCases;
    private List<MapPoint> incidences;
    private List<List<MapPoint>> chart;

    @PostConstruct
    void init() {
        activeCases = new ArrayList<>();
        incidences = new ArrayList<>();
        chart = new ArrayList<>();
        loadUserData();
    }

    @Transactional
    @Schedule(hour = "*", minute = "*/15", persistent = false)
    public void atSchedule() {
        loadUserData();
    }

    private void loadUserData() {
        System.out.println("TrendScheduler: Read user data");
        // read chart data
        for (int i = 0; i < ChartHistory.MONTH.getMonth(); i++) {
            chart.add(dataAnalyzer.getCoordinatesOfInfectedUsersByMonth(i));
        }
        // read active cases
        List<List<MapPoint>> trendData = dataAnalyzer.readLatestMovementByInfectedUsers2();
        activeCases = trendData.get(0);
        // read incidence data
        incidences = trendData.get(1);
        System.out.println("TrendScheduler: User data loaded");
    }

    public List<MapPoint> getActiveCases() {
        return activeCases;
    }

    public List<MapPoint> getIncidences() {
        return incidences;
    }

    public List<List<MapPoint>> getChart() {
        return chart;
    }
}
