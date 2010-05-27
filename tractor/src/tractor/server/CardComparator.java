package tractor.server;

import java.util.Comparator;

public interface CardComparator<T> extends Comparator<T> {
	public int gameCompare(T t1, T t2);
}
