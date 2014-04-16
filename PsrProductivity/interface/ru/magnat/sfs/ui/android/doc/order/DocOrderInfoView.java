package ru.magnat.sfs.ui.android.doc.order;

import java.util.HashMap;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.view.BalanceView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class DocOrderInfoView extends GenericEntityView<DocOrderJournal, DocOrderEntity> {

	final String TABS_TAG_1 = "HEADER";
	final String TABS_TAG_2 = "DEBTS";
	final String TABS_TAG_3 = "TIME";
	
	private class DocOrderTabContentFactory implements TabHost.TabContentFactory {

		private DocOrderEntity _doc;
		private DocOrderJournal _catalog;
		private Map<String, SfsContentView> _tabs;
		
		public DocOrderTabContentFactory(DocOrderJournal catalog, DocOrderEntity doc) {
			_doc = doc;
			_catalog = catalog;
			_tabs = new HashMap<String, SfsContentView>();
		}

		public View createTabContent(String tag) {
			SfsContentView v = null;
			Context context = MainActivity.getInstance();
			if (tag == TABS_TAG_1) {
				v = new DocOrderHeaderTab(context, _catalog, _doc);
			} else if (tag == TABS_TAG_2) {
				v = new BalanceView(context, _doc.Contractor);
			}

			if (v != null) {
				_tabs.put(tag, v.inflate());
			}

			return v;
		}
	}

	public DocOrderInfoView(Context context, DocOrderJournal catalog, DocOrderEntity doc) {
		super(context, catalog, doc);
	}
	
	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_info_view, this);
		mTabHost = (TabHost) findViewById(R.id.tabHost);
		mTabHost.setup();
		if (_entity != null) {
			_tabFactory = new DocOrderTabContentFactory(_catalog, _entity);
			
			Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_1, null, "Условия");
			if (_entity.Contractor != null) {
				Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_2, null, "Задолженности");
			}

			mTabHost.setOnTabChangedListener(this);
		}
		
		return this;
	}

	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
	}
}
