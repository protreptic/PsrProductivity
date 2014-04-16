package ru.magnat.sfs.bom.creditrequest;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;

@OrmEntityOwner(owner = DocCreditRequestLine.class)
public class DocCreditRequestLineEntity extends	DocGenericLineEntity<DocCreditRequestLine> {
}
