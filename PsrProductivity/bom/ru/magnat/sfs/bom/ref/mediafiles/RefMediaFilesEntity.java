package ru.magnat.sfs.bom.ref.mediafiles;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.gcasstate.RefGcasStateEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefMediaFiles.class)
public final class RefMediaFilesEntity extends
		RefGenericEntity<RefMediaFiles, RefMediaFilesEntity> {
	@OrmEntityField(DisplayName = "��� �����", isPrimary = 0, fields = "FIleName")
	@EntityCardField(DisplayName = "��������", Sortkey = 1, SelectMethod = "showFileName")
	public String FileName;
	@EntityCardField(DisplayName = "������ ��������", Sortkey = 2, SelectMethod = "")
	@OrmEntityField(DisplayName = "������ ��������", isPrimary = 0, fields = "BeginOfActivity")
	public Date BeginOfActivity;
	@EntityCardField(DisplayName = "��������� ��������", Sortkey = 3, SelectMethod = "")
	@OrmEntityField(DisplayName = "��������� ��������", isPrimary = 0, fields = "EndOfActivity")
	public Date EndOfActivity;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "Seq")
	public long Seq;
	@EntityCardField(DisplayName = "������ (����)", Sortkey = 4, SelectMethod = "")
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "FileSize")
	public long FileSize;
	
	public boolean isExists() {
		if (FileName==null)
			return false;
		return Globals.findDownloadedFile(FileName);
	}
}
