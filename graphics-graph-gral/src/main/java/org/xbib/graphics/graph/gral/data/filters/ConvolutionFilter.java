package org.xbib.graphics.graph.gral.data.filters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xbib.graphics.graph.gral.util.WindowIterator;

public class ConvolutionFilter<T extends Number & Comparable<T>> implements Filter<T> {

	private final List<Double> filtered;

	private final Iterator<List<T>> windowIterator;

	public ConvolutionFilter(Iterable<T> data, Kernel kernel) {
		filtered = new LinkedList<>();

		windowIterator = new WindowIterator<>(data.iterator(), kernel.size());

		while (windowIterator.hasNext()) {
			List<T> window = windowIterator.next();
			double convolvedValue = 0.0;
			for (int windowIndex = 0; windowIndex < window.size(); windowIndex++) {
				int kernelIndex = windowIndex - kernel.getOffset();
				convolvedValue += kernel.get(kernelIndex)*window.get(windowIndex).doubleValue();
			}
			filtered.add(convolvedValue);
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return filtered.iterator();
	}
}
