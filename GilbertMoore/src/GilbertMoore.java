import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;


public class GilbertMoore {

	public static void main(String[] args) {

		String inputFilename = "input.txt";
		String outputFilename = "output.txt";
		
		double[] p = getProbabilitiesFromInputFile(inputFilename);
		int[] y = getCodeLettersFromInputFile(inputFilename);
		List<String> cl = gilbert_moore(p, y);
		
		writeResultToOutputFile(p, y, cl, outputFilename);
	}

	public static double[] getProbabilitiesFromInputFile(String inputFilename) {
		List<String> lines = new ArrayList<>();
		try {
			File file = new File(inputFilename);
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				lines.add(myReader.nextLine());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		String[] temp;
		temp = lines.get(0).replace(" ", "").split("=");
		String[] strp;
		strp = temp[1].split(",");
		double[] p = new double[strp.length];
		for (int i = 0; i < strp.length; i++) {
			p[i] = Double.parseDouble(strp[i]);
		}

		System.out.println("Probabilities:");
		System.out.println(Arrays.toString(p));
		System.out.println();

		return p;
	}
	
	public static int[] getCodeLettersFromInputFile(String inputFilename) {

		
		List<String> lines = new ArrayList<>();
		try {
			File file = new File(inputFilename);
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				lines.add(myReader.nextLine());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		String[] temp;
		temp = lines.get(1).replace(" ", "").split("=");
		String[] strp;
		strp = temp[1].split(",");
		int[] y = new int[strp.length];
		for (int i = 0; i < strp.length; i++) {
			y[i] = Integer.parseInt(strp[i]);
		}
		
		System.out.println("Code Letters:");
		System.out.println(Arrays.toString(y));
		System.out.println();
		
		return y;
		
	}

	public static List<Double> calc_x(double[] p) {
		System.out.println("Calculating x:");
		List<Double> x = new ArrayList<Double>();
		for (int i = 0; i < p.length; i++) {
			double sum = 0;
			for (int j = 0; j < i; j++) {
				sum += p[j];
			}
			x.add((sum + (p[i] / 2)));
		}
		System.out.println(x);
		return x;
	}

	public static List<List<Double>> split_n(List<Double> r, int n) { // Az n a number_of_code_words a forrásból.
		double beg = r.get(0);
		double end = r.get(1);
		System.out.println("for range [" + beg + ", " + end + "]");
		System.out.println("splitting by " + n);
		List<List<Double>> result = new ArrayList<List<Double>>();
		double width = end - beg;
		double step = width / n;
		for (int i = 0; i < n; i++) {
			double a = beg + i * step;
			double b = beg + (i + 1) * step;
			result.add(Arrays.asList(a, b));
		}
		System.out.println("rl = " + result.toString());
		System.out.println();
		return result;
	}

	public static List<Double> match_xlist_range(List<Double> xl, List<Double> list) {
		List<Double> ml = new ArrayList<Double>();
		double beg = list.get(0);
		double end = list.get(1);
		ListIterator<Double> it = xl.listIterator();
		while (it.hasNext()) {
			double current = it.next();
			int index = it.nextIndex();
			System.out.println("for x [" + (index - 1) + "] in range " + list);
			if (current >= beg && current <= end) {
				ml.add(current);
			}
		}
		System.out.println("ml = " + ml);
		System.out.println();
		return ml;
	}

	public static List<String> recursiveFun(List<Double> subxl, double beg, double end, int n, List<String> cl,
			List<Double> xl, int node) {
		node += 1;
		System.out.println("recursive-ness starting node = " + node);
		System.out.println();

		List<Double> ml = Arrays.asList(1.0, 1.0);
		List<Double> begEndList = Arrays.asList(beg, end);
		List<List<Double>> rlist = split_n(begEndList, n);
		ListIterator<List<Double>> it = rlist.listIterator();
		while (it.hasNext()) {
			Integer ri = it.nextIndex();
			List<Double> r = it.next();
			System.out.println("node: " + node + " checking " + subxl + " for range = " + r);
			System.out.println();
			ml = match_xlist_range(subxl, r);
			if (ml.size() == 1) {
				int xi = xl.indexOf(ml.get(0));
				System.out.println("adding code " + ri + " for x = " + ml.get(0) + " of index " + xi);
				cl.set(xi, cl.get(xi) + ri.toString());
				System.out.println("cl = " + cl);
				System.out.println();
			} else if (ml.size() > 1) {
				beg = r.get(0);
				end = r.get(1);
				for (int m = 0; m < ml.size(); m++) {
					Integer xi = xl.indexOf(ml.get(m));
					System.out.println("adding code " + ri + " for x = " + ml.get(m) + " index of " + xi);
					cl.set(xi, cl.get(xi) + ri.toString());
					System.out.println("cl = " + cl);
					System.out.println();
				}
				recursiveFun(ml, beg, end, n, cl, xl, node);
			} else {
				System.out.println("ml = " + ml);
				System.out.println();
			}
		}
		return cl;
	}

	public static List<String> gilbert_moore(double[] p, int[] y) {
		int n = y.length;
		List<Double> xl = calc_x(p);
		List<String> cl = new ArrayList<>();
		for (int i = 0; i < xl.size(); i++) {
			cl.add("");
		}
		System.out.println("cl = " + cl);
		System.out.println();
		cl = recursiveFun(xl, 0, 1, n, cl, xl, 0);
		System.out.println("result:");
		for (int i = 0; i < p.length; i++) {
			System.out.println("p[" + i + "] code = " + cl.get(i));
		}
		return cl;
	}

	public static void writeResultToOutputFile(double[] p, int[] y, List<String> code, String outputFilename) {
		try {
			FileWriter myWriter = new FileWriter(outputFilename);
			myWriter.write("p = " + Arrays.toString(p) + "\n\n");
			myWriter.write("y = " + Arrays.toString(y) + "\n\n");
			myWriter.write("Gilbert Moore Coding:" + "\n\n");
			for (int i = 0; i < p.length; i++) {
				myWriter.write("p[" + i + "] code = " + code.get(i) + "\n");
			}
			myWriter.write("\n");

			double entropy = 0;
			double L = 0;
			for (int i = 0; i < p.length; i++) {
				entropy += p[i] * (Math.log(p[i]) / Math.log(2));
				L += p[i] * code.get(i).length();
			}
			entropy = -entropy;
			double efficiency = entropy / L * (Math.log(y.length) / Math.log(2));

			System.out.println("Entropy value: " + entropy);
			System.out.println("Efficiency value: " + efficiency);
			myWriter.write("Entropy value: " + entropy + "\n");
			myWriter.write("Efficiency value: " + efficiency);

			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
