package org.xbib.graphics.graph.gral.data.filters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xbib.graphics.graph.gral.data.statistics.Statistics;
import org.xbib.graphics.graph.gral.util.WindowIterator;

public class MedianFilter<T extends Number & Comparable<T>> implements Filter<T> {

	private final List<Double> filtered;

	private final Iterator<List<T>> windowIterator;

	public MedianFilter(Iterable<T> data, int windowSize) {
		filtered = new LinkedList<>();

		windowIterator = new WindowIterator<>(data.iterator(), windowSize);

		while (windowIterator.hasNext()) {
			List<T> window = windowIterator.next();
			Statistics windowStatistics = new Statistics(window);
			double median = windowStatistics.get(Statistics.MEDIAN);
			filtered.add(median);
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return filtered.iterator();
	}
}
