SELECT
    convert(varchar(20), t.StartDate, 14) as StartTime,
    convert(varchar(20), t.EndDate, 14) as EndTime,
    cast(t.StartDate as date) as StartDate,
    t.StartDate,
    t.CreateDate,
    t.IsCompleted,
    o.Descr,
    o.Address,
    o.LocationLat,
    o.LocationLon,
    o.Id,
    s.Descr
FROM TaskVisitJournal AS t 
    INNER JOIN RefOutlet AS o ON
        t.Outlet = o.Id
    LEFT JOIN RefStoreType AS s ON
        o.StoreType = s.Id
WHERE 
    t.IsMark = 0 AND o.IsMark = 0 AND t.Author = 43
    AND DateDiff(day, '2013-02-14', StartDate) = 0 
ORDER BY 
    StartTime
