package org.xbib.graphics.graph.gral.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Iterables {
	private static class ConcatenationIterable<T> implements Iterable<T> {
		private final Iterable<Iterable<T>> inputIterables;

		public ConcatenationIterable(Iterable<Iterable<T>> inputIterables) {
			this.inputIterables = inputIterables;
		}

		@SuppressWarnings({"unchecked","rawtypes"})
		@Override
		public Iterator<T> iterator() {
			List<Iterator<T>> iterators = new LinkedList<>();
			for (Iterable<T> iterable : inputIterables) {
				iterators.add(iterable.iterator());
			}
			return new ConcatenationIterator<T>(iterators.toArray(new Iterator[0]));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> concatenate(Iterable<T>... iterables) {
		return new ConcatenationIterable<>(Arrays.asList(iterables));
	}

	private static class LengthIterator<T> implements Iterator<T> {
		private final Iterator<T> inputIterator;
		private final int maxElementCount;
		private int retrievedElementCount;

		public LengthIterator(Iterator<T> inputIterator, int elementCount) {
			this.inputIterator = inputIterator;
			this.maxElementCount = elementCount;
		}

		@Override
		public boolean hasNext() {
			return retrievedElementCount < maxElementCount && inputIterator.hasNext();
		}

		@Override
		public T next() {
			retrievedElementCount++;
			return inputIterator.next();
		}

		@Override
		public void remove() {
			inputIterator.remove();
		}
	}

	public static <T> Iterable<T> take(final Iterable<T> iterable, final int elementCount) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new LengthIterator<>(iterable.iterator(), elementCount);
			}
		};
	}
}
