package ru.magnat.sfs.bom.generic;

import java.io.Closeable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.generic.OnDocumentChangedListener.OnMarkedListener;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;

public abstract class DocGenericEntity<T extends DocGeneric<? extends DocGenericEntity<T, L>, L>, L extends DocGenericLineEntity<? extends DocGenericLine<L>>>

		extends GenericEntity<T>
implements Closeable
{

	@OrmEntityField(DisplayName = "Пользователь", isPrimary = 1, fields = "Author")
	public RefUserEntity Author;
	@OrmEntityField(DisplayName = "Торговый агент", isPrimary = 0, fields = "Employee")
	public RefEmployeeEntity Employee;
	@OrmEntityField(DisplayName = "Основание", isPrimary = 0, fields = "MasterDoc")
	public DocGenericEntity<?, ?> MasterDoc;
	@OrmEntityField(DisplayName = "Задача", isPrimary = 0, fields = "MasterTask")
	public TaskGenericEntity<?, ?> MasterTask;
	@EntityCardField(DisplayName = "черновик", Sortkey = 1, SelectMethod = "changeIsMark")
	@OrmEntityField(DisplayName = "Удален", isPrimary = 0, fields = "IsMark")
	public Boolean IsMark;
	@OrmEntityField(DisplayName = "Проведен", isPrimary = 0, fields = "IsAccepted")
	public Boolean IsAccepted;
	@EntityCardField(DisplayName = "время создания", Sortkey = 1, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Дата создания", isPrimary = 0, fields = "CreateDate")
	public Date CreateDate;
	@OrmEntityField(DisplayName = "Номер", isPrimary = 0, fields = "DocNo")
	public String DocNo;

	public Boolean getReadOnly() {
		if (IsMark)
			return false;
		if (!IsAccepted)
			return false;
		return true;
	}

	abstract protected Class<?> getLinesContainer();

	abstract public void setDefaults(Context context, GenericEntity<?> owner);

	protected DocGenericLine<L> _lines;

	@SuppressWarnings("unchecked")
	public DocGenericLine<L> getLines(Context context) {
		boolean need = false;
		if (_lines != null) {
			if (this != _lines.getOwner()) {
				_lines.close();
				need = true;
			}
		} else
			need = true;

		if (need)
			_lines = (DocGenericLine<L>) Globals.createKidOrmObject(this.getLinesContainer(), this);
		return _lines;
	}

	protected void onClose() {
		if (_lines != null)
			_lines.close();
	}

	public void setMarked(Boolean val) {
		if (val.equals(IsMark)) return;
		IsMark = val;
		onMarked(IsMark, val);
	}

	private final Set<IEventListener> _eventMarkedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnMarkedListener(OnMarkedListener eventListener) {
		EventListenerSubscriber.setListener(_eventMarkedListeners,
				eventListener);
	}

	public void addOnMarkedListener(OnMarkedListener eventListener) {
		EventListenerSubscriber.addListener(_eventMarkedListeners,
				eventListener);
	}

	public void removeOnAMarkedListener(OnMarkedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventMarkedListeners,
				eventListener);
	}

	public void onMarked(Boolean old_value, Boolean new_value) {
		if (save())
			for (IEventListener eventListener : _eventMarkedListeners)
				((OnMarkedListener) eventListener).onMarked(this, old_value,
						new_value);
	}
	public void close() {
		if (_lines!=null) _lines.close();
		
	}
}
