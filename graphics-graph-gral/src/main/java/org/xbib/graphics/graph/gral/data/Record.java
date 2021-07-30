package org.xbib.graphics.graph.gral.data;

import static java.util.Arrays.copyOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Record implements Iterable<Comparable<?>> {

	private final Comparable<?>[] values;

	@SuppressWarnings("rawtypes")
	public Record(List<? extends Comparable<?>> values) {
		this.values = values.toArray(new Comparable[0]);
	}

	public Record(Comparable<?>... values) {
		this.values = copyOf(values, values.length);
	}

	@SuppressWarnings("unchecked")
	public <T extends Comparable<?>> T get(int index) {
		return (T) values[index];
	}

	public int size() {
		return values.length;
	}

	@Override
	public Iterator<Comparable<?>> iterator() {
		// More readable version using Arrays.asList is prevented by broken Generics system
		List<Comparable<?>> list = new ArrayList<>(values.length);
		Collections.addAll(list, values);
		return list.iterator();
	}

	public boolean isNumeric(int index) {
		return values[index] instanceof Number;
	}


	public Record insert(Comparable<?> value, int position) {
		List<Comparable<?>> recordCopyAsList = new ArrayList<>(values.length + 1);
		Collections.addAll(recordCopyAsList, values);
		recordCopyAsList.add(position, value);
		return new Record(recordCopyAsList);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Record)) {
			return false;
		}
		Record record = (Record) obj;
		return size() == record.size() && Arrays.equals(this.values, record.values);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(values);
	}

	@Override
	public String toString() {
		StringBuilder representation = new StringBuilder("(");
		for (int elementIndex = 0; elementIndex < values.length; elementIndex++) {
			Comparable<?> element = values[elementIndex];
			representation.append(element);
			if (elementIndex != values.length - 1) {
				representation.append(", ");
			}
		}
		representation.append(")");
		return representation.toString();
	}

}
