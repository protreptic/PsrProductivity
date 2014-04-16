SELECT 
    I.Id,
    I.IsMark AS '��������',
    I.ExtId AS '��� �� 1�',
    I.CreateDate AS '���� ������',
    I.LastModified AS '��������� ���������',
    I.ShipmentDate AS '���� ��������',
    I.Author AS '��� ��', 
    U.Descr AS '��� ��', 
    I.Amount AS '����� ������', 
    O.Descr AS '��������', 
    O.Address AS '����� �����'
FROM 
    DocOrderJournal AS I
    INNER JOIN RefUser AS U ON 
        U.Id = I.Author 
    AND U.TestUser <> 0
    INNER JOIN RefOutlet AS O ON 
        I.Outlet = o.Id 
WHERE 
//        I.IsMark <> 0 // �� ��������
//    AND 
    I.CreateDate > '2013-02-14' 
    AND I.Amount > 1000 
    AND I.Author = 43 // ������ (��������)
    //AND U.Parent = 98 ��������
    //AND I.Author = 60 // ���������� �������� ����������� (������������)
    //AND I.Author = 106 // �������� ���� ������������ (��������)
    //AND I.Author = 145 // ��������� ������� ����������
    //AND I.Author = 137 // �������� ��������� ��������� (�����)
    //AND I.Author = 155 // ����������� ������ ��������� (������������)
    //AND I.Author = 130 // ����������� ������� ���������� (�����)
    //AND I.Author = 55  // ������� ������� ������������� (���������)
    //AND I.Author = 148 // ������� ������� (������������)
    //AND I.Author = 53  // �������� ��������� �������� (��������� ������)
    //AND I.Author = 51  // ������� ������� ����������
ORDER BY 
    I.ExtId DESC, 
    I.Amount DESC