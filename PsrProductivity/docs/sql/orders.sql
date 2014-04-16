SELECT 
    I.Id,
    I.IsMark AS 'Черновик',
    I.ExtId AS 'Код из 1С',
    I.CreateDate AS 'Дата заказа',
    I.LastModified AS 'Последние изменения',
    I.ShipmentDate AS 'Дата доставки',
    I.Author AS 'Код ТП', 
    U.Descr AS 'ФИО ТП', 
    I.Amount AS 'Сумма заказа', 
    O.Descr AS 'Заказчик', 
    O.Address AS 'Адрес точки'
FROM 
    DocOrderJournal AS I
    INNER JOIN RefUser AS U ON 
        U.Id = I.Author 
    AND U.TestUser <> 0
    INNER JOIN RefOutlet AS O ON 
        I.Outlet = o.Id 
WHERE 
//        I.IsMark <> 0 // Не черновик
//    AND 
    I.CreateDate > '2013-02-14' 
    AND I.Amount > 1000 
    AND I.Author = 43 // Мыцкин (Оренбург)
    //AND U.Parent = 98 Оренбург
    //AND I.Author = 60 // Половинкин Вячеслав Анатольевич (Екатеринбург)
    //AND I.Author = 106 // Изюмская Анна Владимировна (Оренбург)
    //AND I.Author = 145 // Муратшина Рушания Рявкатевна
    //AND I.Author = 137 // Балтаева Гульчечак Халиловна (Перьм)
    //AND I.Author = 155 // Фатхутдинов Ильдар Рафитович (Екатеринбург)
    //AND I.Author = 130 // Вологжанина Евгения Алексеевна (Пермь)
    //AND I.Author = 55  // Козлова Татьяна Александровна (Астрахань)
    //AND I.Author = 148 // Миронов Алексей (Екатеринбург)
    //AND I.Author = 53  // Клочкова Анастасия Ивановна (Свиридова Марина)
    //AND I.Author = 51  // Казарян Надежда Казаиновна
ORDER BY 
    I.ExtId DESC, 
    I.Amount DESC