package ekon.dominion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MyList<T extends Comparable<T>> implements Iterable<T> {

	private List<T> items;
	
	@SafeVarargs
	public MyList(T... item) { this(Arrays.<T>asList(item)); }
	public MyList(List<T> itemsToAdd) { items = new ArrayList<T>(itemsToAdd); }
	public MyList(MyList<T> list) { this(list.items); }
	public MyList(int initialCapacity) { items = new ArrayList<T>(initialCapacity); }
	
	public List<T> asList() { return items; }
	protected List<T> items() { return items; }
	
	public void add(T item) { items.add(item); }
	public void add(MyList<T> newMyList) { items.addAll(newMyList.asList()); }
	public void add(Collection<T> newMyList) { items.addAll(newMyList); }
	
	public boolean remove(T item) { return items.remove(item); }
	public boolean remove(MyList<T> toRemove) {
		boolean allRemoved = true;
		for (T item : toRemove.asList()) {
			allRemoved =  remove(item) ? allRemoved : false;
		}
		return allRemoved;
	}
	public boolean remove(Collection<T> toRemove) {
		boolean allRemoved = true;
		for (T item : toRemove) {
			allRemoved =  remove(item) ? allRemoved : false;
		}
		return allRemoved;
	}
	
	public T get(int index) { return items.get(index); }
	
	public int size() { return items.size(); }
	public boolean contains(T item) { return items.contains(item); }
	
	public void shuffle() { Collections.shuffle(items); }

	@Override
	public Iterator<T> iterator() { return items.iterator(); }
	
	@Override
	public String toString() { return items.toString(); }

	@Override
	public int hashCode() { return items.hashCode(); }

	@Override
	public boolean equals(Object obj) {
		// Don't care about sorting!
		Collections.<T>sort(items);
		if ((obj != null) && (obj.getClass().equals(this.getClass()))) {
			@SuppressWarnings("unchecked")
			List<T> otherMyList = ((MyList<T>)obj).asList();
			Collections.sort(otherMyList);
			return items.equals(otherMyList);
		}
		return false;
	}
}
