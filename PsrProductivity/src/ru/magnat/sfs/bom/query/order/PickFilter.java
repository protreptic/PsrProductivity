package ru.magnat.sfs.bom.query.order;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;

public class PickFilter implements Cloneable {
	
	protected RefCskuEntity Product = null;
	protected boolean InStock = false;
	protected boolean Recommended = false;
	protected boolean OnlyListA = false;
	protected boolean OnlyListC = false;
	protected boolean OnlyPriority = false;
	protected boolean OnlyInitiative = false;
	protected boolean OnlyTpr = false;
	protected boolean OnlyTemporary = false;
	protected boolean OnlyKits = false;
	
	public PickFilter() {
		Product = null;
		InStock = false;
		Recommended = false;
		OnlyListA = false;
		OnlyListC = false;
		OnlyPriority = false;
		OnlyInitiative = false;
		OnlyTpr = false;
		OnlyTemporary = false;
		OnlyKits = false;
	}
	public PickFilter(RefCskuEntity product, boolean inStock, boolean recommended, boolean listA, boolean listC, boolean priority, boolean initiative, boolean tpr, boolean temporary, boolean isKits) {
		Product = product;
		InStock = inStock;
		Recommended = recommended;
		this.OnlyListA = listA;
		this.OnlyListC = listC;
		this.OnlyPriority = priority;
		this.OnlyInitiative = initiative;
		this.OnlyTpr = tpr;
		OnlyTemporary = temporary;
		OnlyKits = isKits;
	}
	@Override
	public PickFilter clone() {
		return new PickFilter(this.Product,this.InStock,this.Recommended, this.OnlyListA, this.OnlyListC, this.OnlyPriority, this.OnlyInitiative, this.OnlyTpr, OnlyTemporary, OnlyKits);
	}
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof PickFilter)) return false;
		PickFilter pf = (PickFilter) o;
		if (Product==null && pf.Product!=null) return false;
		if (Product!=null && pf.Product==null) return false;
		if (Product!=null && !Product.equals(pf.Product)) return false;		
		if (pf.InStock!=InStock) return false;
		if (pf.Recommended!=Recommended) return false;
		if (pf.OnlyListA!=this.OnlyListA) return false;
		if (pf.OnlyListC!=this.OnlyListC) return false;
		if (pf.OnlyPriority!=this.OnlyPriority) return false;
		if (pf.OnlyInitiative!=this.OnlyInitiative) return false;
		if (pf.OnlyTpr!=this.OnlyTpr) return false;
		if (pf.OnlyTemporary != OnlyTemporary) return false;
		if (pf.OnlyKits != OnlyKits) return false;
		return true;
	}
	
	public RefCskuEntity getProduct() {
		return Product;
	}
	
	public void setProduct(RefCskuEntity val) {
		if (val==null && Product==null) return;
		if (val!=null && val.equals(Product)) return;
		Product = val;
		onFilterChanged();
	}
	public boolean getKits() {
		return OnlyKits;
	}
	
	public void setKits(boolean val) {
		if (OnlyKits==val) return;
		OnlyKits = val;
		onFilterChanged();
	}
	
	public boolean getTemporary() {
		return OnlyTemporary;
	}
	
	public void setTemporary(boolean val) {
		if (OnlyTemporary==val) return;
		OnlyTemporary = val;
		onFilterChanged();
	}
	
	public boolean getInStock() {
		return InStock;
	}
	
	public void setInStock(boolean val) {
		if (InStock==val) return;
		InStock = val;
		onFilterChanged();
	}
	
	public boolean getRecommended() {
		return Recommended;
	}
	
	public boolean getOnlyListA() {
		return this.OnlyListA;
	}
	
	public void setOnlyListA(boolean val) {
		this.OnlyListA = val;
	}
	
	public boolean getOnlyListC() {
		return this.OnlyListC;
	}
	
	public void setOnlyListC(boolean val) {
		this.OnlyListC = val;
	}
	
	public boolean getOnlyPriority() {
		return this.OnlyPriority;
	}
	public void setOnlyPriority(boolean val) {
		this.OnlyPriority = val;
	}
	public boolean getOnlyInitiative() {
		return this.OnlyInitiative;
	}
	public void setOnlyInitiative(boolean val) {
		this.OnlyInitiative = val;
	}
	
	public boolean getOnlyTpr() {
		return this.OnlyTpr;
	}
	public void setOnlyTpr(boolean val) {
		this.OnlyTpr = val;
	}
	public void setRecommnded(boolean val) {
		if (Recommended==val) return;
		Recommended = val;
		onFilterChanged();
	}
	public void setFilter(PickFilter filter) {
		if (equals(filter)) return;
		Product = filter.Product;
		InStock = filter.InStock;
		Recommended = filter.Recommended;
		this.OnlyListA = filter.OnlyListA;
		this.OnlyListC = filter.OnlyListC;
		this.OnlyPriority = filter.OnlyPriority;
		this.OnlyInitiative = filter.OnlyInitiative;
		this.OnlyTpr = filter.OnlyTpr;
		OnlyTemporary = filter.OnlyTemporary;
		OnlyKits = filter.OnlyKits;
		onFilterChanged();
	}
	private final Set<IEventListener> _eventFilterChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnFilterChanged(OnPickFilterChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventFilterChangedListeners,
				eventListener);
	}

	public void addOnFilterChanged(OnPickFilterChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventFilterChangedListeners,
				eventListener);
	}
	public void removeOnFilterChanged(OnPickFilterChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventFilterChangedListeners,
				eventListener);
	}
	public void onFilterChanged() {
		for (IEventListener eventListener : _eventFilterChangedListeners)
			((OnPickFilterChangedListener) eventListener).onFilterChanged(this);
	}
	
}
