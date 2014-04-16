package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.CommandWithParameters;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.contract.QueryContractorListView;
import ru.magnat.sfs.ui.android.doc.creditrequest.DocRequestOnChangeCreditListView;
import ru.magnat.sfs.ui.android.doc.onchangeroute.DocRequestOnChangeRouteListView;
import ru.magnat.sfs.ui.android.doc.requestonchangecontractor.DocRequestOnChangeContractorListView;
import ru.magnat.sfs.ui.android.doc.requestonchangeoutlet.DocRequestOnChangeOutletListView;
import ru.magnat.sfs.ui.android.doc.requestoutlet.DocRequestOutletListView;
import ru.magnat.sfs.util.Loader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DataView extends SfsContentView {

	private DataAdapter mAdapter;
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private ListView mListView;
	
	public DataView(Context context) {
		super(context);

		layoutInflater.inflate(R.layout.list_layout, this);
		
		mAdapter = new DataAdapter();
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (parent instanceof ListView) {
					Map<String, Object> item = mData.get(position);
					Command cmd = (Command) item.get("command");
					if (cmd != null) {
						cmd.execute();
					}
				}

			}

		});
		
		new LoadData(context).execute();
	}

	private class LoadData extends Loader {
		
		public LoadData(Context context) {
			super(context);
		}

		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();
			
			mData.add(createMenuItem("Стандартные договора", "Управление стандартными договорами КА", mOpenContractors));
			mData.add(createMenuItem("Задолженности", "Управление задолженностями КА", mOpenDebts));
			mData.add(createMenuItem("Дополнительные материалы", "Фотографии новинок, инициативные цели, инструменты и т.п.", mOpenExtras));
			mData.add(createMenuItem("Заказы", "Посмотреть список заказов", mOpenOrders));
			mData.add(createMenuItem("Маркетинговые измерения", "Посмотреть список маркетинговых измерений", mOpenMeasures));
			mData.add(createMenuItem("TPR-акции", "Управление TPR-акциями КА", mOpenTprMeasure));
			mData.add(createMenuItem("Контрагенты", "Управление сведениями о КА", mOpenRequestContractor));
			mData.add(createMenuItem("Торговые точки", "Управление физическими торговыми точками КА", mOpenRequestOutlet));
			mData.add(createMenuItem("Потенциальные торговые точки", "Управление потенциальными торговыми точками КА", mOpenRequestPotentialOutlet));
			mData.add(createMenuItem("Изменить маршрут", "Управление маршрутом ТП", mOpenRequestRoute));
			mData.add(createMenuItem("Кредитные параметры", "Управление кредитными параметрами КА", mOpenRequestCredit));
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {		
			mAdapter.notifyDataSetChanged();
			
			super.onPostExecute(result); 
		}
		
		private Map<String, Object> createMenuItem(String label, String descr, Object cmd) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("label", label);
			map.put("description", descr);
			map.put("command", cmd);
			
			return map;
		}
		
		private void openItem(SfsContentView v) {
			mCloseView.execute();
			mCloseView.getParameters().put("view", v);
			MainActivity.getInstance().addToFlipper(v, this.getClass().getSimpleName());
			MainActivity.getInstance().addOnBackPressedListener(new OnBackPressedListener() {
				public Boolean onBackPressed() {
					mCloseView.execute();
					return true;
				}

			});
		}
		
		private CommandWithParameters mCloseView = new CommandWithParameters() {
			private Map<String, Object> _parms = new HashMap<String, Object>();

			public void execute() {
				MainActivity.getInstance().removeFromFlipper((View) _parms.get("view"));
			}

			public Map<String, Object> getParameters() {
				return _parms;
			}
		};
		
		private Command mOpenOrders = new Command() {
			public void execute() {
				openItem(new OrdersView(getContext(), false));
			}
		};
		
		private Command mOpenContractors = new Command() {
			@Override
			public void execute() {
				openItem(new QueryContractorListView(getContext()));
			}
		};
		 
		private Command mOpenDebts = new Command() {
			public void execute() {
				openItem(new BalanceView(getContext()));
			}
		};
		
		private Command mOpenMeasures = new Command() {
			public void execute() {
//				openItem(new QueryMarketingMeasureListView(getContext()));
				openItem(new GoldenMeasureView(getContext(), false));
			}  
		};
		private Command mOpenRequestContractor = new Command() {
			public void execute() {
				openItem(new DocRequestOnChangeContractorListView(getContext(), MainActivity.getInstance().mCurrentWorkday));
			}
		};
		private Command mOpenRequestRoute = new Command() {
			public void execute() {
				openItem(new DocRequestOnChangeRouteListView(getContext(), MainActivity.getInstance().mCurrentWorkday));
			}
		};
		private Command mOpenRequestOutlet = new Command() {
			public void execute() {
				openItem(new DocRequestOnChangeOutletListView(getContext(), MainActivity.getInstance().mCurrentWorkday));
			}
		};
		private Command mOpenRequestPotentialOutlet = new Command() {
			public void execute() {
				openItem(new DocRequestOutletListView(getContext(), MainActivity.getInstance().mCurrentWorkday));
			}
		};
		private Command mOpenRequestCredit = new Command() {
			public void execute() {
				openItem(new DocRequestOnChangeCreditListView(getContext(), MainActivity.getInstance().mCurrentWorkday));
			}
		};
		private Command mOpenExtras = new Command(){
			public void execute(){
				Globals.openExtras();
			}
		};
		private Command mOpenTprMeasure = new Command() {
			@Override
			public void execute() {
				//openItem(new QueryTprMeasureListView(getContext()));
				openItem(new TprMeasuresView(getContext(), false));
			}
		};
		
	}
	
	private static class ViewHolder {
		TextView text1;
		TextView text2;
	}
	
	private class DataAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position); 
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.two_line_list_item, null);
				
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.first_line);
				holder.text2 = (TextView) convertView.findViewById(R.id.second_line);
				
				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text1.setText((String) mData.get(position).get("label"));
			holder.text2.setText((String) mData.get(position).get("description"));
			
			return convertView;
		}
		
	}

}
