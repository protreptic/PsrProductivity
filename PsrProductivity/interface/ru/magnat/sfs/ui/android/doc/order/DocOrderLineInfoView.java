package ru.magnat.sfs.ui.android.doc.order;

import java.util.HashMap;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntityView;
import ru.magnat.sfs.bom.reg.tprdiscount.RegTprDiscountEntityView;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.GenericPictureView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class DocOrderLineInfoView extends GenericEntityView<RefProductItem, RefProductItemEntity> {

	final public Float _baseprice;
	final public Float _price;
	final public Float _discount;

	private class DocOrderLineTabContentFactory implements TabHost.TabContentFactory {
		
		private Map<String, SfsContentView> _tabs;
		
		public DocOrderLineTabContentFactory() {
			_tabs = new HashMap<String, SfsContentView>();
		}
		
		public View createTabContent(String tag) {
			SfsContentView v = null;
			if (tag == TABS_TAG_1) {
				v = new RefProductItemEntityView(DocOrderLineInfoView.this._catalog, DocOrderLineInfoView.this._entity,DocOrderLineInfoView.this._baseprice,DocOrderLineInfoView.this._price,DocOrderLineInfoView.this._discount);
			} else if (tag == TABS_TAG_2) {
	//			v = new RegTprDiscountEntityView(DocOrderLineInfoView.this._tpr,DocOrderLineInfoView.this._price);
			} else if (tag == TABS_TAG_3) {
				v = new GenericPictureView(MainActivity.sInstance,DocOrderLineInfoView.this._picture_name);
			}
			if (v != null) {
				v.inflate();
				_tabs.put(tag,v);
			}

			return v;
		}
		public void dispose(){
			RegTprDiscountEntityView v = (RegTprDiscountEntityView) _tabs.get(TABS_TAG_2);
			if (v!=null) v.dispose();
		}
	}
	
	public DocOrderLineInfoView(Context context, RefProductItemEntity entity, DocOrderEntity doc, DocOrderLineEntity docline, Float baseprice,Float price,Float discount) {
		super(context,(RefProductItem) Globals.createOrmObject(RefProductItem.class),entity);
		_baseprice = baseprice;
		_price = price;
		_discount = discount;
		_docline = docline;
		_doc = doc;
		
		//_tpr = doc.getTprDiscount(entity);
		_picture_name = Globals.definePictureName(_entity);
	}
	
	final DocOrderLineEntity _docline;
	final DocOrderEntity _doc;
	//final RegTprDiscountEntity _tpr;
	final String _picture_name;
	final String TABS_TAG_1 = "PRODUCT";
	final String TABS_TAG_2 = "TPR";
	final String TABS_TAG_3 = "PICTURE";
	
	@Override
	public SfsContentView inflate() {
	
		LayoutInflater inflater = (LayoutInflater)  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_line_info_view, this);
		mTabHost = (TabHost) findViewById(R.id.tabHost);
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);
		_tabFactory = new DocOrderLineTabContentFactory();
		
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_1, null, "Товар");
		if (!_picture_name.isEmpty()) {
			Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_3, null, "Фото");
		}

		return this;
	}

	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
	}

	@Override
	public void fill() {}

	@Override
	public void refresh() {}
	
	public void dispose(){
		if (_catalog != null) {
			_catalog.close();
			_catalog = null;
			((DocOrderLineTabContentFactory)_tabFactory).dispose();
		}
	} 
	
	@Override
	public void moveTaskToBack(Boolean handled) {
		dispose();
	}
}
