package org.ohdsi.metaAnalysis;

import dr.inference.distribution.EmpiricalDistributionData;
import dr.inference.distribution.EmpiricalDistributionLikelihood;
import dr.math.distributions.NormalDistribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class NormalDataModel extends DataModel.Base implements DataModel {

	public NormalDataModel(String fileName) {
		this();
		File file = new File(fileName);
		List<List<String>> lines = new ArrayList<>();

		try {

			Scanner inputStream = new Scanner(file);

			while (inputStream.hasNext()) {
				String line = inputStream.next();
				line = line.replaceAll("\"", "");
				String[] values = line.split(",");
				lines.add(Arrays.asList(values));
			}

			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int logRrIdx = lines.get(0).indexOf("logRr");
		int seLogRrIdx = lines.get(0).indexOf("seLogRr");
		for (int i = 1; i < lines.size(); ++i) {
			double[] parameters = new double[] { Double.parseDouble(lines.get(i).get(logRrIdx)),
					Double.parseDouble(lines.get(i).get(seLogRrIdx)) };
			addLikelihoodParameters(parameters, null);
		}
		finish();
	}

	public NormalDataModel() {
	}

	@Override
	EmpiricalDistributionLikelihood makeFunctionalForm(List<EmpiricalDistributionData> dataList) {

		return new EmpiricalDistributionLikelihood(dataList, false) {

			private static final long serialVersionUID = 6515855145410583409L;

			@Override
			protected double logPDF(double x, EmpiricalDistributionData data) {

				final double mean = data.values[0];
				final double stdDev = data.values[1];

				return NormalDistribution.logPdf(x, mean, stdDev);
			}

			@Override
			protected double gradientLogPdf(double x, EmpiricalDistributionData data) {
				final double mean = data.values[0];
				final double stdDev = data.values[1];

				return NormalDistribution.gradLogPdf(x, mean, stdDev);
			}
		};
	}
}