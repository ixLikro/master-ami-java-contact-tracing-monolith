package de.ami.team1.visualization.model;

import org.json.JSONArray;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chart {
    private List<Integer> cases;
    private List<String> date;

    public Chart() {
        initChart();
    }

    public Chart(List<Integer> cases, List<String> date) {
        this.cases = cases;
        this.date = date;
    }

    private void initChart() {
        this.cases = new ArrayList<>();
        this.date = new ArrayList<>();
        LocalDate init = LocalDate.now();
        for (int i = 0; i < ChartHistory.MONTH.getMonth(); i++) {
            this.date.add(init.minusMonths(i).getMonth().toString());
            this.cases.add(0);
        }
        Collections.reverse(cases);
        Collections.reverse(date);
    }

    public void incrementCase(String strDate) {
        if (!this.date.isEmpty()) {
            for (int i = 0; i < this.date.size(); i++) {
                if (this.date.get(i).equalsIgnoreCase(strDate)) {
                    int ca = this.cases.get(i).intValue();
                    ca++;
                    this.cases.set(i, ca);
                    return;
                }
            }
        }
    }

    public void clearChart() {
        initChart();
    }

    public List<Integer> getCases() {
        return cases;
    }

    public void setCases(List<Integer> cases) {
        this.cases = cases;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public String toJsonArray() {
        JSONArray data = new JSONArray();
        data.put(cases);
        data.put(date);
        return data.toString();
    }
}
