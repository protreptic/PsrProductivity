package ru.magnat.sfs.bom.order;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class DocOrderLine extends DocGenericLine<DocOrderLineEntity>
/*
 * implements OnLineQuantityChangedListener, OnLineUnitChangedListener,
 * OnLineAmountChangedListener, OnLineAmountBaseChangedListener
 */
{

	public DocOrderLine(Context context, DocOrderEntity owner) {
		super(context, DocOrderLineEntity.class, owner);

	}

	public enum Units {
		Piece, Case, Block
	}

	@Override
	public final DocOrderLineEntity getLine(Context context,
			GenericEntity<?> entity) {
		if (entity == null)
			return null;
		return getLine(context,entity.Id);
		}
	
	public final DocOrderLineEntity getLine(Context context,
			Long productItem) {
		if (productItem == null)
			return null;

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		//criteria.add(new SqlCriteria("MasterDocId", _id));
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		//criteria.add(new SqlCriteria("MasterDocAuthor", _author));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author.Id));
		criteria.add(new SqlCriteria("Item", productItem));
		if (this.Select(criteria))
			if (this.Next())
				return this.Current();

		return null;
	}

	public DocOrderLineEntity Current() {
		DocOrderLineEntity entity = (DocOrderLineEntity) super.Current();
		// entity.setOnLineQuantityChangedListener(this);
		// entity.setOnLineUnitChangedListener(this);
		// entity.setOnLineAmountChangedListener(this);
		// entity.setOnLineAmountBaseChangedListener(this);

		return entity;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	/*
	 * public void onLineQuantityChanged(GenericEntity sender, long old_value,
	 * long new_value) { SFSActivity.Me.recalcOrderLine.execute(new
	 * Object[]{sender,((DocOrderEntity)_owner).getTradeRule()}); save(); }
	 */
	/*
	 * 
	 * public void onLineAmountBaseChanged(GenericEntity sender, float
	 * old_value, float new_value) { save();
	 * //SFSActivity.Me.recalcOrder.execute(new
	 * Object[]{sender,(DocOrderEntity)_owner});
	 * 
	 * }
	 */
	/*
	 * public void onLineAmountChanged(GenericEntity sender, float old_value,
	 * float new_value) { Dialogs.MessageBox(_context,
	 * Float.toString(new_value)); save(); }
	 */

	/*
	 * public void onLineUnitChanged(GenericEntity sender, Units old_value,
	 * Units new_value) { SFSActivity.Me.recalcOrderLine.execute(new
	 * Object[]{sender,((DocOrderEntity)_owner).getTradeRule()}); save(); }
	 * 
	 * @Override public DocOrderLineEntity next() { return (DocOrderLineEntity)
	 * super.next(); }
	 * 
	 * @Override public SfsContentView GetExtendedListItemView(ListView lv) { //
	 *  return null; }
	 */

}
