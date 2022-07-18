package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileReader;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.util.*;

public class artificialCovidDetection implements View.OnClickListener {
    TextView tv;
    private boolean conditionOne = true;
    private boolean conditionThree = true;
    private boolean conditionTwo = true;


    public artificialCovidDetection(TextView tv) {
        this.tv = tv;

    }

    // Calculates the mean.
    public static double calcMean(ArrayList<Double> values) {
        double totalSum = 0.0;
        for (int i = 0; i < values.size(); i++) {
            totalSum += values.get(i);
        }

        return totalSum / values.size();
    }

    // Calculates the Standard Deviation.
    public static double calcStd(ArrayList<Double> values) {

        double sigmaEvaluation = 0.0;
        for (int i = 0; i < values.size(); i++) {
            sigmaEvaluation += (values.get(i) - calcMean(values)) * (values.get(i) - calcMean(values));
        }

        return Math.sqrt(sigmaEvaluation / values.size());
    }

    // Calculates the Pearson Correlation Coefficient.
    public static double calcPCR(ArrayList<Double> valuesX, ArrayList<Double> valuesY) {

        double sigmaEvaluation = 0.0;
        double denominatorX = 0.0;
        double denominatorY = 0.0;
        for (int i = 0; i < valuesX.size(); i++) {
            sigmaEvaluation += (valuesX.get(i) - calcMean(valuesX)) * (valuesY.get(i) - calcMean(valuesY));
            denominatorX += (valuesX.get(i) - calcMean(valuesX)) * (valuesX.get(i) - calcMean(valuesX));
            denominatorY += (valuesY.get(i) - calcMean(valuesY)) * (valuesY.get(i) - calcMean(valuesY));
        }

        return sigmaEvaluation / (Math.sqrt(denominatorX * denominatorY));
    }

    // Calculates the Regression Slope.
    public static double calcRegressionSlope(ArrayList<Double> valuesX, ArrayList<Double> valuesY) {
        return calcPCR(valuesX, valuesY) * calcStd(valuesY) / calcStd(valuesX);
    }

    public static void main(String[] args) {

        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader("C:\\Users\\chuck\\positiveCase.csv"));

            String[] nextLine;

            ArrayList<String> xValues = new ArrayList<>();
            ArrayList<String> yValues = new ArrayList<>();

            int counter = 0;

            // Reads one line at a time, splitting the data into x and y coordinates.
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    counter++;

                    if (counter % 2 == 1) {
                        xValues.add(token);
                    } else {
                        yValues.add(token);
                    }
                }
                counter = 0;
            }

            ArrayList<Double> xValuesDouble = new ArrayList<>();
            ArrayList<Double> yValuesDouble = new ArrayList<>();

            // Casts the Strings into Doubles.
            for (int i = 0; i < xValues.size(); i++) {
                double token = Double.parseDouble(xValues.get(i));
                xValuesDouble.add(token);
            }

            for (int j = 0; j < yValues.size(); j++) {
                double token = Double.parseDouble(yValues.get(j));
                yValuesDouble.add(token);
            }

            double currentMax = xValuesDouble.get(0);
            double currentMin = yValuesDouble.get(0);

            for (int i = 0; i < 10; i++) {
                if (yValuesDouble.get(i) > currentMax) {
                    currentMax = yValuesDouble.get(i);
                }
                if (yValuesDouble.get(i) < currentMin) {
                    currentMin = yValuesDouble.get(i);
                }
            }

            double noise = currentMax - currentMin;

             boolean conditionOne = false;
             boolean conditionTwo = false;
            boolean conditionThree = false;

            // Splits the data points into 3 sections: 0-9, 10-20, and 21-30.
            ArrayList<Double> xValuesIntervalOne = new ArrayList<>();
            ArrayList<Double> yValuesIntervalOne = new ArrayList<>();
            ArrayList<Double> xValuesIntervalTwo = new ArrayList<>();
            ArrayList<Double> yValuesIntervalTwo = new ArrayList<>();
            ArrayList<Double> xValuesIntervalThree = new ArrayList<>();
            ArrayList<Double> yValuesIntervalThree = new ArrayList<>();

            for (int i = 0; i <= 30; i++) {
                if (i < 10) {
                    xValuesIntervalOne.add(xValuesDouble.get(i));
                    yValuesIntervalOne.add(yValuesDouble.get(i));
                } else if (i < 21 && i >= 10) {
                    xValuesIntervalTwo.add(xValuesDouble.get(i));
                    yValuesIntervalTwo.add(yValuesDouble.get(i));
                } else if (i >= 21) {
                    xValuesIntervalThree.add(xValuesDouble.get(i));
                    yValuesIntervalThree.add(yValuesDouble.get(i));
                }
            }

            // Conditions to satisfy in order to produce a diagnosis.
            if (calcRegressionSlope(xValuesIntervalTwo, yValuesIntervalTwo) * 10 > noise) {
                conditionOne = true;
            }

            if (calcRegressionSlope(xValuesIntervalTwo, yValuesIntervalTwo) > calcRegressionSlope(xValuesIntervalOne,
                    yValuesIntervalOne)
                    && calcRegressionSlope(xValuesIntervalTwo,
                    yValuesIntervalTwo) > calcRegressionSlope(xValuesIntervalThree, yValuesIntervalThree)) {
                conditionTwo = true;
            }

            if (calcRegressionSlope(xValuesIntervalThree, yValuesIntervalThree) < 0) {
                conditionThree = true;
            }

            if (conditionOne && conditionTwo && conditionThree) {
                System.out.print("The diagnosis is positive for COVID-19.");
            } else {
                System.out.print("The diagnosis is negative for COVID-19.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (conditionOne && conditionTwo && conditionThree) {
            tv.setText(R.string.positive);
        } else {
            tv.setText(R.string.negative);
        }

    }
}


