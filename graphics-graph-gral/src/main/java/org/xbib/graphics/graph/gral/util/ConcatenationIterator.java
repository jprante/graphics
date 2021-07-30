package org.xbib.graphics.graph.gral.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConcatenationIterator<T> implements Iterator<T> {
	private final Iterator<T>[] inputIterators;

	@SuppressWarnings("unchecked")
	public ConcatenationIterator(Iterator<T>... inputIterators) {
		this.inputIterators = Arrays.copyOf(inputIterators, inputIterators.length);
	}

	@Override
	public boolean hasNext() {
		for (Iterator<T> inputIterator : inputIterators) {
			if (inputIterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public T next() {
		for (Iterator<T> inputIterator : inputIterators) {
			if (inputIterator.hasNext()) {
				return inputIterator.next();
			}
		}
		throw new NoSuchElementException("No elements left in concatenated iterator.");
	}

	@Override
	public void remove() {
	}
}
