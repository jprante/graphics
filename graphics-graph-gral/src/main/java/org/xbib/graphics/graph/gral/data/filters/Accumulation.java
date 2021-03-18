package org.xbib.graphics.graph.gral.data.filters;

import java.util.Iterator;

public class Accumulation<T extends Number & Comparable<T>> implements Filter<T> {

	private final Iterable<T> data;

	private static class AccumulationIterator<U extends Number> implements Iterator<Double> {
		private final Iterator<U> wrappedIterator;
		private double accumulatedValue;

		public AccumulationIterator(Iterator<U> wrappedIterator) {
			this.wrappedIterator = wrappedIterator;
			accumulatedValue = 0.0;
		}

		@Override
		public boolean hasNext() {
			return wrappedIterator.hasNext();
		}

		@Override
		public Double next() {
			accumulatedValue += wrappedIterator.next().doubleValue();
			return accumulatedValue;
		}

		@Override
		public void remove() {
			wrappedIterator.remove();
		}
	}

	public Accumulation(Iterable<T> data) {
		this.data = data;
	}

	@Override
	public Iterator<Double> iterator() {
		return new AccumulationIterator<>(data.iterator());
	}
}
