package ru.magnat.sfs.ui.android.task.visit;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class VisitTypeTab extends SfsContentView {

	private TaskVisitEntity mTaskVisitEntity;

	public VisitTypeTab(Context context, TaskVisitEntity visit) {
		super(context);
		
		mTaskVisitEntity = visit;
		layoutInflater.inflate(R.layout.visit_type_tab, this);
		
		TextView caption = (TextView) findViewById(R.id.caption_text);
		caption.setText(visit.toString());
		
		EditText comment = (EditText) findViewById(R.id.editComment);
		comment.setText(mTaskVisitEntity.Goal);
		comment.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			public void afterTextChanged(Editable s) {
				mTaskVisitEntity.Goal = s.toString();
				mTaskVisitEntity.save();
			}
		});
		
		RadioGroup result = (RadioGroup) findViewById(R.id.resultSelector);
		result.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.radioResultative: {
						mTaskVisitEntity.IsCompleted = true;
					} break;
					case R.id.radioNonResultative: {
						mTaskVisitEntity.IsCompleted = false;
					} break;
				}
				mTaskVisitEntity.save();	
			}
		});
		
		RadioGroup type = (RadioGroup) findViewById(R.id.typeSelector);
		type.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.radioOrder: {
						mTaskVisitEntity.setVisitType(0);
					} break;
					case R.id.radioFinance: {
						mTaskVisitEntity.setVisitType(1);
					} break;
					case R.id.radioDocuments: {
						mTaskVisitEntity.setVisitType(2);
					} break;
					case R.id.radioShelving: {
						mTaskVisitEntity.setVisitType(3);
					} break;
				}
				mTaskVisitEntity.save();
			}
		});
		
		refresh();
	}

	@Override
	public void refresh() {
		switch (mTaskVisitEntity.VisitType) {
			case 0: {
				((RadioGroup) findViewById(R.id.typeSelector)).check(R.id.radioOrder);
			} break;
			case 1: {
				((RadioGroup) findViewById(R.id.typeSelector)).check(R.id.radioFinance);
			} break;
			case 2: {
				((RadioGroup) findViewById(R.id.typeSelector)).check(R.id.radioDocuments);
			} break;
			case 3: {
				((RadioGroup) findViewById(R.id.typeSelector)).check(R.id.radioShelving);
			} break;
		}

		((RadioGroup) findViewById(R.id.resultSelector)).check((mTaskVisitEntity.IsCompleted) ? R.id.radioResultative : R.id.radioNonResultative);
	}

}
