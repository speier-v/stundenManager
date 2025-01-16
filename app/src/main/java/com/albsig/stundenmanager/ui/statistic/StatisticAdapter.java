package com.albsig.stundenmanager.ui.statistic;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {

    private List<UserStatistic> statistics;

    public StatisticAdapter(List<UserStatistic> statistics) {
        this.statistics = statistics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_statistic_with_graph, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserStatistic stat = statistics.get(position);
        holder.tvUserName.setText(stat.getUserName());

        populateBarChart(holder.barChart, stat.getExpectedTimePerWeek(), stat.getActualTimePerWeek(), stat.getShiftDateLabels());
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }

    private void populateBarChart(BarChart barChart, Map<String, Integer> expected, Map<String, Integer> actual, List<String> shiftDateLabels) {
        List<BarEntry> expectedEntries = new ArrayList<>();
        List<BarEntry> actualEntries = new ArrayList<>();
        final List<String> weeks = new ArrayList<>(expected.keySet());
        List<String> labels = shiftDateLabels;

        for (int i = 0; i < weeks.size(); i++) {
            String week = weeks.get(i);
            expectedEntries.add(new BarEntry(i, expected.getOrDefault(week, 0) / 60f));
            actualEntries.add(new BarEntry(i, actual.getOrDefault(week, 0) / 60f));
        }

        BarDataSet expectedDataSet = new BarDataSet(expectedEntries, "Expected (hrs)");
        expectedDataSet.setColor(Color.RED);
        BarDataSet actualDataSet = new BarDataSet(actualEntries, "Actual (hrs)");
        actualDataSet.setColor(Color.GREEN);

        BarData data = new BarData(expectedDataSet, actualDataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);

        // Configure X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setAvoidFirstLastClipping(true);
        //xAxis.setLabelCount(5, true);

        // Configure Y-Axis
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(10f);
        yAxis.setGranularity(1f);
        yAxis.setDrawLabels(true);
        yAxis.setDrawGridLines(true);

        // Style BarChart
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.invalidate();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        BarChart barChart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            barChart = itemView.findViewById(R.id.barChart);
        }
    }
}
