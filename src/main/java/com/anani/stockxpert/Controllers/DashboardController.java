package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Repository.ArticleRepository;
import com.anani.stockxpert.Repository.DepenseRepository;
import com.anani.stockxpert.Repository.FactureRepository;
import com.anani.stockxpert.Service.ArticleService;
import com.anani.stockxpert.Service.DepenseService;
import com.anani.stockxpert.Service.FactureService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private PieChart articleCategoriePie;

    @FXML
    private BarChart<?, ?> barChartRevenue;

    ObservableList<PieChart.Data> dataAC = FXCollections.observableArrayList();

    private ArticleService articleService;
    private FactureService factureService;
    private DepenseService depenseService;

    public DashboardController() {

        this.articleService = new ArticleRepository();
        this.factureService = new FactureRepository();
        this.depenseService = new DepenseRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadData();
    }

    private void refreshData() {
        dataAC.clear();
    }

    public void loadData() {
        refreshData();
        List<?> results = articleService.getCountCategorie();

        for (Object result : results) {
            Object[] row = (Object[]) result;
            Long count = (Long) row[0];
            String libelle = (String) row[1] + " (" + row[0] + ")";
            dataAC.add(new PieChart.Data(libelle, count));
        }

        articleCategoriePie.setTitle("Catégorie des articles");
        articleCategoriePie.setData(dataAC);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<Object[]> revenueData = factureService.getRevenue();
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Revenue Mensuelle " + currentYear);

        List<Object[]> depenseData = depenseService.getTotalDepense();
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Depense Mensuelle " + currentYear);

        for (Object[] row : revenueData) {
            int month = (int) row[0];
            long revenue = (long) row[1];

            // Conversion du mois en format MMMM (ex: janvier, février, etc.)
            String monthName = new SimpleDateFormat("MMMM", Locale.FRENCH)
                    .format(new Date(currentYear - 1900, month - 1, 1));

            series1.getData().add(new XYChart.Data<>(monthName, revenue));

        }

        for (Object[] row : depenseData) {
            int month = (int) row[0];
            long dep = (long) row[1];

            // Conversion du mois en format MMMM (ex: janvier, février, etc.)
            String monthName = new SimpleDateFormat("MMMM", Locale.FRENCH)
                    .format(new Date(currentYear - 1900, month - 1, 1));

            series2.getData().add(new XYChart.Data<>(monthName, dep));

        }
//        barChartRevenue.setTitle("Revenue Mensuelle");
        barChartRevenue.getData().addAll(series1, series2);
    }
}
