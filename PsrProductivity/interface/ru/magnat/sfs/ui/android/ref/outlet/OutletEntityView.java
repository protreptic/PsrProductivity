package ru.magnat.sfs.ui.android.ref.outlet;

import android.content.Context;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.SimpleGenericEntityView;

public class OutletEntityView extends SimpleGenericEntityView<RefOutlet, RefOutletEntity> {

	public TaskWorkdayEntity currentWorkday;

	public OutletEntityView(Context context, RefOutletEntity entity) {
		super(context, entity);
	}

}
