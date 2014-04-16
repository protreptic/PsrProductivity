package ru.magnat.sfs.bom.requestoutlet;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;

@OrmEntityOwner(owner = DocRequestOutletLine.class)
public class DocRequestOutletLineEntity extends
		DocGenericLineEntity<DocRequestOutletLine> {
}
