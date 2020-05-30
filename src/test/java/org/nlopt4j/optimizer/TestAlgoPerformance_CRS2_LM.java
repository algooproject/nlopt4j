package org.nlopt4j.optimizer;

import org.nlopt4j.optimizer.NLopt;
import org.nlopt4j.optimizer.NLoptResult;
import org.junit.Test;

import com.algotrading.backtesting.replay.MainForNLOPT;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TestAlgoPerformance_CRS2_LM {
	MainForNLOPT mainForNLOPT = MainForNLOPT.getInstance();

	static int random(int intMax, int intMin) {
		int intRange = intMax - intMin + 1;
		return (int) (Math.random() * intRange) + intMin;
	}

	static void printWithTime(String message) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String formattedDate = dateFormat.format(date);
		System.out.println("##########" + formattedDate + " " + message);
	}

	static final class Constraint implements NLopt.NLopt_func {		
		int intRSIMagnitude;
		int dblRSILowerThan;
		int intSMAMagnitude;
		int dblVolumeHigherThan;
		int intReentryRSIMagnitude;
		int dblReentryRSILowerThan;
		int dblTakeProfit;
		int dblStopLoss;
		
		Constraint(int intRSIMagnitude, 
				int dblRSILowerThan, 
				int intSMAMagnitude, 
				int dblVolumeHigherThan,
				int intReentryRSIMagnitude, 
				int dblReentryRSILowerThan, 
				int dblTakeProfit, 
				int dblStopLoss) {
			this.intRSIMagnitude = intRSIMagnitude;
			this.dblRSILowerThan = dblRSILowerThan;
			this.intSMAMagnitude = intSMAMagnitude;
			this.dblVolumeHigherThan = dblVolumeHigherThan;
			this.intReentryRSIMagnitude = intReentryRSIMagnitude;
			this.dblReentryRSILowerThan = dblReentryRSILowerThan;
			this.dblTakeProfit = dblTakeProfit;
			this.dblStopLoss = dblStopLoss;
		}

		@Override
		public double execute(double[] x, double[] gradient) {
			int intRSIMagnitude = this.intRSIMagnitude;
			int dblRSILowerThan = this.dblRSILowerThan;
			int	intSMAMagnitude = this.intSMAMagnitude;
			int dblVolumeHigherThan = this.dblVolumeHigherThan;
			int	intReentryRSIMagnitude = this.intReentryRSIMagnitude;
			int dblReentryRSILowerThan = this.dblReentryRSILowerThan;
			int dblTakeProfit = this.dblTakeProfit;
			int dblStopLoss = this.dblStopLoss;
			return intRSIMagnitude * x[0] + 
					dblRSILowerThan * x[1] + 
					intSMAMagnitude * x[2] + 
					dblVolumeHigherThan * x[3] + 
				    intReentryRSIMagnitude * x[4] + 
					dblReentryRSILowerThan * x[5] + 
					dblTakeProfit * x[6] + 
					dblStopLoss * x[7];
		}
	}

	@Test
	public void testTutorialSample() {
		NLopt.NLopt_func func = new NLopt.NLopt_func() {
			@Override
			public double execute(double[] x, double[] gradient) {
				// x0 int intRSIMagnitude,
				// x1 double dblRSILowerThan,
				// x2 int intSMAMagnitude,
				// x3 double dblVolumeHigherThan,
				// x4 int intReentryRSIMagnitude,
				// x5 double dblReentryRSILowerThan,
				// x6 double dblTakeProfit,
				// x7 double dblStopLoss

				// System.out.println(String.format("**********
				// f(%f,%f,%f,%f,%f,%f,%f,%f) = \n", x[0], x[1], x[2], x[3], x[4], x[5], x[6],
				// x[7]));
				x[1] = ((int) x[1]);
				x[3] = ((int) x[3]);
				x[5] = ((int) x[5]);
				x[6] = ((int) x[6]);
				x[7] = ((int) x[7]);
				// System.out.println(String.format("**********
				// f(%f,%f,%f,%f,%f,%f,%f,%f) = \n", x[0], x[1], x[2], x[3], x[4], x[5], x[6],
				// x[7]));
				x[1] = x[1] / 100;
				x[3] = x[3] / 100;
				x[5] = x[5] / 100;
				x[6] = x[6] / 100;
				x[7] = x[7] / 100;
				// System.out.println(String.format("**********
				// f(%f,%f,%f,%f,%f,%f,%f,%f) = \n", x[0], x[1], x[2], x[3], x[4], x[5], x[6],
				// x[7]));

				return mainForNLOPT.execute((int) x[0], x[1], (int) x[2], x[3], (int) x[4], x[5], x[6], x[7]);
			}
		};

		double[] lb = { 9, 2000, 30, 120, 9, 2000, 105, 75 }; /* lower bounds */
		double[] ub = { 250, 4000, 90, 200, 300, 5000, 135, 95 }; /* upper bounds */

		NLopt optimizer = new NLopt(NLopt.NLOPT_GN_CRS2_LM, 8);
		optimizer.setLowerBounds(lb);
		optimizer.setUpperBounds(ub);
		optimizer.setMinObjective(func);
		//optimizer.setRelativeToleranceOnX(1e-0);
		optimizer.setStopValue(-2.9);
		//implement set Stop on Y function
		
		optimizer.setMaxEval(5000);

		double[] x = { 150, 3000, 60, 160, 150, 3000, 115, 90 }; /* some initial guess */
		x[0] = random(9, 250);
		x[1] = random(2000, 4000);
		x[2] = random(30, 90);
		x[3] = random(120, 200);
		x[4] = random(9, 300);
		x[5] = random(2000, 5000);
		x[6] = random(105, 135);
		x[7] = random(75, 95);

		printWithTime("NLOPT_Start_Time");
		System.out.println(
				"Run,RSIMagnitude,RSILowerThan,SMAMagnitude,VolumeHigherThan,ReentryRSIMagnitude,ReentryRSILowerThan,TakeProfit,StopLoss,StartExecute,EndExecute,NetProfit,AnnualIncrement,TotalTradedVolume");
		NLoptResult minf = optimizer.optimize(x);
		System.out.print(String.format("##########NLOPT found minimum at f(%f,%f,%f,%f,%f,%f,%f,%f) = %f\n", x[0], x[1] / 100,
				x[2], x[3] / 100, x[4], x[5] / 100, x[6] / 100, x[7] / 100, minf.minValue()));

		printWithTime("NLOPT_End_Time");
	}
}
